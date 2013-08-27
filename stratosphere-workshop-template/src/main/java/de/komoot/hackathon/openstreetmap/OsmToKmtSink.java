package de.komoot.hackathon.openstreetmap;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.PrecisionModel;
import de.komoot.hackathon.EntityValidator;
import de.komoot.hackathon.EntityValidator.AreaValidationState;
import de.komoot.hackathon.OsmConstants;
import de.komoot.hackathon.openstreetmap.OsmArea.SOURCE;
import org.openstreetmap.osmosis.core.container.v0_6.*;
import org.openstreetmap.osmosis.core.domain.v0_6.*;
import org.openstreetmap.osmosis.core.task.v0_6.Sink;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.*;

/** @author richard */
public class OsmToKmtSink implements Sink, EntityProcessor {

	/** automatically generated Logger statement */
	private final static org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(OsmToKmtSink.class);
	private final static Map<String, String> EMPTY_TAGS = Collections.unmodifiableMap(new HashMap<String, String>());
	private final static GeometryFactory GEOMETRY_FACTORY = new GeometryFactory(new PrecisionModel(), 4326);
	private final ObjectMapper mapper;
	private final List<OsmNode> nodes;
	private final List<OsmWay> ways;
	private final BufferedWriter nodeswriter;
	private final BufferedWriter lineswriter;
	private final BufferedWriter areaswriter;

	public OsmToKmtSink(int initialnodessize, BufferedWriter nodeswriter, BufferedWriter lineswriter, BufferedWriter areaswriter) {
		this.nodes = new ArrayList<OsmNode>(initialnodessize);
		this.ways = new ArrayList<OsmWay>(initialnodessize / 10);
		this.nodeswriter = nodeswriter;
		this.lineswriter = lineswriter;
		this.areaswriter = areaswriter;
		mapper = new ObjectMapper();
		mapper.registerModule(new GeometryModule());
		mapper.configure(JsonGenerator.Feature.AUTO_CLOSE_TARGET, false);
	}

	/**
	 * binary-searches a {@link OsmNode} in a {@link List} by id.
	 * Implementation taken from Collections.binarySearch.
	 *
	 * @param list
	 * @param osmId
	 * @return the {@link OsmNode} with the given osmId or null
	 */
	public static <T extends OsmEntity<?>> T binarySearchIn(List<T> list, long osmId) {
		int low = 0;
		int high = list.size() - 1;

		while(low <= high) {
			int mid = (low + high) >>> 1;
			T midVal = list.get(mid);
			int cmp = (osmId < midVal.getOsmId() ? -1 : (osmId == midVal.getOsmId() ? 0 : 1));

			if(cmp > 0) {
				low = mid + 1;
			} else if(cmp < 0) {
				high = mid - 1;
			} else {
				return midVal; // key found
			}
		}
		return null; // key not found
	}

	private static void write(String id, Geometry geometry, Map<String, String> tags, Writer writer, ObjectMapper mapper) {
		try {
			JsonGeometryEntity<Geometry> g = new JsonGeometryEntity<Geometry>(id, geometry, tags);
			mapper.writeValue(writer, g);
			writer.write('\n');
		} catch(IOException e) {
			throw new IllegalStateException("IOException occured when writing entity " + id, e);
		}
	}

	@Override
	public void initialize(Map<String, Object> arg0) {

	}

	@Override
	public void complete() {

	}

	@Override
	public void release() {
	}

	@Override
	public void process(EntityContainer entityContainer) {
		entityContainer.process(this);
	}

	@Override
	public void process(BoundContainer arg0) {

	}

	@Override
	public void process(NodeContainer nodeContainer) {
		Node node = nodeContainer.getEntity();
		if(!EntityValidator.validate(node)) {
			return;
		}
		if(nodes.size() > 0) {
			long lastId = nodes.get(nodes.size() - 1).getOsmId();
			if(node.getId() <= lastId) {
				throw new IllegalStateException("Tried to add osm node with id " + node.getId() + " but last imported id was larger: " + lastId);
			}
		}

		Map<String, String> tags = convertTagsToMap(node.getTags());
		Coordinate c = new Coordinate(node.getLongitude(), node.getLatitude());
		OsmNode osmNode = new OsmNodeImpl(node.getId(), GEOMETRY_FACTORY.createPoint(c));
		nodes.add(osmNode);
		write(osmNode.getId(), osmNode.getGeometry(), tags, nodeswriter, mapper);
	}

	@Override
	public void process(WayContainer wayContainer) {
		Way way = wayContainer.getEntity();

		/*
		 * We can be sure that blocks (nodes, ways, relations) are sorted
		 * (http://wiki.openstreetmap.org/wiki/OSM_XML#Assumptions)
		 */
		List<OsmNode> osmNodes = new ArrayList<OsmNode>(way.getWayNodes().size());
		boolean useWay = true;
		for(WayNode wayN : way.getWayNodes()) {
			OsmNode node = searchNode(wayN.getNodeId());
			if(node != null) {
				osmNodes.add(node);
			} else {
				/* If node cannot be found in osm file, dont use the way. */
				useWay = false;
				break;
			}
		}

		/*
		 * First add all ways to ways. Needed when relations are created. Filter
		 * afterwards for polylines.
		 */
		if(useWay && EntityValidator.doesWayHaveValidNumberOfNodes(osmNodes)) {
			if(ways.size() > 0) {
				long lastId = ways.get(ways.size() - 1).getOsmId();
				if(way.getId() <= lastId) {
					throw new IllegalStateException("Tried to add osm way with id " + way.getId() + " but last imported id was larger: " + lastId);
				}
			}
			Map<String, String> tags = convertTagsToMap(way.getTags());
			OsmWay osmWay = new OsmWayImpl(way.getId(), osmNodes, GEOMETRY_FACTORY);
			ways.add(osmWay);
			writeWayOrArea(osmWay, tags);
		}
	}

