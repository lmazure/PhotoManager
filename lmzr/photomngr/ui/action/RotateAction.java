package lmzr.photomngr.ui.action;

import java.awt.event.ActionEvent;

import lmzr.photomngr.data.ListSelectionManager;
import lmzr.photomngr.data.PhotoList;

public class RotateAction extends PhotoManagerAction {

	final private PhotoList a_photoList;
	final private float a_value;
	final private ListSelectionManager a_selection;

	/**
	 * @param value
	 * @param photoList 
	 * @param selection 
	 */
	public RotateAction(final float value,
			            final PhotoList photoList,
		 	     	    final ListSelectionManager selection) {
		super("rotate " + value + "°", 0, null, "rotate of " + value + "°");
		a_value = value;
		a_photoList = photoList;
		a_selection = selection;
	}

	@Override
	public void actionPerformed(final ActionEvent arg0) {
    	for (int i=0; i<a_selection.getSelection().length; i++) {
    	    final float r = ((Float)(a_photoList.getValueAt(a_selection.getSelection()[i],PhotoList.PARAM_ROTATION))).floatValue();
    	    a_photoList.setValueAt(new Float(r+a_value),
                                   a_selection.getSelection()[i],
                                   PhotoList.PARAM_ROTATION);
         }
	}
}
