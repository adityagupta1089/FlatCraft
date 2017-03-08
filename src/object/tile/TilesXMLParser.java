package object.tile;

import java.util.HashMap;

import org.andengine.util.SAXUtils;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import manager.ResourcesManager;

public class TilesXMLParser extends DefaultHandler implements TilesXMLConstants {

	public TilesXMLParser() {
		ResourcesManager.tilePassability = new HashMap<String, Boolean>();
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		if (localName.equals(TAG_TILE)) {
			ResourcesManager.tilePassability.put(SAXUtils.getAttributeOrThrow(attributes, TAG_TILE_ATTRIBUTE_NAME), SAXUtils.getBooleanAttributeOrThrow(attributes, TAG_TILE_ATTRIBUTE_PASSABLE));
		} else if (!localName.equals(TAG_TILES)) { throw new SAXException("Unexpected start tag: '" + localName + "'."); }
	}
}
