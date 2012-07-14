package lmzr.photomngr.ui.action;

import java.awt.event.ActionEvent;

import lmzr.photomngr.data.ListSelectionManager;
import lmzr.photomngr.data.PhotoList;

/**
 * @author Laurent
 *
 */
public class ResetGeometryAction extends PhotoManagerAction {

	final private PhotoList a_photoList;
	final private ListSelectionManager a_selection;

	/**
	 * @param photoList 
	 * @param selection 
	 */
	public ResetGeometryAction(final PhotoList photoList,
		 	   	               final ListSelectionManager selection) {
		super("reset geometry", 0, null, "reset geometry");
		a_photoList = photoList;
		a_selection = selection;
	}

	@Override
	public void actionPerformed(final ActionEvent arg0) {
    	for (int i=0; i<a_selection.getSelection().length; i++) {
    	    a_photoList.setValueAt(new Float(1.0f),
                                   a_selection.getSelection()[i],
                                   PhotoList.PARAM_ZOOM);
    	    a_photoList.setValueAt(new Float(0.0f),
                                   a_selection.getSelection()[i],
                                   PhotoList.PARAM_ROTATION);
    	    a_photoList.setValueAt(new Float(0.0f),
                                   a_selection.getSelection()[i],
                                   PhotoList.PARAM_FOCUS_X);
    	    a_photoList.setValueAt(new Float(0.0f),
                                   a_selection.getSelection()[i],
                                   PhotoList.PARAM_FOCUS_Y);
         }
	}
}
