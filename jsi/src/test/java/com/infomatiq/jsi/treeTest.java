package com.infomatiq.jsi;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

import junit.framework.TestCase;

import com.infomatiq.jsi.rtree.RTree;

public class treeTest extends TestCase{

public void test() throws IOException{
	RTree tree = new RTree();
	tree.init(null);
	for(int i = 0;i<1000;i++){
		tree.add(new Rectangle(i,i+1,i+2,i+3), i);
	}
	PipedInputStream pipedIn= new PipedInputStream(1024*1024);
	DataInputStream dataIn = new DataInputStream(pipedIn);
	DataOutputStream dataOut = new DataOutputStream(new PipedOutputStream(pipedIn));
	
	tree.write(dataOut);
	
	
	RTree tree2 = new RTree();
	tree2.init(null);
	tree2.read(dataIn);
	
	assert(tree.size()==1000);
	
}

}
