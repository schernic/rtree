package de.komoot.hackathon;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;

import java.util.ArrayList;
import java.util.List;

/**
 * Simple Regular grid implementation
 *
 * @author jan
 */
public class Grid {
	/** automatically generated Logger statement */
	private final static org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(Grid.class);
	List<IdEnvelope> envelopes = new ArrayList<>();

	/**
	 * Creates a new grid for WGS84 coordinates with the given cell width
	 *
	 * @param cellWidth width of a cell in degrees
	 */
	public Grid(double cellWidth) {
		double x = -180;
		while(x < 180) {
			double y = -90;
			while(y < 90) {
				String id = x + ":" + y;
				envelopes.add(new IdEnvelope(x, x + cellWidth, y, y + cellWidth, id));
				y += cellWidth;
			}
			x += cellWidth;
		}
	}

	public List<String> getIdsForGeometry(Geometry g) {
		Envelope ge = g.getEnvelopeInternal();
		return getIdsForGeometry(ge);		
	}
	
	public List<String> getIdsForGeometry(Envelope ge)
	{
		List<String> ids = new ArrayList<>();
		
		for(IdEnvelope e : envelopes) {
			if(ge.intersects(e)) {
				ids.add(e.getId());
			}
		}
		
		return ids;
	}

	public List<IdEnvelope> getEnvelopes() {
		return envelopes;
	}

	class IdEnvelope extends Envelope {
		private final String id;

		IdEnvelope(double x1, double x2, double y1, double y2, String id) {
			super(x1, x2, y1, y2);
			this.id = id;
		}

		public String getId() {
			return id;
		}
	}
}


