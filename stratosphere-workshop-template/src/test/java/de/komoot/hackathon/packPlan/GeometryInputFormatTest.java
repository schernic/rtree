package de.komoot.hackathon.packPlan;

import org.junit.*;

import de.komoot.hackathon.pactPlan.GeometryInputFormat;

import eu.stratosphere.nephele.configuration.Configuration;
import eu.stratosphere.pact.common.io.FileInputFormat;
import eu.stratosphere.pact.common.type.PactRecord;
import eu.stratosphere.pact.common.type.base.PactString;

public class GeometryInputFormatTest {
	
	private final static String TEST_INPUT[] = {
			"{\"id\":\"W3999490\",\"geometry\":\"0020000002000010E6000000034021344C81B8D244404ACA2A370B2F0940213504CE9BD9E6404ACA28FEFCCAC240213685DED1B20F404ACA2480120917\",\"tags\":{\"highway\":\"residential\",\"name\":\"Besenbuschkuhle\"}}",
			"{\"id\":\"W3999496\",\"geometry\":\"0020000002000010E6000000024021352674080F99404ACB09F043CA5B40213523BA196BE2404ACB05E94E0CD4\",\"tags\":{\"surface\":\"paved\",\"highway\":\"tertiary\",\"name\":\"Lotjeweg\",\"cycleway\":\"lane\",\"maxspeed\":\"30\"}}" };
	
	
	@Test
	public void test(){
		GeometryInputFormat format = new GeometryInputFormat();
		Configuration config = new Configuration();
		config.setString(FileInputFormat.FILE_PARAMETER_KEY, "file");
		format.configure(config);
		PactRecord record = new PactRecord();
		
		for (String input : TEST_INPUT) {
			format.readRecord(record, input.getBytes(), 0, input.length());
			System.out.println("id: " + record.getField(0, PactString.class) 
					+ "\ngeometry: " + record.getField(1, PactString.class));
		}
		
	}

}
