package de.komoot.hackathon.openstreetmap;

import com.vividsolutions.jts.geom.MultiPolygon;

public interface OsmArea extends OsmEntity<MultiPolygon>{

	public enum SOURCE {WAY, RELATION};
	
	public SOURCE getSource();

}