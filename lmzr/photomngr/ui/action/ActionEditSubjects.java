package lmzr.photomngr.ui.action;

import java.awt.event.ActionEvent;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.KeyStroke;

import lmzr.photomngr.data.filter.FilteredPhotoList;
import lmzr.photomngr.ui.MapTranslationPerformer;
import lmzr.photomngr.ui.SubjectBatchEditor;

/**
 * Action to edit the list of subjects
 */
public class ActionEditSubjects extends PhotoManagerAction
                                implements MapTranslationPerformer {

	final private JFrame a_frame;
	final private FilteredPhotoList a_photoList;

	/**
	 * @param text
	 * @param mnemonic
	 * @param accelerator
	 * @param tooltipText
	 */
	public ActionEditSubjects(final String text,
			final int mnemonic,
			final KeyStroke accelerator,
			final String tooltipText,
			final JFrame frame,
			final FilteredPhotoList photoList) {
		
		super(text, mnemonic, accelerator, tooltipText);
		a_frame = frame;
		a_photoList = photoList;
	}

	/**
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(@SuppressWarnings("unused") final ActionEvent e) {
		
		final SubjectBatchEditor editor = new SubjectBatchEditor(a_frame,
				                                                 a_photoList.getSubjectFactory().getRootAsHierarchicalCompoundString(),
				                                                 this);
		editor.setVisible(true);
	}

	/**
	 * @see lmzr.photomngr.ui.MapTranslationPerformer#performMapTranslation(java.util.Map)
	 */
	public void performMapTranslation(final Map<String,String> map) {
		a_photoList.performSubjectMapTranslation(map);
	}
}