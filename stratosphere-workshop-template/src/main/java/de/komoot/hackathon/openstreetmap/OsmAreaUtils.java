package de.komoot.hackathon.openstreetmap;

import com.vividsolutions.jts.geom.*;
import de.komoot.hackathon.openstreetmap.PolygonMember.PolygonMemberType;
import org.springframework.util.Assert;

import java.util.*;

public class OsmAreaUtils {
	private final static org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(OsmAreaUtils.class);

	public static MultiPolygon createGeometry(GeometryFactory factory, PolygonMember... polygonMembers) {
		Assert.notNull(polygonMembers, "argument must not be null");
		return createGeometry(factory, Arrays.asList(polygonMembers));
	}

	public static MultiPolygon createGeometry(GeometryFactory factory, List<PolygonMember> polygonMembers) {

		if (polygonMembers == null) {
			return null;
		}

		List<Polygon> polygons = new ArrayList<Polygon>();

		// all not yet closed ways are in queue
		List<OsmWay> wayQueue = new ArrayList<OsmWay>();
		LinearRing[] holes = null;
		// all not yet committed inner holes are in queue
		List<LinearRing> holesQueue = new ArrayList<LinearRing>();
		LinearRing shell = null;

		PolygonMemberType lastMemberType = null;

		for (PolygonMember pMember : polygonMembers) {

			wayQueue.add(pMember.getOsmWay());
			
			if (isClosedRing(wayQueue)) { //canditate for commit - else just collect more ways 
				LinearRing ring = createLinearRing(factory, wayQueue);
				wayQueue = new ArrayList<OsmWay>(); // fresh queue
				
				if (readyToCommit(lastMemberType, pMember.getType(), shell)) {
					holes = holesQueue.toArray(new LinearRing[holesQueue.size()]);
					polygons.add(new Polygon(shell, holes, factory));
					holes = null;
					shell = null;
					holesQueue = new ArrayList<LinearRing>(); //fresh queue
					wayQueue = new ArrayList<OsmWay>(); // fresh queue
				}
				
				if (pMember.getType() == PolygonMemberType.OUTER) {
					shell = ring;
				} else {
					holesQueue.add(ring);
				}
				lastMemberType = pMember.getType();
			}
			
		}
		if (shell != null) { // commit the last vaild polygon
			holes = holesQueue.toArray(new LinearRing[holesQueue.size()]);
			polygons.add(new Polygon(shell, holes, factory));
		}
		if(!wayQueue.isEmpty()){//
			LOGGER.debug("uncomitted - open rings - left");
			return null;
		}
		if(polygons.isEmpty()){
			LOGGER.debug("no valid multipolygon possible - eg only inner rings");
			return null;
		}
		Polygon[] polys = polygons.toArray(new Polygon[polygons.size()]);
		MultiPolygon mp = new MultiPolygon(polys, factory);
		return mp;
	}

	/**
	 * determines if a polygon is created
	 * 
	 * @param lastMemberType
	 * @param currentMemberType
	 * @param shell
	 * @return
	 */
	static boolean readyToCommit(PolygonMemberType lastMemberType, PolygonMemberType currentMemberType, LinearRing shell) {
		// nothing to commit
		if (shell == null) {
			return false;
		}
		// new outer ring after some inner rings
		if (lastMemberType == PolygonMemberType.INNER && currentMemberType == PolygonMemberType.OUTER) {
			return true;
		}
		// new outer ring after another outer rings
		if (lastMemberType == PolygonMemberType.OUTER && currentMemberType == PolygonMemberType.OUTER) {
			return true;
		}

		return false;
	}

	static boolean isClosedRing(List<OsmWay> ways) {

		/**
		 * we count the number of nodes, that are in more than one way as
		 * connections. if number of connections is same as number of ways, its a
		 * circle (a star has less)
		 */
		int connections = 0;
		Map<Long, Integer> nodeIds = new HashMap<Long, Integer>();
		for (OsmWay way : ways) {
			long firstNodeId = way.getWayNodes().get(0).getOsmId();

			if (nodeIds.containsKey(firstNodeId)) {
				nodeIds.put(firstNodeId, nodeIds.get(firstNodeId) + 1);
			} else {
				nodeIds.put(firstNodeId, 1);
			}

			long lastNodeId = way.getWayNodes().get(way.getWayNodes().size() - 1).getOsmId();
			if (nodeIds.containsKey(lastNodeId)) {
				nodeIds.put(lastNodeId, nodeIds.get(lastNodeId) + 1);
			} else {
				nodeIds.put(lastNodeId, 1);
			}
		}

		//you might better iterate over the values() or entrySet() (more efficient)
		for (long id : nodeIds.keySet()) {
			if (nodeIds.get(id) > 1) {
				connections++;
			}
		}

		if (ways.size() == connections) {
			return true;
		} else {
			return false;
		}

	}

	private static LinearRing createLinearRing(GeometryFactory factory, List<OsmWay> waysOfRing) {
		try {
			CoordinateSequence coordSeq = new KmtCoordinateSequenceLinearRing(waysOfRing);
			return new LinearRing(coordSeq, factory);
		} catch (IllegalArgumentException e) {
			throw new RuntimeException("Could not create linear ring" + e);
		}
	}

	public static void bufferGeometry(MultiPolygon area, double distance) {
		Geometry bufferedGeom = area.buffer(distance);

		if (bufferedGeom instanceof MultiPolygon) {
			area = (MultiPolygon) bufferedGeom;
		} else if (bufferedGeom instanceof Polygon) {
			Polygon[] poly = { (Polygon) bufferedGeom };
			area = new MultiPolygon(poly, bufferedGeom.getFactory());
		} else {
			throw new RuntimeException("Cannot buffer geometry!");
		}
	}

	public static List<PolygonMember> getPolygonMembersByType(OsmAreaImpl area, PolygonMemberType type) {
		return getPolygonMembersByType(area.getPolygonMembers(), type);
	}

	public static List<PolygonMember> getPolygonMembersByType(List<PolygonMember> members, PolygonMemberType type) {
		List<PolygonMember> result = new ArrayList<PolygonMember>();
		for (PolygonMember p : members) {
			if (p.getType().equals(type)) {
				result.add(p);
			}
		}
		return result;
	}

}
