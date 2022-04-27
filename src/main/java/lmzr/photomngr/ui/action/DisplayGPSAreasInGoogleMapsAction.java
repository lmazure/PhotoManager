package lmzr.photomngr.ui.action;

import java.awt.Desktop;
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
 *
 * @author Laurent Mazuré
 */
public class DisplayGPSAreasInGoogleMapsAction extends PhotoManagerAction {

    final private GPSDatabase gpsDatabase;
    final private PhotoList photoList;
    final private ListSelectionManager selection;
    final private String cacheDirectory;

    /**
     * @param text
     * @param mnemonic
     * @param accelerator
     * @param tooltipText
     * @param gpsDatabase
     * @param photoList
     * @param selection
     * @param cacheDirectory
     */
    public DisplayGPSAreasInGoogleMapsAction(final String text,
                                             final int mnemonic,
                                             final KeyStroke accelerator,
                                             final String tooltipText,
                                             final GPSDatabase gpsDatabase,
                                             final PhotoList photoList,
                                             final ListSelectionManager selection,
                                             final String cacheDirectory) {
        super(text, mnemonic, accelerator, tooltipText);
        this.gpsDatabase = gpsDatabase;
        this.photoList = photoList;
        this.selection = selection;
        this.cacheDirectory = cacheDirectory;
    }

    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    @Override
    public void actionPerformed(final ActionEvent e) {

        final HierarchicalCompoundString location = (HierarchicalCompoundString)(this.photoList.getValueAt(this.selection.getSelection()[0],
                                                                                                           PhotoList.PARAM_LOCATION));

        final String folder = (String)this.photoList.getValueAt(this.selection.getSelection()[0], PhotoList.PARAM_FOLDER);
        final String filename = "displayGPSAreasInGoogleMaps_" + escape(location.toLongString()) + ".html";

        try {
            final File file = GoogleMapsURICreator.createMapURIForGPSDebug(this.cacheDirectory, folder, filename, this.photoList, location, this.gpsDatabase);
            Desktop.getDesktop().browse(file.toURI());
        } catch (final IOException ex) {
            System.err.println("failed to generate file for debugging GPS");
            ex.printStackTrace();
        } catch (final Exception ex) {
            System.err.println("failed to start a browser to display the map of "+location.toString());
            ex.printStackTrace();
        }
    }

    /**
     * generate a legal filename from a string
     * @param string
     * @return an acceptable filename (dubious characters are replaced by "_")
     */
    private static String escape(final String string) {

        final int l = string.length();
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
