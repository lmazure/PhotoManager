package lmzr.photomngr.data.filter;

import java.util.Set;

import lmzr.photomngr.data.PhotoList;
import lmzr.util.string.HierarchicalCompoundString;
import lmzr.util.string.MultiHierarchicalCompoundString;

/**
 *
 */
public class FilterOnHierarchicalCompoundString {

	private final Set<HierarchicalCompoundString> a_values;
    final private int a_parameter; 

	/**
	 * @param values
     * @param parameter (i.e. column index in the PhotoList)
	 */
	public FilterOnHierarchicalCompoundString(final Set<HierarchicalCompoundString> values,
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
        
    	final Object value = list.getValueAt(index,a_parameter);
    	if ( list.getColumnClass(a_parameter)==HierarchicalCompoundString.class ) {
            final HierarchicalCompoundString hcs = (HierarchicalCompoundString)value;
            if ( a_values.contains(hcs) ) return true;
    	} else if ( list.getColumnClass(a_parameter)==MultiHierarchicalCompoundString.class ) {
            final MultiHierarchicalCompoundString mhcs = (MultiHierarchicalCompoundString)value;
        	for (HierarchicalCompoundString hcs : mhcs.getParts() ) {
                if ( a_values.contains(hcs) ) return true;
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
    public Set<HierarchicalCompoundString> getValues() {
    	return a_values;
    }
    
    /**
     * @return indicate if the filter is enabled
     */
    public boolean isEnabled() {
    	return ( a_values.size()>0 );
    }
}
