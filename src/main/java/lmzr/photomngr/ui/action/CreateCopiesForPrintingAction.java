package lmzr.photomngr.ui.action;

import java.awt.event.ActionEvent;

import javax.swing.JFrame;
import javax.swing.KeyStroke;

import lmzr.photomngr.data.PhotoList;
import lmzr.photomngr.data.filter.FilteredPhotoList;
import lmzr.photomngr.exporter.Exporter;

/**
 * Action to create exportable copies
 *
 * @author Laurent Mazuré
 */
public class CreateCopiesForPrintingAction extends PhotoManagerAction {

    final private PhotoList a_photoList;
    final private JFrame a_frame;

    /**
     * @param text
     * @param mnemonic
     * @param accelerator
     * @param tooltipText
     * @param frame
     * @param photoList
     */
    public CreateCopiesForPrintingAction(final String text,
                                         final int mnemonic,
                                         final KeyStroke accelerator,
                                         final String tooltipText,
                                         final JFrame frame,
                                         final  FilteredPhotoList photoList) {
        super(text, mnemonic, accelerator, tooltipText);
        this.a_frame = frame;
        this.a_photoList = photoList;
    }


    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    @Override
    public void actionPerformed(final ActionEvent e) {

        final Exporter exporter = new Exporter(this.a_frame);
        exporter.export(this.a_photoList);
    }
}