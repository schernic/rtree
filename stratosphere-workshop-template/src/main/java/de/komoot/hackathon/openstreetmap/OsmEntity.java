package de.komoot.hackathon.openstreetmap;

import com.vividsolutions.jts.geom.Geometry;

public interface OsmEntity<T extends Geometry> extends GeometryContainer<T>, TagContainer {

	/** @return the openstreetmap id of the source record in the openstreetmap database. Not unique across Nodes, Ways and Relations. */
	long getOsmId();

	/**
	 * Unique id across all nodes, ways and relations
	 * @return a unique String identifier
	 */
	String getId();
}