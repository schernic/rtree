package de.komoot.hackathon.openstreetmap;

/**
 * Specifies osm relationtypes and their identifier.
 * @author richard
 *
 */
public enum RelationType {

	MUTLIPOLYGON("multipolygon"), ROUTE("route"), RESTRICTION("restriction"), BOUNDARY("boundary"), UNKOWN("unknown");

	private final String osmTypeIdentifier;

	RelationType(String typeIdentifier) {
		this.osmTypeIdentifier = typeIdentifier;
	}

	public String getOsmTypeIdentifier() {
		return osmTypeIdentifier;
	}
}
