package object.tile;

import java.io.BufferedInputStream;
import java.io.InputStream;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import manager.ResourcesManager;

public class TilesLoader {

	public static void loadTiles() {
		try {
			final SAXParserFactory mSAXParserFactory = SAXParserFactory.newInstance();
			final SAXParser mSAXParser = mSAXParserFactory.newSAXParser();

			final XMLReader xmlReader = mSAXParser.getXMLReader();
			final TilesXMLParser pXMLParser = new TilesXMLParser();

			xmlReader.setContentHandler(pXMLParser);

			InputStream inputStream = ResourcesManager.gameActivity.getAssets().open("gfx/game/tiles/tiles.xml");
			xmlReader.parse(new InputSource(new BufferedInputStream(inputStream)));
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
