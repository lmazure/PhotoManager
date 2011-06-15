package lmzr.photomngr.ui.action;

import lmzr.photomngr.data.ListSelectionManager;
import lmzr.photomngr.data.PhotoList;
import lmzr.photomngr.data.filter.FilteredPhotoList;
import lmzr.photomngr.data.phototrait.PhotoOriginality;

/**
 * Action to display the originality of the selected photos
 * @author Laurent Mazuré
*/

public class ChangeOriginalityAction extends ChangeTraitAction {

	/**
	 * @param value
	 * @param photoList 
	 * @param selection 
	 */
	public ChangeOriginalityAction(final PhotoOriginality value,
                                   final FilteredPhotoList photoList,
 			                       final ListSelectionManager selection) {
        super(value,"originality",PhotoList.PARAM_ORIGINALITY,photoList,selection);
	}	

}
