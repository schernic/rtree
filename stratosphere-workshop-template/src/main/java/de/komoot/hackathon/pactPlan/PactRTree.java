package de.komoot.hackathon.pactPlan;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import com.infomatiq.jsi.rtree.RTree;

import eu.stratosphere.pact.common.type.Value;

public class PactRTree implements Value{
	static public int i;
	RTree tree;
	public PactRTree(){
		this.tree = new RTree();
		tree.init(null);
		i = 0;
	}
	
	public PactRTree(RTree tree){
		this.tree = tree;
		i = 0;
	}
	@Override
	public void write(DataOutput out) throws IOException {
		/*if(!(out instanceof OutputStream)){
			throw new RuntimeException("dataoutput ist doof"+out.getClass().getName());
		}*/
		tree.write(out);
	}

	@Override
	public void read(DataInput in) throws IOException {
		tree.read(in);
		i++;
		
		
		
		/*if(!(in instanceof InputStream)){
			throw new RuntimeException("datainput ist doof "+in.getClass().getName());
		}
		
		ObjectInputStream input = new ObjectInputStream((InputStream)in);
		try {
			tree = (RTree)input.readObject();
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("Could not read PactRtree input");
		}*/
		
	}
	
	public RTree getTree(){
		return tree;
	}

}
