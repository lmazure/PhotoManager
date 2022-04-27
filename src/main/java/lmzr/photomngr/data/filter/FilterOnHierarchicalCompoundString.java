package lmzr.photomngr.data.filter;

import java.util.HashSet;
import java.util.Set;

import lmzr.photomngr.data.PhotoList;
import lmzr.util.string.HierarchicalCompoundString;
import lmzr.util.string.MultiHierarchicalCompoundString;

/**
 *
 */
public class FilterOnHierarchicalCompoundString extends FilterBase {

    private final Set<HierarchicalCompoundString> a_values;
    final private int a_parameter;


    /**
     * @param parameter
     */
    public FilterOnHierarchicalCompoundString(final int parameter) {
        super(false);
        this.a_values = new HashSet<HierarchicalCompoundString>();
        this.a_parameter = parameter;
    }

    /**
     * @param isEnabled
     * @param values
     * @param parameter (i.e. column index in the PhotoList)
     */
    public FilterOnHierarchicalCompoundString(final boolean isEnabled,
                                              final Set<HierarchicalCompoundString> values,
                                              final int parameter) {
        super(isEnabled);
        this.a_values = values;
        this.a_parameter = parameter;
    }

    /**
     * this method shall be called only if the filter is enabled
     * @param list
     * @param index
     * @return does the photo fulfill the filter?
     */
    public boolean filter(final PhotoList list,
                          final int index) {

        final Object value = list.getValueAt(index,this.a_parameter);
        if ( list.getColumnClass(this.a_parameter)==HierarchicalCompoundString.class ) {
            final HierarchicalCompoundString hcs = (HierarchicalCompoundString)value;
            if ( this.a_values.contains(hcs) ) return true;
        } else if ( list.getColumnClass(this.a_parameter)==MultiHierarchicalCompoundString.class ) {
            final MultiHierarchicalCompoundString mhcs = (MultiHierarchicalCompoundString)value;
            for (HierarchicalCompoundString hcs : mhcs.getParts() ) {
                if ( this.a_values.contains(hcs) ) return true;
            }
        } else {
            throw new ClassCastException("filter on HierarchicalCompoundString is corrupted");
        }

        return false;
    }

    /**
     * @return values of the filter
     */
    public Set<HierarchicalCompoundString> getValues() {
        return this.a_values;
    }
}
