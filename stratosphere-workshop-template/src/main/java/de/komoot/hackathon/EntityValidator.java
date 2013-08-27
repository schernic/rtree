package de.komoot.hackathon;

import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import de.komoot.hackathon.openstreetmap.OsmArea;
import de.komoot.hackathon.openstreetmap.OsmNode;
import org.openstreetmap.osmosis.core.domain.v0_6.Node;

import java.util.List;

public class EntityValidator {
	// /** automatically generated Logger statement */
	// private final static org.slf4j.Logger LOGGER =
	// org.slf4j.LoggerFactory.getLogger(EntityValidator.class);

	public final static int MIN_LAT_FOR_UTM = -80;
	public final static int MAX_LAT_FOR_UTM = 84;
	public final static int MIN_LON_FOR_UTM = -180;
	public final static int MAX_LON_FOR_UTM = 180;
	private final static int MIN_WGS_LAT = -90;
	private final static int MAX_WGS_LAT = 90;
	private final static int MIN_WGS_LON = -180;
	private final static int MAX_WGS_LON = 180;

	/**
	 * Checks if {@link Node}'s coordinate values are in a valid WGS84 range.
	 *
	 * @param node
	 * @return
	 */
	public static boolean validate(Node node) {
		if(!isInRange(node.getLongitude(), MIN_WGS_LON, MAX_WGS_LON)) {
			return false;
		}
		if(!isInRange(node.getLatitude(), MIN_WGS_LAT, MAX_WGS_LAT)) {
			return false;
		}
		return true;
	}

	private static boolean isInRange(double value, int lower, int upper) {
		if(value >= lower && value <= upper) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Checks if the list contains a valid number of nodes for ways. Valid
	 * number of nodes means at least two nodes.
	 *
	 * @param nodes
	 * @return
	 */
	public static boolean doesWayHaveValidNumberOfNodes(List<OsmNode> nodes) {
		if(nodes.size() < 2) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * Checks if {@link OsmArea} has valid geometry. A valid geometry is valid
	 * according to the OGC SFS specification, is non-empty, and has a valid,
	 * non-null centroid.
	 * <p/>
	 * TODO: if invalid we repair it with jts bufferGeometry
	 *
	 * @param area
	 * @return
	 */
	public static AreaValidationState validate(OsmArea area) {
		MultiPolygon mp = area.getGeometry();
		if(mp == null) {
			return AreaValidationState.EMPTY;
		}
		if(mp.isEmpty()) {
			return AreaValidationState.EMPTY;
		}
		if(mp.isValid() == false) {
			return AreaValidationState.INVALID;
		}
		if(!hasValidCentroid(mp)) {
			return AreaValidationState.INVALID_CENTROID;
		}
		return AreaValidationState.VALID;
	}

	/** Validates the area, specifically its centroid. */
	private static boolean hasValidCentroid(MultiPolygon mp) {
		Point centroid = mp.getCentroid();
		if(centroid == null) {
			return false;
		}
		double lon = centroid.getX();
		if(!isInRange(lon, MIN_LON_FOR_UTM, MAX_LON_FOR_UTM)) {
			return false;
		}
		double lat = centroid.getY();
		if(!isInRange(lat, MIN_LAT_FOR_UTM, MAX_LAT_FOR_UTM)) {
			return false;
		}
		return true;
	}

	public enum AreaValidationState {
		VALID, INVALID, INVALID_CENTROID, EMPTY, UNKOWN;
	}
}
