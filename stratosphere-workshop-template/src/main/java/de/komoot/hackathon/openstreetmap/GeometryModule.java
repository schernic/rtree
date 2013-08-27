package de.komoot.hackathon.openstreetmap;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKBReader;
import com.vividsolutions.jts.io.WKBWriter;

import java.io.IOException;

/**
 * @author jan
 * @date 23.08.13
 */
public class GeometryModule extends SimpleModule {
	/** automatically generated Logger statement */
	private final static org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(GeometryModule.class);

	public GeometryModule() {
		addSerializer(Geometry.class, new JsonSerializer<Geometry>() {
			@Override
			public void serialize(Geometry geometry, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException, JsonProcessingException {
				byte[] bytes = new WKBWriter(2, true).write(geometry);
				jsonGenerator.writeString(WKBWriter.toHex(bytes));
			};
		});

		SimpleModule simpleModule = addDeserializer(Geometry.class, new JsonDeserializer<Geometry>() {
			@Override
			public Geometry deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
				if(jsonParser.getCurrentToken() == JsonToken.VALUE_STRING) {
					try {
						byte[] bytes = WKBReader.hexToBytes(jsonParser.getValueAsString());
						return new WKBReader().read(bytes);
					} catch(ParseException e) {
						throw new IOException("Unable to read geometry", e);
					}
				} else {
					throw new IllegalStateException("Expected VALUE_STRING");
				}
			}
		});
	}

}
