package lmzr.photomngr.ui.mapdisplayer;

import java.net.URI;
import java.net.URISyntaxException;

import lmzr.photomngr.data.GPS.GPSData;
import lmzr.photomngr.data.GPS.GPSDatabase.GPSRecord;

/**
 * @author Laurent
 *
 */
public class GeoportailMapURICreator implements MapURICreator {
	
	/**
	 * 
	 */
	public GeoportailMapURICreator()
	{
	}

	/**
	 * @see lmzr.photomngr.ui.mapdisplayer.MapURICreator#getName()
	 */
	public String getName()
	{
		return "Geoportail";
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
			final int zoom = (int)Math.floor(z) - 2;
			
			final String str = "http://www.geoportail.fr/?c="
					           + data.getLongitudeAsDouble()
					           + ","
					           + data.getLatitudeAsDouble()
					           + "&l=Scan&z="
					           + zoom;
			uri = new URI(str);
		} catch (final URISyntaxException e) {
			// this should never occur
			e.printStackTrace();
		}

		return uri;
	}

}
