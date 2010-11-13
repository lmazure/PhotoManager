package lmzr.photomngr.ui.mapdisplayer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;

import lmzr.photomngr.data.GPS.GPSData;
import lmzr.photomngr.data.GPS.GPSDatabase;
import lmzr.photomngr.data.GPS.GPSDatabase.GPSRecord;
import lmzr.util.string.HierarchicalCompoundString;

/**
 * @author Laurent
 *
 */
public class GoogleMapsURICreator implements MapURICreator {
	
	static final private String templateName = "resources/googleMapsTemplate.html";
	static final private String listPlaceholder = "__LIST_PLACEHOLDER__";


	/**
	 * @see lmzr.photomngr.ui.mapdisplayer.MapURICreator#createMapURLFromGPSData(lmzr.util.string.HierarchicalCompoundString, lmzr.photomngr.data.GPS.GPSData)
	 */
	public URI createMapURIFromGPSData(final GPSRecord record) {

		final HierarchicalCompoundString location = record.getLocation();
		final GPSData data = record.getGPSData();
		
		URI uri = null;
		
		try {
			final String str = "http://maps.google.com/maps?q="+
							   + data.getLatitudeAsDouble()
							   + "+"
							   + data.getLongitudeAsDouble()
							   + "+("
							   + URLEncoder.encode(location.toLongString().replace(">","/"),"UTF-8")
							   +")&ll="
							   + data.getLatitudeAsDouble()
							   + ","
							   + data.getLongitudeAsDouble()
							   + "&spn="
							   + data.getLatitudeRangeAsDouble()
							   + ","
							   + data.getLongitudeRangeAsDouble()
							   + "&t=h&hl=fr";
			uri = new URI(str);
		} catch (final UnsupportedEncodingException e) {
			// this should never occur
			e.printStackTrace();
		} catch (final URISyntaxException e) {
			// this should never occur
			e.printStackTrace();
		}

		return uri;
	}

	/**
	 * create a file displaying all the GPS area in blue, except for locationToHighlight which will be displayed
	 * in red.
	 * @param file file where to write the HTML and JavaScript data 
	 * @param locationToHighlight the location to highlight
	 * @param GPSDatabase GPS database
	 * @return 
	 */
	public void createMapURIForGPSDebug(final File file,
			                            final HierarchicalCompoundString locationToHighlight,
                                        final GPSDatabase GPSDatabase) throws IOException
	{
		final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		final URL url = classLoader.getResource(templateName);
		if ( url == null ) {
			System.err.println("failed to locate template \""+templateName+"\"");
			return;
		}
				
		final InputStream inputStream = url.openStream();
	    final BufferedReader in = new BufferedReader(new InputStreamReader(inputStream, "UTF8"));
	    final Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF8"));
	    
	    String str;
	    while  ( (str = in.readLine()) != null ) {
	    	out.write(handleLine(str,locationToHighlight, GPSDatabase));
	    	out.write('\n');
	    }
	    
	    in.close();
	    out.close();
	}

	/**
	 * edit a line by replacing the placeholders by their real content
	 * @param string the current template line
	 * @param locationToHighlight the current location to highlight
	 * @param GPSDatabase GPS database
	 * @return 
	 */
	static private String handleLine(final String string,
			                         final HierarchicalCompoundString locationToHighlight,
			                         final GPSDatabase GPSDatabase) {
		
		String str = string;
		
		if ( string.indexOf(listPlaceholder)>=0 ) {
			str = str.replace(listPlaceholder, listOfGPSAreas(locationToHighlight, GPSDatabase));
		}
		
		return str;
	}
	
	/**
	 * @param locationToHighlight
	 * @param GPSDatabase
	 * @return
	 */
	static private String listOfGPSAreas(final HierarchicalCompoundString locationToHighlight,
			                             final GPSDatabase GPSDatabase)
	{
		final StringBuilder s = listOfGPSAreasRecurse((HierarchicalCompoundString)GPSDatabase.getRoot(),
				                                      locationToHighlight,
				                                      GPSDatabase);
		return s.toString();
	}
	
	/**
	 * @param location
	 * @param locationToHighlight
	 * @param GPSDatabase
	 * @return
	 */
	static private StringBuilder listOfGPSAreasRecurse(final HierarchicalCompoundString location,
			                                           final HierarchicalCompoundString locationToHighlight,
			                                           final GPSDatabase GPSDatabase) {
		
		final StringBuilder str = new StringBuilder();
		boolean stringHasBeenAdded = false;
		
		final GPSRecord record =  GPSDatabase.getGPSData(location);
		if ( record != null ) {
			final GPSData data = record.getGPSData();
			if ( ( data!=null) && data.isComplete() ) {
				str.append("new GPSRectangle(\"");
				str.append(location);
				str.append("\",");
				str.append(data.getLatitudeMinAsDouble());
				str.append(",");
				str.append(data.getLatitudeMaxAsDouble());
				str.append(",");
				str.append(data.getLongitudeMinAsDouble());
				str.append(",");
				str.append(data.getLongitudeMaxAsDouble());
				str.append(",\"");
				str.append(location.equals(locationToHighlight)?"red":"blue");
				str.append("\")");
				stringHasBeenAdded = true;				
			}
		}
		
		for ( int i=0; i<GPSDatabase.getChildCount(location); i++) {
			final HierarchicalCompoundString child = (HierarchicalCompoundString)GPSDatabase.getChild(location, i);
			final StringBuilder s = listOfGPSAreasRecurse(child, locationToHighlight, GPSDatabase);
			if ( s.length() > 0) {
				if ( stringHasBeenAdded ) {
					str.append(",\n");
				}
				str.append(s);
				stringHasBeenAdded = true;
			}
		}
		
		return str;
	}
}
