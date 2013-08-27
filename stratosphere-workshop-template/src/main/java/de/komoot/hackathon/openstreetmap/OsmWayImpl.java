package de.komoot.hackathon.openstreetmap;

import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;

import java.util.List;
import java.util.Map;

/**
 * @author jan
 */
public class OsmWayImpl extends OsmEntityImpl<LineString> implements OsmWay {

	private final List<OsmNode> wayNodes;

	public OsmWayImpl(long osmId, List<OsmNode> wayOsmNodes, GeometryFactory factory) {
		super(osmId,factory.createLineString(new KmtCoordinateSequenceSlimWay(wayOsmNodes)));
		this.wayNodes = wayOsmNodes;
	}

	public List<OsmNode> getWayNodes() {
		return wayNodes;
	}

	@Override
	public String getId() {
		return "W" + getOsmId();
	}
}
