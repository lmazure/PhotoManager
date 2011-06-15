package lmzr.photomngr.ui.action;

import java.awt.Desktop;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.KeyStroke;

import lmzr.photomngr.data.ListSelectionManager;
import lmzr.photomngr.data.PhotoList;
import lmzr.photomngr.data.GPS.GPSDatabase;
import lmzr.photomngr.ui.mapdisplayer.GoogleMapsURICreator;
import lmzr.util.string.HierarchicalCompoundString;

/**
 * display the GPS coordinates in Google Maps
 * @author Laurent Mazuré
 */
public class DisplayGPSAreasInGoogleMapsAction extends PhotoManagerAction {

	final private GPSDatabase a_GPSDatabase;
	final private PhotoList a_photoList;
	final private ListSelectionManager a_selection;
	final private  String a_cacheDirectory;
	
	/**
	 * @param text
	 * @param mnemonic
	 * @param accelerator
	 * @param tooltipText
	 * @param GPSDatabase 
	 * @param photoList 
	 * @param selection 
	 * @param cacheDirectory 
	 */
	public DisplayGPSAreasInGoogleMapsAction(final String text,
			                                 final int mnemonic,
			                                 final KeyStroke accelerator,
			                                 final String tooltipText,
			                                 final GPSDatabase GPSDatabase,
			                                 final PhotoList photoList,
			                                 final ListSelectionManager selection,
			                                 final String cacheDirectory) {
		super(text, mnemonic, accelerator, tooltipText);
		a_GPSDatabase = GPSDatabase;
		a_photoList = photoList;
		a_selection = selection;
		a_cacheDirectory = cacheDirectory;
	}
	
	/**
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(final ActionEvent e) {

		final HierarchicalCompoundString location = (HierarchicalCompoundString)(a_photoList.getValueAt(a_selection.getSelection()[0],
				                                                                                        PhotoList.PARAM_LOCATION));

		final String folder = (String)a_photoList.getValueAt(a_selection.getSelection()[0], PhotoList.PARAM_FOLDER);
		final String filename = "displayGPSAreasInGoogleMaps_" + escape(location.toLongString()) + ".html";
		final File file = new File(a_cacheDirectory + File.separator + folder + File.separator + filename);		

		final GoogleMapsURICreator creator = new GoogleMapsURICreator();
		
		try {
			creator.createMapURIForGPSDebug(file, location, a_GPSDatabase);
		} catch (final IOException e1) {
			System.err.println("failed to generate file for debugging GPS");			
			e1.printStackTrace();
		}

		try {
			Desktop.getDesktop().browse(file.toURI());
		} catch (final HeadlessException ex) {
			System.err.println("failed to start a browser to display the map of "+location.toString());
			ex.printStackTrace();
		} catch (final UnsupportedOperationException ex) {
			System.err.println("failed to start a browser to display the map of "+location.toString());
			ex.printStackTrace();
		} catch (final IOException ex) {
			System.err.println("failed to start a browser to display the map of "+location.toString());
			ex.printStackTrace();
		}
	}

	/**
	 * generate a legal filename from a string
	 * @param string
	 * @return a acceptable filename (dubious characters are replaced by "_"
	 */
	private String escape(final String string) {
		
		int l = string.length();
		final char[] chars = new char[l];
		string.getChars(0,l,chars,0);
		
		for ( int i = 0; i<l; i++ ) {
			if ( !Character.isLetterOrDigit(chars[i]) ) {
				chars[i] = '_';
			}
		}
		
		return new String(chars);
	}

}
