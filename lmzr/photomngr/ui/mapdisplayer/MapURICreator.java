package lmzr.photomngr.ui.mapdisplayer;

import java.net.URI;

import lmzr.photomngr.data.GPS.GPSDatabase.GPSRecord;

/**
 * @author Laurent Mazuré
 */
public interface MapURICreator {

	/**
	 * @param record
	 * @return URL displaying the corresponding map
	 */
	public URI createMapURIFromGPSData(final GPSRecord record);
	
	/**
	 * @return name of the site
	 */
	public String getName();
}
