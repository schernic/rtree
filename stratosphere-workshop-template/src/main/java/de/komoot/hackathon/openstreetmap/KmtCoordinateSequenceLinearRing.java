package de.komoot.hackathon.openstreetmap;

import com.vividsolutions.jts.geom.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * * A {@link CoordinateSequence} used by {@link OsmArea} to create its
 * {@link Geometry}s, which is made of {@link LinearRing}s. This implementation
 * saves only references to {@link OsmWay}s which make up the {@link OsmArea}.
 * The {@link Coordinate}s are created on the fly.
 *
 * @author richard
 */
public class KmtCoordinateSequenceLinearRing extends AbstractKmtCoordinateSequence {
	/** automatically generated Logger statement */
	private final static org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(KmtCoordinateSequenceLinearRing.class);
	private List<OsmWay> OsmWays;
	private List<CoordinateSequence> coordinateSequences;

	/**
	 * Constructor.
	 *
	 * @param memberWays Must not be null and not empty.
	 */
	public KmtCoordinateSequenceLinearRing(List<OsmWay> memberWays) {
		if(memberWays == null || memberWays.size() == 0) {
			throw new IllegalArgumentException("Memberways must not be null and not be empty!");
		}
		this.OsmWays = memberWays;

		coordinateSequences = collectCoordinateSequencesForRing();
	}

	/**
	 * @return
	 * @throws IllegalArgumentException when ring is not closed. no ring is created
	 */
	private List<CoordinateSequence> collectCoordinateSequencesForRing() throws IllegalArgumentException {
		List<CoordinateSequence> coordSequence = new ArrayList<CoordinateSequence>();
		Map<Long, List<OsmWay>> wayIndex = generateWayIndex(OsmWays);
		// start with first node
		long nextNode = wayIndex.keySet().iterator().next();
		OsmWay w;

		while(wayIndex.size() > 0) {
			// get Way
			if(!wayIndex.containsKey(nextNode)) {
				throw new IllegalArgumentException("Cannot create Ring. its not closed!");
			}
			w = wayIndex.get(nextNode).iterator().next();

			// get startnode of way
			long candidate = w.getWayNodes().get(0).getOsmId();
			boolean reverse = true; //TODO: could be nice but I didn't want to change too many lines
			// if we come from the direction of this node. continue with endnode
			if(candidate == nextNode) {
				candidate = w.getWayNodes().get(w.getWayNodes().size() - 1).getOsmId();
				reverse = false;
			}
			removeUsedEntries(wayIndex, nextNode, w, candidate);
			// add the way data we found to the sequence
			if(reverse) {
				coordSequence.add(new ReverseOrderCoordinateSequenceDecorator(w.getGeometry().getCoordinateSequence()));
			} else {
				coordSequence.add(w.getGeometry().getCoordinateSequence());
			}
			// proceed with next node
			nextNode = candidate;
		}
		// validate result
		Coordinate firstCoordinate = coordSequence.get(0).getCoordinate(0);
		Coordinate lastCoordinate = coordSequence.get(coordSequence.size() - 1).getCoordinate(coordSequence.get(coordSequence.size() - 1).size() - 1);
		if(!firstCoordinate.equals(lastCoordinate)) {
			throw new IllegalArgumentException("Cannot create Ring. its not closed!");
		}
		return coordSequence;
	}

	/**
	 * remove way from the index. every way is referenced two times
	 *
	 * @param wayIndex
	 * @param nextNode
	 * @param way
	 * @param candidate
	 */
	private void removeUsedEntries(Map<Long, List<OsmWay>> wayIndex, long nextNode, OsmWay way, long candidate) {
		wayIndex.get(nextNode).remove(way);
		wayIndex.get(candidate).remove(way);

		if(wayIndex.containsKey(nextNode) && wayIndex.get(nextNode).isEmpty()) {
			wayIndex.remove(nextNode);
		}
		if(wayIndex.containsKey(candidate) && wayIndex.get(candidate).isEmpty()) {
			wayIndex.remove(candidate);
		}
	}

	/**
	 * generate an index with nodeid of start or endpoint as key, ways that
	 * contain that node as values. every way is referenced two times!
	 *
	 * @param OsmWays
	 * @return
	 */
	private Map<Long, List<OsmWay>> generateWayIndex(List<OsmWay> OsmWays) {
		Map<Long, List<OsmWay>> coordinateIndex = new HashMap<Long, List<OsmWay>>();
		for(OsmWay w : OsmWays) {
			long startNode = w.getWayNodes().get(0).getOsmId();
			long endNode = w.getWayNodes().get(w.getWayNodes().size() - 1).getOsmId();

			// generate index for start node
			if(coordinateIndex.containsKey(startNode)) {
				coordinateIndex.get(startNode).add(w);
			} else {
				// new entry
				ArrayList<OsmWay> list = new ArrayList<OsmWay>();
				list.add(w);
				coordinateIndex.put(startNode, list);
			}
			// generate index for end node
			if(coordinateIndex.containsKey(endNode)) {
				coordinateIndex.get(endNode).add(w);
			} else {
				// new entry
				ArrayList<OsmWay> list = new ArrayList<OsmWay>();
				list.add(w);
				coordinateIndex.put(endNode, list);
			}
		}
		return coordinateIndex;
	}

	@Override
	public Envelope expandEnvelope(Envelope env) {
		Coordinate[] coords = toCoordinateArray();
		for(Coordinate c : coords) {
			env.expandToInclude(c);
		}
		return env;
	}

	@Override
	public Coordinate getCoordinate(int index) {
		int visitedCoordinates = 0;
		int cumulativeSizeOfCurrentSequence = 0;
		for(int i = 0; i < coordinateSequences.size(); i++) {
			CoordinateSequence currentSequence = coordinateSequences.get(i);
			cumulativeSizeOfCurrentSequence += currentSequence.size();
			if(i > 0) {
				cumulativeSizeOfCurrentSequence--;
			}
			/* if we are in the correct sequence... */
			if(index < cumulativeSizeOfCurrentSequence) {
				/* get correct index in sequence */
				int indexInCorrectSeq = index - visitedCoordinates;
				/* if we are not in the first sequence, hide first coordinate */
				if(i > 0) {
					indexInCorrectSeq++;
				}
				return currentSequence.getCoordinate(indexInCorrectSeq);
			} else {
				visitedCoordinates += currentSequence.size();
				if(i > 0) {
					visitedCoordinates--;
				}
			}
		}
		throw new RuntimeException("Not valid index: " + index);
	}

	@Override
	public int size() {
		int size = 0;
		for(OsmWay way : OsmWays) {
			size += way.getWayNodes().size() - 1;
		}
		size++;
		return size;
	}

	@Override
	public Coordinate[] toCoordinateArray() {
		Coordinate[] coordinates = new Coordinate[size()];
		int index = 0;
		for(int j = 0; j < coordinateSequences.size(); j++) {
			int size = coordinateSequences.get(j).size();
			/*
			 * if we are not in the first sequence: hide first coordinate -->
			 * start at index 1
			 */
			int i = 0;
			if(j > 0) {
				i = 1;
			}
			for(; i < size; i++) {
				coordinates[index] = coordinateSequences.get(j).getCoordinate(i);
				index++;
			}
		}
		return coordinates;
	}

	public CoordinateSequence clone() {
		return new KmtCoordinateSequenceLinearRing(OsmWays);
	}
}
