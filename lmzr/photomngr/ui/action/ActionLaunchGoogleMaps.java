package lmzr.photomngr.ui.action;

import java.awt.event.ActionEvent;

import javax.swing.KeyStroke;

import lmzr.photomngr.data.ListSelectionManager;
import lmzr.photomngr.data.PhotoList;
import lmzr.photomngr.data.GPS.GPSDatabase;
import lmzr.photomngr.data.GPS.GPSDatabase.GPSRecord;
import lmzr.photomngr.ui.mapdisplayer.GoogleMapDisplayer;
import lmzr.util.string.HierarchicalCompoundString;

/**
 * @author Laurent
 *
 */
public class ActionLaunchGoogleMaps extends PhotoManagerAction {

	final private GPSDatabase a_GPSDatabase;
	final private PhotoList a_photoList;
	final private ListSelectionManager a_selection;

	/**
	 * @param text
	 * @param mnemonic
	 * @param accelerator
	 * @param tooltipText
	 * @param GPSDatabase 
	 * @param photoList 
	 * @param selection 
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

		final HierarchicalCompoundString location = (HierarchicalCompoundString)(a_photoList.getValueAt(a_selection.getSelection()[0],PhotoList.PARAM_LOCATION));
		final GPSRecord gpsRecord = a_GPSDatabase.getGPSData(location);

		if ( gpsRecord==null ) return;
		
		(new GoogleMapDisplayer()).displayMap(location, gpsRecord.getGPSData());
		
	}
}
