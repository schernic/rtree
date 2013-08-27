package de.komoot.hackathon.pactPlan;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKBReader;
import com.vividsolutions.jts.io.WKBWriter;

import eu.stratosphere.pact.common.type.Value;

public class PactGeometry implements Value {

	private byte[] bytes;

	private final WKBReader reader = new WKBReader();

	private final WKBWriter writer = new WKBWriter();

	public PactGeometry(byte[] bytes) {
		this.bytes = bytes;
	}

	public PactGeometry(String hexGeo) {
		this.bytes = WKBReader.hexToBytes(hexGeo);
	}

	public PactGeometry() {
	}

	public Geometry getGeo() throws ParseException {
		return this.reader.read(bytes);
	}

	public void setGeo(Geometry geo) {
		this.bytes = this.writer.write(geo);
	}

	@Override
	public void write(DataOutput out) throws IOException {
		out.writeInt(this.bytes.length);
		out.write(this.bytes);
	}

	@Override
	public void read(DataInput in) throws IOException {
		this.bytes = new byte[in.readInt()];
		in.readFully(this.bytes);
	}
}
