package de.komoot.hackathon.openstreetmap;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Point;

public interface OsmNode extends OsmEntity<Point>{

	/**
	 * Convenience method returns the Point's coordinate
	 * @return
	 */
	Coordinate getCoordinate();
}