/**
 * 
 */
package de.komoot.hackathon.openstreetmap;

import com.vividsolutions.jts.geom.Geometry;

/**
 * Marker interface for classes containing a main geometry
 * 
 * @author jan
 */
public interface GeometryContainer<T extends Geometry> {

	public T getGeometry();

}
