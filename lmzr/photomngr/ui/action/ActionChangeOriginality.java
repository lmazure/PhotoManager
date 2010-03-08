package lmzr.photomngr.ui.action;

import lmzr.photomngr.data.ListSelectionManager;
import lmzr.photomngr.data.PhotoList;
import lmzr.photomngr.data.filter.FilteredPhotoList;
import lmzr.photomngr.data.phototrait.PhotoOriginality;

/**
 * Action to display the originality of the selected photos
 */
public class ActionChangeOriginality extends ActionChangeTrait {

	/**
	 * @param value
	 */
	public ActionChangeOriginality(final PhotoOriginality value,
            final FilteredPhotoList photoList,
 			final ListSelectionManager selection) {
        super(value,"originality",PhotoList.PARAM_ORIGINALITY,photoList,selection);
	}	

}
