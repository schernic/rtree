package de.komoot.hackathon;

import com.google.common.collect.ImmutableMap;

import java.util.*;

public interface OsmConstants {

	String ADMINLEVEL = "admin_level";
	String POSTCODE = "addr:postcode";
	String BUILDING = "building";
	String ADDRHOUSENUMBER = "addr:housenumber";
	String ADDRSTREET = "addr:street";
	String ADDRPOSTCODE = "addr:postcode";
	String ADDRCOUNTRY = "addr:country";
	String ADDRCITY = "addr:city";
	String NAME = "name";
	String ADMIN_LEVEL = "admin_level";
	String POSTAL_CODE = "postal_code";
	String HISTORIC = "historic";
	String ADDR_INTERPOLATION = "addr:interpolation";
	String OSM_ID = "osm_id";
	String PLACE = "place";
	String ISLAND = "island";
	String CAPITAL = "capital";
	String PLACE_TYPE = "place_type";
	String ZIP = "zip";
	String BOUNDARY = "boundary";
	String ADMINISTRATIVE = "administrative";
	String MONUMENT = "monument";
	String LANDUSE = "landuse";
	String FOREST = "forest";
	String RAILWAY = "railway";
	String STATION = "station";
	String HIGHWAY = "highway";
	String SAC_SCALE = "sac_scale";
	String MTB_SCALE = "mtb:scale";
	String SURFACE = "surface";
	String TRACKTYPE = "tracktype";
	String TERTIARY = "tertiary";
	String PATH = "path";
	String MOTORWAY = "motorway";
	String FERRY = "ferry";
	String TRUNK = "trunk";
	String COUNTRY = "country";
	String STATE = "state";
	String COUNTY = "county";
	String CITY = "city";
	String TOWN = "town";
	String VILLAGE = "village";
	String BOROUGH = "borough";
	String LOCALITY = "locality";

	String TYPE = "type";
	String OUTER = "outer";
	String INNER = "inner";
	String ROUTE = "route";
	
	String BICYCLE = "bicycle";
	String HIKING = "hiking";
	String MTB = "mtb";
	
	String AREA = "area";
	String NO = "no";
	String YES = "yes";
	List<String> KEYSDESCRIBINGAREA = Arrays.asList("aeroway", "building", "landuse", "leisure", "natural", "place");
	List<String> KEYSDESCRIBINGCLOSEDPOLYLINE = Arrays.asList("highway", "barrier");
	String[] ROUTEVALUES = {"ferry", "bicycle", "hiking", "mtb", "ski"};
	Map<String, String[]> TAGSDESCRIBINGCLOSEDPOLYLINE = ImmutableMap.of("route", ROUTEVALUES);
	String WILDCARD = "*";
	Map<String, String> TAGS_DEFINING_WAY_MAP = Collections.unmodifiableMap(new HashMap<String, String>() {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		{
			put(OsmConstants.HIGHWAY, WILDCARD);
			put(OsmConstants.ROUTE, OsmConstants.FERRY);
		}
	});
	
	
	public static final List<String[]> TAGS_NOT_IMPORTABLE_WAY_MAP = new ArrayList<String[]>() {{
		add(new String[]{OsmConstants.HIGHWAY,OsmConstants.TRUNK});
		add(new String[]{OsmConstants.HIGHWAY,OsmConstants.MOTORWAY});
	}};
}