package de.komoot.hackathon.pactPlan;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import com.vividsolutions.jts.geom.Envelope;

import eu.stratosphere.pact.common.type.Value;

public class PactEnvelope implements Value{
	
	public double getMaxX() {
		return maxX;
	}

	public void setMaxX(double maxX) {
		this.maxX = maxX;
	}

	public double getMinX() {
		return minX;
	}

	public void setMinX(double minX) {
		this.minX = minX;
	}

	public double getMaxY() {
		return maxY;
	}

	public void setMaxY(double maxY) {
		this.maxY = maxY;
	}

	public double getMinY() {
		return minY;
	}

	public void setMinY(double minY) {
		this.minY = minY;
	}
	
	public void setEnvelope(Envelope env){
		maxX = env.getMaxX();
		minX = env.getMinX();
		maxY = env.getMaxY();
		minY = env.getMinY();
	}

	private double maxX;
	private double minX;
	private double maxY;
	private double minY;
	
	@Override
	public void write(DataOutput out) throws IOException {
		out.writeDouble(maxX);
		out.writeDouble(minY);
		out.writeDouble(maxY);
		out.writeDouble(minY);
		
	}

	@Override
	public void read(DataInput in) throws IOException {
		maxX = in.readDouble();
		minX = in.readDouble();
		maxY = in.readDouble();
		minY = in.readDouble();		
	}
	
	public Envelope getEnvelope()
	{
		return new Envelope(minX, maxX, minY, maxY);
	}
}
