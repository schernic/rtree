package de.komoot.hackathon.pactPlan;

import java.util.List;

import de.komoot.hackathon.Grid;
import eu.stratosphere.pact.common.stubs.Collector;
import eu.stratosphere.pact.common.stubs.MapStub;
import eu.stratosphere.pact.common.type.PactRecord;
import eu.stratosphere.pact.common.type.base.PactString;

public class Gridify extends MapStub {

	private Grid grid;
	
	public Gridify() {
		grid = new Grid(1.0);
	}
	
	@Override
	public void map(PactRecord record, Collector<PactRecord> out)
			throws Exception {
		
		PactEnvelope env = record.getField(2, PactEnvelope.class);
		List<String> ids = grid.getIdsForGeometry(env.getEnvelope());
		
		for(String id : ids)
		{
			PactRecord outputRecord = new PactRecord();
			outputRecord.addField(record.getField(0, PactString.class));
			outputRecord.addField(record.getField(1, PactGeometry.class));
			outputRecord.addField(new PactString(id));
			
			out.collect(outputRecord);
		}
	}
}
