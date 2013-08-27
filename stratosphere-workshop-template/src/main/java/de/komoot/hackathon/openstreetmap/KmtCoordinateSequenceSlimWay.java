package de.komoot.hackathon.openstreetmap;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.CoordinateSequence;
import com.vividsolutions.jts.geom.Envelope;

import java.util.List;

/** @author richard */
public class KmtCoordinateSequenceSlimWay extends AbstractKmtCoordinateSequence {
	/** automatically generated Logger statement */
	// private final static org.slf4j.Logger LOGGER =
	// org.slf4j.LoggerFactory.getLogger(CoordinateSlimWaySequence.class);

	private OsmNode[] slimNodes;

	/** @param slimNodes Must not be null and not be empty. */
	public KmtCoordinateSequenceSlimWay(OsmNode[] slimNodes) {
		if(slimNodes == null || slimNodes.length == 0) {
			throw new IllegalArgumentException("SlimNodes must not be null and not be empty!");
		}
		this.slimNodes = slimNodes;
	}

	/** @param slimNodes Must not be null and not be empty. */
	public KmtCoordinateSequenceSlimWay(List<OsmNode> slimNodes) {
		if(slimNodes == null || slimNodes.size() == 0) {
			throw new IllegalArgumentException("SlimNodes must not be null and not be empty!");
		}
		this.slimNodes = slimNodes.toArray(new OsmNode[slimNodes.size()]);
	}

	@Override
	public Envelope expandEnvelope(Envelope env) {
		for(OsmNode node : slimNodes) {
			env.expandToInclude(node.getCoordinate());
		}
		return env;
	}

	@Override
	public Coordinate getCoordinate(int i) {
		OsmNode nodeAtIndex = slimNodes[i];
		return nodeAtIndex.getCoordinate();
	}

	@Override
	public int size() {
		return slimNodes.length;
	}

	@Override
	public Coordinate[] toCoordinateArray() {
		Coordinate[] coordArray = new Coordinate[slimNodes.length];
		for(int i = 0; i < slimNodes.length; i++) {
			coordArray[i] = slimNodes[i].getCoordinate();
		}
		return coordArray;
	}

	public CoordinateSequence clone() {
		return new KmtCoordinateSequenceSlimWay(slimNodes);
	}
}
