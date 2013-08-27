package de.komoot.hackathon.openstreetmap;

import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.MultiPolygon;

import java.util.List;
import java.util.Map;

/**
 *
 * @author jan
 */
public class OsmAreaImpl extends OsmEntityImpl<MultiPolygon> implements OsmArea {

	private final SOURCE source;
	private final List<PolygonMember> polygonMembers;

	public OsmAreaImpl(long osmId, List<PolygonMember> polygonMembers, SOURCE source, GeometryFactory factory) {
		super(osmId, OsmAreaUtils.createGeometry(factory, polygonMembers));
		this.source = source;
		this.polygonMembers = polygonMembers;
	}

	@Override
	public SOURCE getSource() {
		return source;
	}

	public List<PolygonMember> getPolygonMembers() {
		return polygonMembers;
	}

	@Override
	public String getId() {
		if(source == SOURCE.WAY) {
			return "Aw" + getOsmId();
		} else if(source == SOURCE.RELATION) {
			return "Ar" + getOsmId();
		} else {
			throw new AssertionError("Unexpected SOURCE: " + source.toString());
		}

	}
}

