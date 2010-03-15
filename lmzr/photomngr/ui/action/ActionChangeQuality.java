package lmzr.photomngr.ui.action;

import lmzr.photomngr.data.ListSelectionManager;
import lmzr.photomngr.data.PhotoList;
import lmzr.photomngr.data.filter.FilteredPhotoList;
import lmzr.photomngr.data.phototrait.PhotoQuality;

/**
 * Action to display the quality of the selected photos
 */
public class ActionChangeQuality extends ActionChangeTrait {

	/**
	 * @param value
	 * @param photoList 
	 * @param selection 
	 */
	public ActionChangeQuality(final PhotoQuality value,
                               final FilteredPhotoList photoList,
 			                   final ListSelectionManager selection) {
        super(value,"quality",PhotoList.PARAM_QUALITY,photoList,selection);
	}	

}
