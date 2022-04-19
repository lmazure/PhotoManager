package lmzr.photomngr.ui;

import javax.swing.JTree;

import lmzr.util.string.HierarchicalCompoundString;
import lmzr.util.string.MultiHierarchicalCompoundStringFactory;

/**
 * 
 */
public class MultiHierarchicalCompoundStringTreeDisplay extends JTree {

    /**
     * @param factory
     */
    public MultiHierarchicalCompoundStringTreeDisplay(final MultiHierarchicalCompoundStringFactory factory) {
        super(factory.getHierarchicalCompoundStringFactory());
        setRootVisible(false);
    }
    
    /**
     * @see javax.swing.JTree#convertValueToText(java.lang.Object, boolean, boolean, boolean, int, boolean)
     */
    @Override
	public String convertValueToText(final Object value,
    		                         final boolean selected,
    		                         final boolean expanded,
    		                         final boolean leaf,
                                     final int row,
                                     final boolean hasFocus) {
        final HierarchicalCompoundString string = (HierarchicalCompoundString)value; 
        return string.toShortString();
    }
}
