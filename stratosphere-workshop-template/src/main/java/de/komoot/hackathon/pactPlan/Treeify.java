package de.komoot.hackathon.pactPlan;

import java.util.Iterator;

import com.infomatiq.jsi.Rectangle;
import com.infomatiq.jsi.rtree.RTree;

import eu.stratosphere.pact.common.stubs.Collector;
import eu.stratosphere.pact.common.stubs.ReduceStub;
import eu.stratosphere.pact.common.type.PactRecord;
import eu.stratosphere.pact.common.type.base.PactString;

public class Treeify extends ReduceStub{
	private PactEnvelope pactEnv = new PactEnvelope();

	@Override
	public void reduce(Iterator<PactRecord> records, Collector<PactRecord> out)
			throws Exception {
		RTree tree = new RTree();
		tree.init(null);
		while(records.hasNext()){
			PactRecord record = records.next();
			pactEnv = record.getField(2, PactEnvelope.class);
			Rectangle r = new Rectangle((float)pactEnv.getMaxX(),(float)pactEnv.getMaxY(),(float)pactEnv.getMinX(),(float)pactEnv.getMinY());
			String areaIndex = record.getField(0, PactString.class).toString();
			
			tree.add(r, Integer.valueOf(areaIndex.substring(2)));
		}
		
		out.collect(new PactRecord(new PactRTree(tree)));
		
	}
	
}
