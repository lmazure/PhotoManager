package lmzr.photomngr.ui.action;

import java.awt.event.ActionEvent;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.KeyStroke;

import lmzr.photomngr.data.ListSelectionManager;
import lmzr.photomngr.data.GPS.GPSDatabase;
import lmzr.photomngr.data.filter.FilteredPhotoList;
import lmzr.photomngr.ui.MapTranslationPerformer;
import lmzr.photomngr.ui.SubjectBatchEditor;

/**
 * Action to edit the list of subjects
 *
 * @author Laurent Mazuré
 */
public class EditLocationsAction extends PhotoManagerAction
                                 implements MapTranslationPerformer {

    final private JFrame a_frame;
    final private FilteredPhotoList a_photoList;
    final private GPSDatabase a_GPSDatabase;
    final private ListSelectionManager a_selection;

    /**
     * @param text
     * @param mnemonic
     * @param accelerator
     * @param tooltipText
     * @param frame
     * @param photoList
     * @param selection
     * @param GPSDatabase
     */
    public EditLocationsAction(final String text,
                               final int mnemonic,
                               final KeyStroke accelerator,
                               final String tooltipText,
                               final JFrame frame,
                               final FilteredPhotoList photoList,
                               final ListSelectionManager selection,
                               final GPSDatabase GPSDatabase) {

        super(text, mnemonic, accelerator, tooltipText);
        a_frame = frame;
        a_photoList = photoList;
        a_GPSDatabase = GPSDatabase;
        a_selection = selection;
    }

    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    @Override
    public void actionPerformed(final ActionEvent e) {
        final SubjectBatchEditor editor = new SubjectBatchEditor(a_frame,
                                                                 a_photoList.getLocationFactory().getRootAsHierarchicalCompoundString(),
                                                                 this);
        editor.setVisible(true);
    }

    /**
     * @see lmzr.photomngr.ui.MapTranslationPerformer#performMapTranslation(java.util.Map)
     */
    @Override
    public void performMapTranslation(final Map<String,String> map) {
        a_photoList.performLocationMapTranslation(map,a_selection);
        a_GPSDatabase.performLocationMapTranslation(map);
    }
}