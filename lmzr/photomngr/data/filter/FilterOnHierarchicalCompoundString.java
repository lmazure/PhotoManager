package lmzr.photomngr.data.filter;

import lmzr.photomngr.data.PhotoList;
import lmzr.util.string.HierarchicalCompoundString;
import lmzr.util.string.MultiHierarchicalCompoundString;

/**
 *
 */
public class FilterOnHierarchicalCompoundString {

	private final HierarchicalCompoundString a_values[];
    final private int a_parameter; 

	/**
	 * @param values
     * @param parameter (i.e. column index in the PhotoList)
	 */
	public FilterOnHierarchicalCompoundString(final HierarchicalCompoundString values[],
			                                  final int parameter) {
		a_values = values;
		a_parameter = parameter;
	}
	
    /**
     * this method shall be called only if the filter is enabled
     * @param list
     * @param index
     * @return does the photo fulfill the filter?
     */
    public boolean filter(final PhotoList list, final int index) {
        
    	if (a_values==null) return true;
    	final Object value = list.getValueAt(index,a_parameter);
    	if ( list.getColumnClass(a_parameter)==HierarchicalCompoundString.class ) {
            final HierarchicalCompoundString hcs = (HierarchicalCompoundString)value;
            for (int i=0; i<a_values.length; i++) {
            	HierarchicalCompoundString l = hcs;
            	while ( l!=null ) {
            		if ( l==a_values[i] ) return true;
            		l = l.getParent();
            	}
            }
    	} else if ( list.getColumnClass(a_parameter)==MultiHierarchicalCompoundString.class ) {
            final MultiHierarchicalCompoundString mhcs = (MultiHierarchicalCompoundString)value;
            for (int i=0; i<a_values.length; i++) {
            	for (int j=0; j<mhcs.getParts().length; j++) {
            	HierarchicalCompoundString l = mhcs.getParts()[j];
            	    while ( l!=null ) {
            	    	if ( l==a_values[i] ) return true;
            	    	l = l.getParent();
            	    }
            	}
            }
    	} else {
    		throw new ClassCastException("filter on HierarchicalCompoundString is corrupted");
    	}

    	return false;
    }

    /**
     * @return parameter (i.e. column index) in the PhotoList
     */
    public int getParameter() {
        return a_parameter;
    }
    
    /**
     * @return values of the filter
     */
    public HierarchicalCompoundString[] getValues() {
    	return a_values;
    }
    
    /**
     * @return indicate if the filter is enabled
     */
    public boolean isEnabled() {
    	return a_values!=null;
    }
}
