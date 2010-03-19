package lmzr.photomngr.ui.action;

import java.awt.event.ActionEvent;

import lmzr.photomngr.data.ListSelectionManager;
import lmzr.photomngr.data.filter.FilteredPhotoList;
import lmzr.photomngr.data.phototrait.PhotoTrait;

/**
 * Action to change a trait of the selected photos
 */
public class ActionChangeTrait extends PhotoManagerAction {

	final private FilteredPhotoList a_photoList;
	final private PhotoTrait a_value;
	final private int a_indexInPhotoList;
	final private ListSelectionManager a_selection;

	/**
	 * @param value
	 * @param name
	 * @param indexInPhotoList
	 * @param photoList 
	 * @param selection 
	 */
	public ActionChangeTrait(final PhotoTrait value,
			                 final String name,
			                 final int indexInPhotoList,
			                 final FilteredPhotoList photoList,
		 	     			 final ListSelectionManager selection) {
		super(value.toString(), 0, null, "set " + name + " to " + value.toString());
		a_value = value;
		a_indexInPhotoList = indexInPhotoList;
		a_photoList = photoList;
		a_selection = selection;
	}


	/**
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(@SuppressWarnings("unused") final ActionEvent e) {
		final int select[] = a_selection.getSelection();
		for (int i=0; i<select.length; i++) {
			a_photoList.setValueAt(a_value,select[i],a_indexInPhotoList);
		}
	}
}