package lmzr.photomngr.ui.action;

import java.awt.event.ActionEvent;

import javax.swing.JFrame;
import javax.swing.KeyStroke;

import lmzr.photomngr.data.filter.FilteredPhotoList;

/**
 * Action to export the list of subjects
 *
 * @author Laurent Mazuré
 */
public class ExportSubjectsAction extends ExportAction {

    final private FilteredPhotoList a_photoList;

    /**
     * @param text
     * @param mnemonic
     * @param accelerator
     * @param tooltipText
     * @param frame
     * @param photoList
     */
    public ExportSubjectsAction(final String text,
                                final int mnemonic,
                                final KeyStroke accelerator,
                                final String tooltipText,
                                final JFrame frame,
                                final  FilteredPhotoList photoList) {
        super(text, mnemonic, accelerator, tooltipText, frame);
        a_photoList = photoList;
    }

    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    @Override
    public void actionPerformed(final ActionEvent e) {
        dumpRoot(a_photoList.getSubjectFactory().getRootAsHierarchicalCompoundString());
    }
}