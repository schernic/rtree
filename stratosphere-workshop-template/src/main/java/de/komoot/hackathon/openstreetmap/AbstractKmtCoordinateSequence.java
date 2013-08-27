package de.komoot.hackathon.openstreetmap;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.CoordinateSequence;

/**
 * Abstract base class for {@link CoordinateSequence}s used by
 * .
 *
 * @author richard
 */
public abstract class AbstractKmtCoordinateSequence implements CoordinateSequence {
	// /** automatically generated Logger statement */
	// private final static org.slf4j.Logger LOGGER =
	// org.slf4j.LoggerFactory.getLogger(CoordinateSequenceOsmReader.class);

	protected final static int DIMENSION = 2;

	@Override
	public void getCoordinate(int i, Coordinate coord) {
		Coordinate coordAtIndex = getCoordinate(i);
		coord.x = coordAtIndex.x;
		coord.y = coordAtIndex.y;
	}

	@Override
	public Coordinate getCoordinateCopy(int i) {
		return new Coordinate(getCoordinate(i));
	}

	@Override
	public int getDimension() {
		return DIMENSION;
	}

	@Override
	public double getOrdinate(int index, int ordinateIndex) {
		Coordinate coordAtIndex = getCoordinate(index);
		switch(ordinateIndex) {
			case CoordinateSequence.X:
				return coordAtIndex.x;
			case CoordinateSequence.Y:
				return coordAtIndex.y;
			case CoordinateSequence.Z:
				return coordAtIndex.z;
		}
		throw new IllegalArgumentException("invalid ordinateIndex");
	}

	@Override
	public double getX(int index) {
		return getCoordinate(index).x;
	}

	@Override
	public double getY(int index) {
		return getCoordinate(index).y;
	}

	@Override
	public void setOrdinate(int index, int ordinateIndex, double value) {
		switch(ordinateIndex) {
			case CoordinateSequence.X:
				getCoordinate(index).x = value;
				break;
			case CoordinateSequence.Y:
				getCoordinate(index).y = value;
				break;
			case CoordinateSequence.Z:
				getCoordinate(index).z = value;
				break;
			default:
				throw new IllegalArgumentException("invalid ordinateIndex");
		}
	}

	public abstract CoordinateSequence clone();
}
