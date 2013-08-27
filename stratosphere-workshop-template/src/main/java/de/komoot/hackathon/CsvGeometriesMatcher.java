package de.komoot.hackathon;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vividsolutions.jts.geom.Geometry;
import de.komoot.hackathon.openstreetmap.*;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Reads three csv files containing nodes, ways and areas and matches them based on a spatial relationship.
 *
 * @author jan
 */
public class CsvGeometriesMatcher {
	/** automatically generated Logger statement */
	private final static org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(CsvGeometriesMatcher.class);
	/** pattern of chars that are safe for use in the csv file. */
	private final static Pattern BADCHARS = Pattern.compile("[\\w \\-_]");
	private final ObjectMapper mapper;

	private List<JsonGeometryEntity<Geometry>> nodes;
	private List<JsonGeometryEntity<Geometry>> ways;
	private List<JsonGeometryEntity<Geometry>> areas;

	public CsvGeometriesMatcher(File file) throws IOException {
		mapper = new ObjectMapper();
		mapper.registerModule(new GeometryModule());
		mapper.configure(JsonGenerator.Feature.AUTO_CLOSE_TARGET, false);

		readRecords(file);
	}

	public static void main(String[] args) throws IOException {
		File directory = new File(args[0]);
		CsvGeometriesMatcher m = new CsvGeometriesMatcher(directory);
		m.writeMatches(directory);
	}

	/**
	 * Matches (spatially intersects) two Collections of OsmEntities against each other.
	 * This is the non-optimized brute-force variant.
	 * Writes a csv line to the given targetFile containing all items of the first Collection with a list of all intersecting elements from the second Collection.
	 *
	 * @param entities1
	 * @param entities2
	 * @param targetFile
	 * @throws java.io.IOException
	 */
	private static void match(Collection<? extends JsonGeometryEntity<Geometry>> entities1, Collection<? extends JsonGeometryEntity<?>> entities2, Writer targetFile) throws IOException {
		LOGGER.info("Matching {} elements with {} elements", entities1.size(), entities2.size());
		for(JsonGeometryEntity<?> e1 : entities1) {
			Geometry g1 = e1.getGeometry();
			targetFile.write(e1.getId());
			targetFile.write(',');
			String name = e1.getTags().get("name");
			if(name != null) {
				targetFile.write('"');
				targetFile.write(BADCHARS.matcher(name).replaceAll(""));
				targetFile.write('"');
			}
			for(JsonGeometryEntity<?> e2 : entities2) {
				Geometry g2 = e2.getGeometry();
				if(g1.intersects(g2)) {
					targetFile.write(',');
					targetFile.write(e2.getId());
				}
			}
			targetFile.write('\n');
		}
		LOGGER.info("done");
	}

	private void readRecords(File directory) throws IOException {
		try(BufferedReader reader = new BufferedReader(new FileReader(new File(directory, "nodes-raw.csv")))) {
			nodes = readRecords(reader);
		}
		try(BufferedReader reader = new BufferedReader(new FileReader(new File(directory, "ways-raw.csv")))) {
			ways = readRecords(reader);
		}
		try(BufferedReader reader = new BufferedReader(new FileReader(new File(directory, "areas-raw.csv")))) {
			areas = readRecords(reader);
		}

	}

	private List<JsonGeometryEntity<Geometry>> readRecords(BufferedReader reader) throws IOException {
		String line;
		List<JsonGeometryEntity<Geometry>> entries = new ArrayList<>();
		while((line=reader.readLine()) != null) {
			try {
				entries.add(mapper.readValue(line, JsonGeometryEntity.class));
			} catch (RuntimeException e) {
				throw new RuntimeException("Unable to parse line: " + line + ":", e);
			}
		}
		return entries;
	}

	private void writeMatches(File directory) throws IOException {
		try(Writer writer = new FileWriter(new File(directory, "nodes-with-areas.csv"))) {
			match(nodes, areas, writer);
		}
		try(Writer writer = new FileWriter(new File(directory, "ways-with-areas.csv"))) {
			match(ways, areas, writer);
		}
	}
}
