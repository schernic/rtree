package de.komoot.hackathon;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import org.junit.*;

import java.util.List;

import static org.junit.Assert.*;

/**
 * @author jan
 * @date 26.08.13
 */
public class GridTest {
	/** automatically generated Logger statement */
	private final static org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(GridTest.class);

	private final static GeometryFactory FACTORY = new GeometryFactory();

	@Test
	public void testSimple() {
		Grid g = new Grid(10000);
		assertEquals(1, g.getEnvelopes().size());
	}

	@Test
	public void testOne() {
		Grid g = new Grid(1);
		assertEquals(180*360, g.getEnvelopes().size());
	}

	@Test
	public void testIds() {
		Grid g = new Grid(1);
		Point p = FACTORY.createPoint(new Coordinate(0,0));
		List<String> ids = g.getIdsForGeometry(p);
		assertEquals(4,ids.size());
	}


}
