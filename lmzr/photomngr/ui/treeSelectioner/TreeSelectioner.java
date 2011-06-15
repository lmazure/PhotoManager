package lmzr.photomngr.ui.treeSelectioner;

import java.util.Set;

import lmzr.util.string.HierarchicalCompoundString;
import lmzr.util.string.HierarchicalCompoundStringFactory;

import org.jdesktop.swingx.JXTreeTable;

/**
 * @author Laurent Mazuré
 */
public class TreeSelectioner extends JXTreeTable {

	//TODO remplacer par un enum

	/**
	 * 
	 */
	final public static int MODE_MONO_SELECTION = 1;

	/**
	 * 
	 */
	final public static int MODE_MULTI_SELECTION_WITH_SELECT_ALL_COLUMN = 2;
	
	/**
	 * 
	 */
	final public static int MODE_MULTI_SELECTION_WITHOUT_SELECT_ALL_COLUMN = 3;
	
	/**
	 * @param dataDescription 
	 * @param compoundStringFactory 
	 * @param mode
	 */
	public TreeSelectioner(final String dataDescription,
                           final HierarchicalCompoundStringFactory compoundStringFactory,
                           final int mode) {
		
		super(new DatabaseForTreeSelectioner(dataDescription, compoundStringFactory, mode));

		getColumnModel().getColumn(DatabaseForTreeSelectioner.PARAM_VALUE).setPreferredWidth(500);
		getColumnModel().getColumn(DatabaseForTreeSelectioner.PARAM_SELECTED).setPreferredWidth(30);
		getColumnModel().getColumn(DatabaseForTreeSelectioner.PARAM_SELECT_ALL).setPreferredWidth(30);

		moveColumn(DatabaseForTreeSelectioner.PARAM_VALUE, DatabaseForTreeSelectioner.PARAM_SELECT_ALL);
		
		if ( mode != MODE_MULTI_SELECTION_WITH_SELECT_ALL_COLUMN ) {
			removeColumn(getColumnModel().getColumn(1));
		}
	}

	/**
	 * @return current selection
	 */
	public Set<HierarchicalCompoundString> getSelection() {
		return ((DatabaseForTreeSelectioner)getTreeTableModel()).getSelection();
	}
	
	/**
	 * @param selection
	 */
	public void setSelection(final Set<HierarchicalCompoundString> selection) {
		
		((DatabaseForTreeSelectioner)getTreeTableModel()).setSelection(selection);
		
		for (HierarchicalCompoundString h : selection ) {
			expandPath(HierarchicalCompoundStringFactory.getPath(h.getParent()));
		}
	}

}
