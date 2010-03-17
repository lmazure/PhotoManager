package lmzr.photomngr.ui.mapdisplayer;

import java.io.IOException;
import java.net.URLEncoder;

import lmzr.photomngr.data.GPS.GPSData;
import lmzr.util.string.HierarchicalCompoundString;

/**
 * @author Laurent
 *
 */
public class GoogleMapDisplayer implements MapDisplayer {

	/**
	 * 
	 */
	public GoogleMapDisplayer() {
		super();
	}
	
	/**
	 * @see lmzr.photomngr.ui.mapdisplayer.MapDisplayer#displayMap(lmzr.util.string.HierarchicalCompoundString, lmzr.photomngr.data.GPS.GPSData)
	 */
	@Override
	public void displayMap(final HierarchicalCompoundString location, 
			               final GPSData GPSData) {

		if (GPSData==null) return;
		if (!GPSData.isComplete()) return;

		try {
			final String[] commandLine = { "C:\\Program Files\\Mozilla Firefox\\firefox.exe", 
					"http://maps.google.com/maps?q="+
					+ GPSData.getLatitudeAsDouble()
					+ "+"
					+ GPSData.getLongitudeAsDouble()
					+ "+("
					+ URLEncoder.encode(location.toLongString().replace(">","/"),"UTF-8")
					+")&ll="
					+ GPSData.getLatitudeAsDouble()
					+ ","
					+ GPSData.getLongitudeAsDouble()
					+ "&spn="
					+ GPSData.getLatitudeRangeAsDouble()
					+ ","
					+ GPSData.getLongitudeRangeAsDouble()
					+ "&t=h&hl=fr" };
			Runtime.getRuntime().exec(commandLine);
		} catch (final IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();	}
	}

}
