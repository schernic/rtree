package de.komoot.hackathon.pactPlan;


import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import eu.stratosphere.pact.common.io.TextInputFormat;
import eu.stratosphere.pact.common.type.PactRecord;
import eu.stratosphere.pact.common.type.base.PactString;

public class GeometryInputFormat extends TextInputFormat
{

	PactString reusableId = new PactString();
	PactString reusableGeometryStr = new PactString();
	
	@Override
	public boolean readRecord(PactRecord target, byte[] bytes, int offset, int numBytes)
	{

		PactString str = this.theString;
		
		if (this.ascii) {
			str.setValueAscii(bytes, offset, numBytes);
		}
		else {
			ByteBuffer byteWrapper = this.byteWrapper;
			if (bytes != byteWrapper.array()) {
				byteWrapper = ByteBuffer.wrap(bytes, 0, bytes.length);
				this.byteWrapper = byteWrapper;
			}
			byteWrapper.clear();
			byteWrapper.position(offset);
			byteWrapper.limit(offset + numBytes);
				
			try {
				CharBuffer result = this.decoder.decode(byteWrapper);
				str.setValue(result);
			}
			catch (CharacterCodingException e) {
				return false;
			}
		}
		
		
		PactString id = findStringValue(str,"id",reusableId);
		if (id == null)
			throw new RuntimeException("Could not parse id attribute");
		PactString geometryStr = findStringValue(str,"geometry",reusableGeometryStr);
		if(geometryStr == null)
			throw new RuntimeException("Could not parse geometry attribute");
		
		target.clear();
		target.addField(id);
		target.addField(new PactGeometry(geometryStr.toString()));
		return true;
		
	}
	
	public static PactString findStringValue(PactString text, String attribut, PactString target){
		int startPosition = text.find("\""+attribut+"\":\"");
		if(startPosition == -1) {
			return null;
		}
		startPosition += 4 + attribut.length();
		int endPosition = text.find("\"",startPosition);
		if(endPosition == -1){
			return null;
		}
		text.substring(target,startPosition,endPosition);
		return target;
	}
}
