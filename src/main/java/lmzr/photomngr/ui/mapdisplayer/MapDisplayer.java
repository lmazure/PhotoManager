package lmzr.photomngr.ui.mapdisplayer;

import lmzr.photomngr.data.GPS.GPSData;
import lmzr.util.string.HierarchicalCompoundString;

/**
 * @author Laurent Mazuré
 */
public interface MapDisplayer {

	/**
	 * @param location
	 * @param GPSData
	 */
	public void displayMap(final HierarchicalCompoundString location, 
                           final GPSData GPSData);

}
