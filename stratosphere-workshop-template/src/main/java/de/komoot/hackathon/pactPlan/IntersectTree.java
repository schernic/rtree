package de.komoot.hackathon.pactPlan;

import java.util.LinkedList;

import com.infomatiq.jsi.Rectangle;
import com.infomatiq.jsi.rtree.RTree;
import com.vividsolutions.jts.geom.Envelope;

import eu.stratosphere.pact.common.stubs.Collector;
import eu.stratosphere.pact.common.stubs.CrossStub;
import eu.stratosphere.pact.common.type.PactRecord;
import eu.stratosphere.pact.common.type.base.PactString;
import gnu.trove.procedure.TIntProcedure;

public class IntersectTree extends CrossStub{

	@Override
	public void cross(PactRecord record1, PactRecord record2,
			Collector<PactRecord> out) {		
		final LinkedList<PactString> areas = new LinkedList<PactString>();
		PactString nodeId = record1.getField(0, PactString.class);
		PactGeometry nodeGeo = record1.getField(1, PactGeometry.class);
		Envelope env = record1.getField(2, PactEnvelope.class).getEnvelope();
		
		RTree tree = record2.getField(0,PactRTree.class).getTree();
		Rectangle r = new Rectangle((float)env.getMaxX(),(float)env.getMaxY(),(float)env.getMinX(),(float)env.getMinY());
		tree.intersects(r, new TIntProcedure(){

			@Override
			public boolean execute(int value) {
				areas.add(new PactString("Aw"+value));
				return true;
			}});
		
		for (PactString area : areas) {
			PactRecord newRecord = new PactRecord();
			newRecord.addField(nodeId);
			newRecord.addField(nodeGeo);
			newRecord.addField(area);
			out.collect(newRecord);
		}
		
	}

}
