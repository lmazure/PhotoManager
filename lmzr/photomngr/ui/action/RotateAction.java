package lmzr.photomngr.ui.action;

import java.awt.event.ActionEvent;

import javax.swing.KeyStroke;

import lmzr.photomngr.data.ListSelectionManager;
import lmzr.photomngr.data.PhotoList;

/**
 * @author Laurent
 *
 */
public class RotateAction extends PhotoManagerAction {

	final private PhotoList a_photoList;
	final private float a_value;
	final private ListSelectionManager a_selection;

	/**
	 * @param text 
	 * @param value
	 * @param mnemonic
	 * @param photoList
	 * @param accelerator
	 * @param tooltipText 
	 * @param selection
	 */
	public RotateAction(final String text,
            			final int mnemonic,
                        final KeyStroke accelerator,
                        final String tooltipText,
            			final PhotoList photoList,
		 	     	    final ListSelectionManager selection,
                        final float value) {
		super(text, mnemonic, accelerator, tooltipText);
		a_value = value;
		a_photoList = photoList;
		a_selection = selection;
	}

	@Override
	public void actionPerformed(final ActionEvent arg0) {
    	for (int i=0; i<a_selection.getSelection().length; i++) {
    	    final float r = ((Float)(a_photoList.getValueAt(a_selection.getSelection()[i],PhotoList.PARAM_ROTATION))).floatValue();
    	    a_photoList.setValueAt(Float.valueOf(r+a_value),
                                   a_selection.getSelection()[i],
                                   PhotoList.PARAM_ROTATION);
         }
	}
}
