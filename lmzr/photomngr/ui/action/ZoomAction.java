package lmzr.photomngr.ui.action;

import java.awt.event.ActionEvent;

import lmzr.photomngr.data.ListSelectionManager;
import lmzr.photomngr.data.PhotoList;

/**
 * @author Laurent
 *
 */
public class ZoomAction extends PhotoManagerAction {

	final private PhotoList a_photoList;
	final private float a_value;
	final private ListSelectionManager a_selection;

	/**
	 * @param value
	 * @param photoList 
	 * @param selection 
	 */
	public ZoomAction(final float value,
			          final PhotoList photoList,
		 	   	      final ListSelectionManager selection) {
		super("multiply zoom by " + value, 0, null, "multiply zoom by " + value);
		a_value = value;
		a_photoList = photoList;
		a_selection = selection;
	}

	@Override
	public void actionPerformed(final ActionEvent arg0) {
    	for (int i=0; i<a_selection.getSelection().length; i++) {
    	    final float z = ((Float)(a_photoList.getValueAt(a_selection.getSelection()[i],PhotoList.PARAM_ZOOM))).floatValue();
    	    a_photoList.setValueAt(new Float(z*a_value),
                                   a_selection.getSelection()[i],
                                   PhotoList.PARAM_ZOOM);
         }
	}
}
