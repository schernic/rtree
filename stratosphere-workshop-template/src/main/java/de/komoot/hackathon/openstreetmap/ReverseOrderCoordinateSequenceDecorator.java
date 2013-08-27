package de.komoot.hackathon.openstreetmap;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.CoordinateSequence;
import com.vividsolutions.jts.geom.Envelope;
import org.springframework.util.Assert;

/**
 * Decorator for {@link KmtCoordinateSequenceLinearRing}. Can access the
 * {@link KmtCoordinateSequenceLinearRing}'s {@link Coordinate}s if specified in
 * reverse order.
 * 
 * @author richard, jan
 * 
 */
public class ReverseOrderCoordinateSequenceDecorator implements CoordinateSequence {
	/** automatically generated Logger statement */
	// private final static org.slf4j.Logger LOGGER =
	// org.slf4j.LoggerFactory.getLogger(CoordinateSlimWaySequence.class);

	private final CoordinateSequence coordSeq;

	public ReverseOrderCoordinateSequenceDecorator(CoordinateSequence coordSeq) {
		Assert.notNull(coordSeq, "coordinateSequence must not be null");
		this.coordSeq = coordSeq;
	}
	
	private int reverseIndex(int index) {
		return coordSeq.size() - index - 1;
	}

	@Override
	public Envelope expandEnvelope(Envelope env) {
		return coordSeq.expandEnvelope(env);
	}

	@Override
	public Coordinate getCoordinate(int i) {
			return coordSeq.getCoordinate(reverseIndex(i));
	}

	@Override
	public int size() {
		return coordSeq.size();
	}

	@Override
	public Coordinate[] toCoordinateArray() {
		Coordinate[] coordArray = new Coordinate[size()];
		for (int i = 0; i < size(); i++) {
			coordArray[i] = getCoordinate(i);
		}
		return coordArray;
	}

	@Override
	public CoordinateSequence clone() {
		//provoke ClassCastException of wrong type is returned
		CoordinateSequence cloned = (CoordinateSequence) coordSeq.clone();
		return new ReverseOrderCoordinateSequenceDecorator( cloned);
	}

	@Override
	public void getCoordinate(int i, Coordinate coord) {
		coordSeq.getCoordinate(reverseIndex(i), coord);
	}

	@Override
	public Coordinate getCoordinateCopy(int i) {
		return coordSeq.getCoordinateCopy(reverseIndex(i));
	}

	@Override
	public int getDimension() {
		return coordSeq.getDimension();
	}

	@Override
	public double getOrdinate(int index, int ordinateIndex) {
		return coordSeq.getOrdinate(reverseIndex(index), ordinateIndex);
	}

	@Override
	public double getX(int index) {
		return coordSeq.getX(reverseIndex(index));
	}

	@Override
	public double getY(int index) {
		return coordSeq.getY(reverseIndex(index));
	}

	@Override
	public void setOrdinate(int index, int ordinateIndex, double value) {
		coordSeq.setOrdinate(reverseIndex(index), ordinateIndex, value);
	}
}
