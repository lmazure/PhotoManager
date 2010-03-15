package lmzr.photomngr.ui.mapdisplayer;

import lmzr.photomngr.data.GPS.GPSData;
import lmzr.util.string.HierarchicalCompoundString;

public interface MapDisplayer {

	public void displayMap(final HierarchicalCompoundString location, 
                           final GPSData GPSData);

}
