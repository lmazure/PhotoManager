package lmzr.photomngr.ui.action;

import java.awt.event.ActionEvent;

import javax.swing.KeyStroke;

import lmzr.photomngr.data.ListSelectionManager;
import lmzr.photomngr.data.PhotoList;
import lmzr.photomngr.data.GPS.GPSDatabase;
import lmzr.photomngr.ui.PhotoNavigator;

/**
 * @author Laurent Mazuré
 */
public class DisplayPhotoNavigatorAction extends PhotoManagerAction {

       final private PhotoList a_photoList;
       final private GPSDatabase a_GPSDatabase;
       final private ListSelectionManager a_selection;
       private PhotoNavigator a_PhotoNavigator;

        /**
         * @param text
         * @param mnemonic
         * @param accelerator
         * @param tooltipText
         * @param photoList
         * @param GPSDatabase
         * @param selection
         */
        public DisplayPhotoNavigatorAction(final String text,
                                           final int mnemonic,
                                           final KeyStroke accelerator,
                                           final String tooltipText,
                                           final PhotoList photoList,
                                           final GPSDatabase GPSDatabase,
                                           final ListSelectionManager selection) {
            super(text, mnemonic, accelerator, tooltipText);
            this.a_photoList = photoList;
            this.a_GPSDatabase = GPSDatabase;
            this.a_selection = selection;
            this.a_PhotoNavigator = null;
        }


        /**
         * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
         */
        @Override
        public void actionPerformed(final ActionEvent e) {

            if ( this.a_PhotoNavigator==null)
                this.a_PhotoNavigator = new PhotoNavigator(this.a_photoList, this.a_GPSDatabase, this.a_selection);

            this.a_PhotoNavigator.setVisible(true);
        }
}
