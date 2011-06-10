package lmzr.photomngr.ui.mapdisplayer;

import java.net.URI;
import java.net.URISyntaxException;

import lmzr.photomngr.data.GPS.GPSData;
import lmzr.photomngr.data.GPS.GPSDatabase.GPSRecord;

/**
 * @author Laurent
 *
 */
public class BingMapURICreator implements MapURICreator {

	/**
	 * 
	 */
	public BingMapURICreator()
	{
	}

	/**
	 * @see lmzr.photomngr.ui.mapdisplayer.MapURICreator#getName()
	 */
	public String getName()
	{
		return "Bing";
	}

	/**
	 * @see lmzr.photomngr.ui.mapdisplayer.MapURICreator#createMapURLFromGPSData(lmzr.util.string.HierarchicalCompoundString, lmzr.photomngr.data.GPS.GPSData)
	 */
	public URI createMapURIFromGPSData(final GPSRecord record) {
		
		final GPSData data = record.getGPSData();
		
		URI uri = null;
		
		try {
			final double earthRadiusInMeters = 6365000;
			final double longitudeRangeInMeters = ( data.getLatitudeRangeAsDouble() / 180.0)  *  Math.PI * earthRadiusInMeters * Math.cos(data.getLatitudeRangeAsDouble() / 180.0);  
			final double latitudeRangeInMeters = ( data.getLatitudeRangeAsDouble() / 180.0)  *  Math.PI * earthRadiusInMeters;
			final double rangeInMeters = Math.max(longitudeRangeInMeters, latitudeRangeInMeters);
			final double z = Math.log(rangeInMeters)/Math.log(2);
			final int zoom = 25 - (int)Math.floor(z);
			
			final String str = "http://bing.com/maps/default.aspx?cp="
					           + data.getLatitudeAsDouble()
					           + "~"
					           + data.getLongitudeAsDouble()
					           + "&lvl="
					           + zoom
					           + "&style=h";
			uri = new URI(str);
		} catch (final URISyntaxException e) {
			// this should never occur
			e.printStackTrace();
		}

		return uri;
	}


}
