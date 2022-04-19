package lmzr.photomngr.ui.action;

import lmzr.photomngr.data.ListSelectionManager;
import lmzr.photomngr.data.PhotoList;
import lmzr.photomngr.data.phototrait.PhotoQuality;

/**
 * Action to display the quality of the selected photos
 * 
 * @author Laurent Mazuré
 */

public class ChangeQualityAction extends ChangeTraitAction {

	/**
	 * @param value
	 * @param photoList 
	 * @param selection 
	 */
	public ChangeQualityAction(final PhotoQuality value,
                               final PhotoList photoList,
 			                   final ListSelectionManager selection) {
        super(value,"quality",PhotoList.PARAM_QUALITY,photoList,selection);
	}	

}
