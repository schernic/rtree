package de.komoot.hackathon.pactPlan;

import java.util.Iterator;

import eu.stratosphere.pact.common.stubs.Collector;
import eu.stratosphere.pact.common.stubs.ReduceStub;
import eu.stratosphere.pact.common.type.PactRecord;
import eu.stratosphere.pact.common.type.base.PactList;
import eu.stratosphere.pact.common.type.base.PactString;

public class NodesReducer extends ReduceStub {

	@Override
	public void reduce(Iterator<PactRecord> records, Collector<PactRecord> out)
			throws Exception {
		PactRecord element = null;
		PactList<PactString> areas = new PactListImpl();
		
		while (records.hasNext()) {
			element = records.next();
			areas.add(element.getField(1, PactString.class));						
		}
		
		PactRecord result = new PactRecord();
		if(element != null)
		{
			result.addField(element.getField(0, PactString.class));
			result.addField(areas);
			
			out.collect(result);
		}
	}
}
