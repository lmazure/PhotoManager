package lmzr.photomngr.ui.action;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.URLEncoder;

import javax.swing.KeyStroke;

import lmzr.photomngr.data.ListSelectionManager;
import lmzr.photomngr.data.PhotoList;
import lmzr.photomngr.data.GPS.GPSData;
import lmzr.photomngr.data.GPS.GPSDatabase;
import lmzr.photomngr.data.GPS.GPSDatabase.GPSRecord;
import lmzr.util.string.HierarchicalCompoundString;

public class ActionLaunchGoogleMaps extends PhotoManagerAction {

	final private GPSDatabase a_GPSDatabase;
	final private PhotoList a_photoList;
	final private ListSelectionManager a_selection;

	/**
	 * @param text
	 * @param mnemonic
	 * @param accelerator
	 * @param tooltipText
	 */
	public ActionLaunchGoogleMaps(final String text,
			                      final int mnemonic,
			                      final KeyStroke accelerator,
			                      final String tooltipText,
			                      final GPSDatabase GPSDatabase,
    			                  final PhotoList photoList,
			                      final ListSelectionManager selection) {
		super(text, mnemonic, accelerator, tooltipText);
		a_GPSDatabase = GPSDatabase;
		a_photoList = photoList;
		a_selection = selection;
	}
	
	/**
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(@SuppressWarnings("unused") final ActionEvent e) {

		final GPSRecord gpsRecord = a_GPSDatabase.getGPSData(((HierarchicalCompoundString)(a_photoList.getValueAt(a_selection.getSelection()[0],PhotoList.PARAM_LOCATION))));

		if (gpsRecord==null) return;
		final GPSData gps = gpsRecord.getGPSData();
		if (!gps.isComplete()) return;

		try {
			final String[] commandLine = { "C:\\Program Files\\Mozilla Firefox\\firefox.exe", 
					"http://maps.google.com/maps?q="+
					+ gps.getLatitudeAsDouble()
					+ "+"
					+ gps.getLongitudeAsDouble()
					+ "+("
					+ URLEncoder.encode(gpsRecord.getLocation().toLongString().replace(">","/"),"UTF-8")
					+")&ll="
					+ gps.getLatitudeAsDouble()
					+ ","
					+ gps.getLongitudeAsDouble()
					+ "&spn="
					+ gps.getLatitudeRangeAsDouble()
					+ ","
					+ gps.getLongitudeRangeAsDouble()
					+ "&t=h&hl=fr" };
			Runtime.getRuntime().exec(commandLine);
		} catch (final IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
}
