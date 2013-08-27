package de.komoot.hackathon.openstreetmap;

/**
 * Represents a member of a multi-polygon, which describes a {@link OsmWay}. A
 * {@link OsmWay} consists at least of one {@link PolygonMember}. Every
 * {@link PolygonMember} has a role (outer or inner), which describes its usage
 * in the {@link OsmWay} (compare with multi-polygon).
 *
 * @author richard
 */
public class PolygonMember {
	// /** automatically generated Logger statement */
	// private final static org.slf4j.Logger LOGGER =
	// org.slf4j.LoggerFactory.getLogger(PolygonMember.class);

	private OsmWay way;
	;
	private PolygonMemberType type;

	public PolygonMember(OsmWay way, PolygonMemberType type) {
		this.way = way;
		this.type = type;
	}

	public OsmWay getOsmWay() {
		return way;
	}

	public PolygonMemberType getType() {
		return type;
	}

	/**
	 * Membertype: member of outer or inner ring
	 *
	 * @author richard
	 */
	public enum PolygonMemberType {
		OUTER, INNER
	}
}
