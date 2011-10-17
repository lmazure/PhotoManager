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
 * @author Laurent Mazur�
 */
public class GoogleMapsURICreator implements MapURICreator {
	
	static final private String templateName = "resources/googleMapsTemplate.html";
	
	static final private String listPlaceholder = "__PLACEHOLDER_LIST__";
	static final private String mapCenterPlaceholder = "__PLACEHOLDER_MAPCENTER__";
	static final private String zoomPlaceholder = "__PLACEHOLDER_ZOOM__";
	
	static final private String defaultCenter = "47.0, 2.0"; 
	static final private String defaultZoom = "6";

	/**
	 * 
	 */
	public GoogleMapsURICreator()
	{
	}
	
	/**
	 * @see lmzr.photomngr.ui.mapdisplayer.MapURICreator#getName()
	 */
	@Override
	public String getName()
	{
		return "Google";
	}

	/**
	 * @see lmzr.photomngr.ui.mapdisplayer.MapURICreator#createMapURLFromGPSData(lmzr.util.string.HierarchicalCompoundString, lmzr.photomngr.data.GPS.GPSData)
	 */
	@Override
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
	 * @param gpsDatabase GPS database
	 * @throws IOException 
	 */
	public void createMapURIForGPSDebug(final File file,
			                            final HierarchicalCompoundString locationToHighlight,
                                        final GPSDatabase gpsDatabase) throws IOException
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
	    	out.write(handleLine(str,locationToHighlight, gpsDatabase));
	    	out.write('\n');
	    }
	    
	    in.close();
	    out.close();
	}

	/**
	 * edit a line by replacing the placeholders by their real content
	 * @param string the current template line
	 * @param locationToHighlight the current location to highlight
	 * @param gpsDatabase GPS database
	 * @return 
	 */
	static private String handleLine(final String string,
			                         final HierarchicalCompoundString locationToHighlight,
			                         final GPSDatabase gpsDatabase) {
		
		String str = string;
		
		if ( string.indexOf(listPlaceholder)>=0 ) {
			str = str.replace(listPlaceholder, listOfGPSAreas(locationToHighlight, gpsDatabase));
		}

		if ( string.indexOf(mapCenterPlaceholder)>=0 ) {
			String center = defaultCenter;
			final GPSRecord record = gpsDatabase.getGPSData(locationToHighlight);
			if ( record!=null ) {
				final GPSData gps = record.getGPSData();
				if ( gps!=null && gps.isComplete() ) {
					final Double latitude = gps.getLatitudeAsDouble(); 
					final Double longitude = gps.getLongitudeAsDouble();
					center = latitude.toString() + "," + longitude.toString();
				}
			}
			str = str.replace(mapCenterPlaceholder, center);
		}

		if ( string.indexOf(zoomPlaceholder)>=0 ) {
			String zoom = defaultZoom;
			final GPSRecord record = gpsDatabase.getGPSData(locationToHighlight);
			if ( record != null ) {
				final GPSData gps = record.getGPSData();
				if ( gps != null && gps.isComplete() ) {
					final Double latitudeRange = gps.getLatitudeRangeAsDouble();
					final Double longitudeRange = gps.getLongitudeRangeAsDouble();
					final Double latitude = gps.getLatitudeAsDouble();
					final double earthRadiusInMeters = 6365000;
					final double longitudeRangeInMeters = ( longitudeRange / 180.0)  *  Math.PI * earthRadiusInMeters * Math.cos(latitude / 180.0);  
					final double latitudeRangeInMeters = ( latitudeRange / 180.0)  *  Math.PI * earthRadiusInMeters;
					final double rangeInMeters = Math.max(longitudeRangeInMeters, latitudeRangeInMeters);
					final double z = Math.log(rangeInMeters)/Math.log(2);
					final int zoomAsInt = 25 - (int)Math.floor(z);
					zoom = Integer.toString(zoomAsInt);
				}
			}
			str = str.replace(zoomPlaceholder, zoom);
		}

		return str;
	}
	
	/**
	 * @param locationToHighlight
	 * @param gpsDatabase
	 * @return
	 */
	static private String listOfGPSAreas(final HierarchicalCompoundString locationToHighlight,
			                             final GPSDatabase gpsDatabase)
	{
		final StringBuilder s = listOfGPSAreasRecurse((HierarchicalCompoundString)gpsDatabase.getRoot(),
				                                      locationToHighlight,
				                                      gpsDatabase);
		return s.toString();
	}
	
	/**
	 * @param location
	 * @param locationToHighlight
	 * @param gpsDatabase
	 * @return
	 */
	static private StringBuilder listOfGPSAreasRecurse(final HierarchicalCompoundString location,
			                                           final HierarchicalCompoundString locationToHighlight,
			                                           final GPSDatabase gpsDatabase) {
		
		final StringBuilder str = new StringBuilder();
		boolean stringHasBeenAdded = false;
		
		final GPSRecord record =  (GPSRecord)gpsDatabase.getValueAt(location,GPSDatabase.PARAM_GPS_DATA_FOR_MAPPING);
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
				str.append(location.equals(locationToHighlight)?"#FF0000":"#0000FF");
				str.append("\")");
				stringHasBeenAdded = true;				
			}
		}
		
		for ( int i=0; i<gpsDatabase.getChildCount(location); i++) {
			final HierarchicalCompoundString child = (HierarchicalCompoundString)gpsDatabase.getChild(location, i);
			final StringBuilder s = listOfGPSAreasRecurse(child, locationToHighlight, gpsDatabase);
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
