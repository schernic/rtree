package de.komoot.hackathon.openstreetmap;

import de.komoot.hackathon.OsmConstants;

import java.util.Map;
import java.util.Map.Entry;

public class WayHelper {

	/**
	 * Checks if way way represents an area.
	 *
	 * @return
	 */
	public static boolean isArea(OsmWay way, Map<String, String> tags) {
		// check if first node is the same as last node
		if(!firstAndLastIsSame(way)) {
			return false;
		}

		String area = tags.get(OsmConstants.AREA);
		if(area != null) {
			if(area.equals(OsmConstants.YES)) {
				return true;
			} else if(area.equals(OsmConstants.NO)) {
				return false;
			}
		}

		for(String key : OsmConstants.KEYSDESCRIBINGAREA) {
			if(tags.containsKey(key)) {
				return true;
			}
		}
		return false;
	}

	public static boolean firstAndLastIsSame(OsmWay way) {
		return way
				.getWayNodes()
				.get(0)
				.equals(way.getWayNodes().get(
						way.getWayNodes().size() - 1));
	}

	/**
	 * Checks if way way represents a way to walk, drive... on and not only the
	 * closing of an area. Ways in OSM can represent areas, but not ways to
	 * walk, drive... on.
	 *
	 * @return
	 */
	public static boolean isUsedAsOsmWay(OsmWay way, Map<String, String> tags) {
		// check if first node is not the same as last node
		if(!firstAndLastIsSame(way)) {
			return true;
		}

		for(String key : OsmConstants.KEYSDESCRIBINGCLOSEDPOLYLINE) {
			if(tags.containsKey(key)) {
				return true;
			}
		}
		if(doMapsShareATag(tags, OsmConstants.TAGSDESCRIBINGCLOSEDPOLYLINE)) {
			return true;
		}
		return false;
	}

	public static boolean isUsedAsKmtWay(OsmWay way, Map<String, String> tags) {
		if(tags == null) {
			return false;
		}
		for(String key : tags.keySet()) {
			if(OsmConstants.TAGS_DEFINING_WAY_MAP.containsKey(key)) {
				String value = way.getTags().get(key);

				for(String[] blackTags : OsmConstants.TAGS_NOT_IMPORTABLE_WAY_MAP) {
					if(blackTags[0].equals(key) && blackTags[1].equals(value)) {
						return false;
					}
				}

				if(OsmConstants.TAGS_DEFINING_WAY_MAP.get(key).equals(OsmConstants.WILDCARD)) {
					return true;
				} else {
					return value.equals(OsmConstants.TAGS_DEFINING_WAY_MAP.get(key));
				}
			}
		}
		return false;
	}

	private static boolean doMapsShareATag(Map<String, String> map1,
										   Map<String, String[]> map2) {
		for(Entry<String, String[]> entry : map2.entrySet()) {
			String value = map1.get(entry.getKey());
			String[] valuesToSearchFor = entry.getValue();
			for(String v : valuesToSearchFor) {
				if(v.equals(value)) {
					return true;
				}
			}
		}
		return false;
	}
}
