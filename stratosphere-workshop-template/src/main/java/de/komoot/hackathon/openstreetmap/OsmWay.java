package de.komoot.hackathon.openstreetmap;

import com.vividsolutions.jts.geom.LineString;

import java.util.List;

public interface OsmWay extends OsmEntity<LineString>{

	List<OsmNode> getWayNodes();

}