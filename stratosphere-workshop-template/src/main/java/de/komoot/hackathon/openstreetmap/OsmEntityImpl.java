package de.komoot.hackathon.openstreetmap;

import com.google.common.base.Preconditions;
import com.vividsolutions.jts.geom.Geometry;

import java.util.Collections;
import java.util.Map;

/**
 * @author jan
 * @date 23.08.13
 */
public abstract class OsmEntityImpl<T extends Geometry> implements OsmEntity<T> {

	private final long osmId;

	private final T geometry;

	protected OsmEntityImpl(long osmId, T geometry) {
		Preconditions.checkNotNull(geometry);
		this.osmId = osmId;
		this.geometry = geometry;
	}

	public long getOsmId() {
		return osmId;
	}

	public T getGeometry() {
		return geometry;
	}

	public Map<String, String> getTags() {
		return Collections.emptyMap();
	}
}
