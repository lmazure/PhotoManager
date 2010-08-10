package lmzr.photomngr.ui.treeSelectioner;

import java.util.Set;

import lmzr.util.string.HierarchicalCompoundString;
import lmzr.util.string.HierarchicalCompoundStringFactory;

import org.jdesktop.swingx.JXTreeTable;

public class TreeSelectioner extends JXTreeTable {
	
	//TODO remplacer par un enum
	public static int MODE_MONO_SELECTION = 1;
	public static int MODE_MULTI_SELECTION_WITH_SELECT_ALL_COLUMN = 2;
	public static int MODE_MULTI_SELECTION_WITHOUT_SELECT_ALL_COLUMN = 3;
	
	/**
	 * @param database
	 */
	public TreeSelectioner(final String dataDescription,
                           final HierarchicalCompoundStringFactory compoundStringFactory,
                           final int mode) {
		
		super(new DatabaseForTreeSelectioner(dataDescription, compoundStringFactory, mode));
	}

	/**
	 * @return
	 */
	public Set<HierarchicalCompoundString> getSelection() {
		return ((DatabaseForTreeSelectioner)getTreeTableModel()).getSelection();
	}
	
	/**
	 * @param selection
	 */
	public void setSelection(final Set<HierarchicalCompoundString> selection) {
		((DatabaseForTreeSelectioner)getTreeTableModel()).setSelection(selection);
	}

}
