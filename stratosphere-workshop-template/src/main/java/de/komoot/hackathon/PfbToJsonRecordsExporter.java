package de.komoot.hackathon;

import de.komoot.hackathon.openstreetmap.OsmToKmtSink;
import org.openstreetmap.osmosis.pbf2.v0_6.PbfReader;

import java.io.*;
import java.util.zip.GZIPOutputStream;

/**
 * Exports a Pbf openstreetmap file to a file containing rows of json objects.
 *
 * @author jan
 */
public class PfbToJsonRecordsExporter {
	/** automatically generated Logger statement. */
	private final static org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(PfbToJsonRecordsExporter.class);

	private final static int NUMBEROFNODES = 2001460000; //prevents ArrayList resize: from http://www.openstreetmap.org/stats/data_stats.html

	public static void main(String[] args) throws IOException {
		PbfReader in = new PbfReader(new File(args[0]), 4);

		File directory = new File(args[1]);
		try(BufferedWriter nwriter = createWriter(new File(directory, "nodes-raw.csv.gz"))) {
			try(BufferedWriter lwriter = createWriter(new File(directory, "ways-raw.csv.gz"))) {
				try(BufferedWriter awriter = createWriter(new File(directory, "areas-raw.csv.gz"))) {
					OsmToKmtSink out = new OsmToKmtSink(1000, nwriter, lwriter, awriter);
					in.setSink(out);
					in.run();
				}
			}
		}
		LOGGER.info("Wrote all files to " + directory);
	}

	private static BufferedWriter createWriter(File out) throws IOException {
		return new BufferedWriter(new OutputStreamWriter(new GZIPOutputStream(new FileOutputStream(out))));
	}
}
