package lmzr.photomngr.ui.action;

import lmzr.photomngr.data.ListSelectionManager;
import lmzr.photomngr.data.PhotoList;
import lmzr.photomngr.data.filter.FilteredPhotoList;
import lmzr.photomngr.data.phototrait.PhotoPrivacy;

/**
 * Action to display the privacy of the selected photos
 * @author Laurent Mazuré
 */

public class ChangePrivacyAction extends ChangeTraitAction {

	/**
	 * @param value
	 * @param photoList 
	 * @param selection 
	 */
	public ChangePrivacyAction(final PhotoPrivacy value,
                               final FilteredPhotoList photoList,
 			                   final ListSelectionManager selection) {
        super(value,"privacy",PhotoList.PARAM_PRIVACY,photoList,selection);
	}	

}