	@Override
	public void process(RelationContainer relationContainer) {
		Relation relation = relationContainer.getEntity();
		Collection<Tag> tags = relation.getTags();
		RelationType relationType = determineRelationType(tags);
		if(relationType == RelationType.MUTLIPOLYGON || relationType == RelationType.BOUNDARY) {
			processMultiPolygonRelation(relation);
		}
	}

	private Map<String, String> convertTagsToMap(Collection<Tag> tags) {
		if(tags.isEmpty()) {
			// more efficient to re-use empty map
			return Collections.emptyMap();
		} else {
			Map<String, String> newMap = Maps.newHashMapWithExpectedSize(tags.size());
			for(Tag t : tags) {
				String key = t.getKey();
				String value = t.getValue();
				newMap.put(key, value);
			}
			return newMap;
		}
	}

	private OsmNode searchNode(long osmID) {
		return binarySearchIn(nodes, osmID);
	}

	private OsmWay searchWay(long osmID) {
		return binarySearchIn(ways, osmID);
	}

	/**
	 * Determines relation type
	 *
	 * @param tags
	 * @return
	 */
	private RelationType determineRelationType(Collection<Tag> tags) {
		for(Tag t : tags) {
			if(t.getKey().equals(OsmConstants.TYPE)) {
				if(t.getValue().equals(RelationType.MUTLIPOLYGON.getOsmTypeIdentifier())) {
					return RelationType.MUTLIPOLYGON;
				}
				if(t.getValue().equals(RelationType.BOUNDARY.getOsmTypeIdentifier())) {
					return RelationType.BOUNDARY;
				}
			}
		}
		return RelationType.UNKOWN;
	}

	private void processMultiPolygonRelation(Relation relation) {
		List<PolygonMember> polygonMembers = buildMultipolygon(relation);
		if(polygonMembers != null) {
			Map<String, String> tags = convertTagsToMap(relation.getTags());
			try {
				OsmArea osmArea = new OsmAreaImpl(relation.getId(), polygonMembers, SOURCE.RELATION, GEOMETRY_FACTORY);
				AreaValidationState areaValidationState = EntityValidator.validate(osmArea);
				if(areaValidationState == AreaValidationState.VALID) {
					write(osmArea.getId(), osmArea.getGeometry(), tags, areaswriter, mapper);
				}
			} catch(RuntimeException e) {
				// ignore area, geometry broken
			}
		}
	}

	private List<PolygonMember> buildMultipolygon(Relation relation) {
		// create Multipolygon
		List<PolygonMember> polygonMembers = new ArrayList<PolygonMember>();
		List<RelationMember> relationMembers = relation.getMembers();
		boolean useArea = true;
		/* Collect and mark relation members as inner or outer members. */
		for(RelationMember r : relationMembers) {
			if(r.getMemberType() == EntityType.Way) {

				Long wayId = r.getMemberId();

				OsmWay memberWay = searchWay(wayId);
				if(memberWay == null || r.getMemberRole() == null) {
					return null;
				} else {
					if(r.getMemberRole().equals(OsmConstants.OUTER)) {
						polygonMembers.add(new PolygonMember(memberWay, PolygonMember.PolygonMemberType.OUTER));
					} else if(r.getMemberRole().equals(OsmConstants.INNER)) {
						polygonMembers.add(new PolygonMember(memberWay, PolygonMember.PolygonMemberType.INNER));
					} else {
						// if role is not set, us outer
						polygonMembers.add(new PolygonMember(memberWay, PolygonMember.PolygonMemberType.OUTER));
					}
				}
			}
		}

		return polygonMembers;
	}

	/**
	 * Marks {@link OsmWay} as way, if it is used as a way/polyline.
	 * {@link OsmWay} not representing a polyline are not deleted, because they
	 * are needed to build up the (multi-)polygons.
	 */
	private void writeWayOrArea(OsmWay osmWay, Map<String, String> tags) {
		if(WayHelper.isArea(osmWay, tags)) {
			PolygonMember member = new PolygonMember(osmWay, PolygonMember.PolygonMemberType.OUTER);
			try {
				OsmArea osmArea = new OsmAreaImpl(osmWay.getOsmId(), Arrays.asList(member), SOURCE.WAY, GEOMETRY_FACTORY);
				AreaValidationState areaValidationState = EntityValidator.validate(osmArea);
				if(areaValidationState == AreaValidationState.VALID) {
					write(osmArea.getId(), osmArea.getGeometry(), tags, areaswriter, mapper);
				}
			} catch(RuntimeException e) {
				// ignore area., it is not valid
			}
		}

		if(WayHelper.isUsedAsOsmWay(osmWay, tags)) {
			write(osmWay.getId(), osmWay.getGeometry(), tags, lineswriter, mapper);
		}
	}
}
