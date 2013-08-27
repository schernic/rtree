package de.komoot.hackathon.openstreetmap;

import com.vividsolutions.jts.geom.Geometry;

public interface OsmRelation extends OsmEntity<Geometry> {

	public abstract long[] getMembers();

}