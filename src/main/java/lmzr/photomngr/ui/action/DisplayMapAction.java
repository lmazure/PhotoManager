package lmzr.photomngr.ui.action;

import java.awt.Desktop;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.io.IOException;

import javax.swing.KeyStroke;

import lmzr.photomngr.data.ListSelectionManager;
import lmzr.photomngr.data.PhotoList;
import lmzr.photomngr.data.GPS.GPSDatabase;
import lmzr.photomngr.data.GPS.GPSDatabase.GPSRecord;
import lmzr.photomngr.ui.mapdisplayer.MapURICreator;
import lmzr.util.string.HierarchicalCompoundString;

/**
 * @author Laurent Mazuré
 */
public class DisplayMapAction extends PhotoManagerAction {

    final private GPSDatabase a_GPSDatabase;
    final private PhotoList a_photoList;
    final private ListSelectionManager a_selection;
    final private MapURICreator a_uriCreator;

    /**
     * @param text
     * @param mnemonic
     * @param accelerator
     * @param tooltipText
     * @param GPSDatabase
     * @param photoList
     * @param selection
     * @param mapURICreator
     */
    public DisplayMapAction(final String text,
                            final int mnemonic,
                            final KeyStroke accelerator,
                            final String tooltipText,
                            final GPSDatabase GPSDatabase,
                            final PhotoList photoList,
                            final ListSelectionManager selection,
                            final MapURICreator mapURICreator) {
        super(text, mnemonic, accelerator, tooltipText);
        a_GPSDatabase = GPSDatabase;
        a_photoList = photoList;
        a_selection = selection;
        a_uriCreator = mapURICreator;
    }

    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    @Override
    public void actionPerformed(final ActionEvent e) {

        final HierarchicalCompoundString location = (HierarchicalCompoundString)(a_photoList.getValueAt(a_selection.getSelection()[0],
                                                                                 PhotoList.PARAM_LOCATION));
        final GPSRecord data = a_GPSDatabase.getGPSData(location);

        try {
            Desktop.getDesktop().browse(a_uriCreator.createMapURIFromGPSData(data));
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
}
