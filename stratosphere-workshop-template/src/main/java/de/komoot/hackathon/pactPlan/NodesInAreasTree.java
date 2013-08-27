package de.komoot.hackathon.pactPlan;

import eu.stratosphere.pact.common.contract.CrossContract;
import eu.stratosphere.pact.common.contract.FileDataSink;
import eu.stratosphere.pact.common.contract.FileDataSource;
import eu.stratosphere.pact.common.contract.MapContract;
import eu.stratosphere.pact.common.contract.MatchContract;
import eu.stratosphere.pact.common.contract.ReduceContract;
import eu.stratosphere.pact.common.io.RecordOutputFormat;
import eu.stratosphere.pact.common.plan.Plan;
import eu.stratosphere.pact.common.plan.PlanAssemblerDescription;
import eu.stratosphere.pact.common.type.base.PactList;
import eu.stratosphere.pact.common.type.base.PactString;

public class NodesInAreasTree implements PlanAssemblerDescription {

	// schema: GEO_ID, GEO_OBJECT, ENVELOPE, CELL_ID
	public static final int CELL_ID_COLUMN = 3;
	public static final int GEO_ID_COLUMN = 0;
	@Override
	public Plan getPlan(String... args) {
		if (args.length != 4) {
			throw new IllegalArgumentException("illegal number of arguments");
		}
		
		int dop = Integer.valueOf(args[0]);
		String nodesPath = args[1];
		String areasPath = args[2];
		String outputPath = args[3];
		FileDataSource nodes = new FileDataSource(GeometryInputFormat.class,
				nodesPath, "Nodes");
		
		FileDataSource areas = new FileDataSource(GeometryInputFormat.class,
				areasPath, "Areas");

		MapContract boundNodes = MapContract.builder(BoundingBox.class)
				.input(nodes).name("Add BoundingBox for Nodes").build();
		
		MapContract boundAreas = MapContract.builder(BoundingBox.class)
				.input(areas).name("Add BoundingBox for Areas").build();

		ReduceContract areaTree = ReduceContract
				.builder(Treeify.class, PactString.class, CELL_ID_COLUMN)
				.input(boundAreas).name("Insert Areas into Tree").build();
		
		CrossContract probeTree = CrossContract.builder(IntersectTree.class).input1(boundNodes).input2(areaTree).name("Intersect nodes with areas in tree").build();
		
		MatchContract matchedAreaIDs = MatchContract.builder(IntersectMatcher.class, PactString.class, 0, 2).input1(areas).input2(probeTree).name("Match Areas and TreeRectangles").build();
				
		ReduceContract reduceNodes = ReduceContract
				.builder(NodesReducer.class, PactString.class, 0)
				.input(matchedAreaIDs).name("Reduce by nodeID").build();

		FileDataSink output = new FileDataSink(RecordOutputFormat.class,
				outputPath, reduceNodes, "Sink");
		RecordOutputFormat.configureRecordFormat(output).recordDelimiter('\n')
				.fieldDelimiter(',').lenient(true).field(PactString.class, 0)
				.field(PactListImpl.class, 1);
		
//		FileDataSink output = new FileDataSink(RecordOutputFormat.class,
//				outputPath, probeTree, "Sink");
//		RecordOutputFormat.configureRecordFormat(output).recordDelimiter('\n')
//				.fieldDelimiter(',').lenient(true).field(PactString.class, 0)
//				.field(PactString.class, 2);
		
		Plan plan = new Plan(output);
		plan.setDefaultParallelism(dop);

		return plan;
	}

	@Override
	public String getDescription() {
		return "areas output";
	}

}
