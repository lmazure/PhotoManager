package lmzr.photomngr.data.filter;

import lmzr.photomngr.data.PhotoList;
import lmzr.photomngr.data.phototrait.PhotoTrait;

/**
 * 
 */
public class FilterOnPhotoTrait extends FilterBase {

    final private PhotoTrait[] a_traits;
    final private boolean[] a_values;
    final private int a_parameter; 
    
    /**
     * creates a filter accepting all the trait values
     * @param traits
     * @param parameter
     */
    public FilterOnPhotoTrait(final PhotoTrait traits[],
                              final int parameter) {
    	super(false);
        a_traits = traits;
        a_values = new boolean[traits.length];
        for (int i=0; i<traits.length; i++) a_values[i]=true;
        a_parameter = parameter;
    }

    /**
     * creates a filter with the specified value
     * @param isEnabled 
     * @param traits
     * @param values
     * @param parameter (i.e. column index in the PhotoList)
     */
    public FilterOnPhotoTrait(final boolean isEnabled,
    		                  final PhotoTrait traits[],
                              final boolean values[],
                              final int parameter) {
    	super(isEnabled);
        if (traits.length!=values.length) throw new AssertionError("filter on trait is corrupted");
        a_traits = traits;
        a_values = values;
        a_parameter = parameter;
    }

    /**
     * this method shall be called only if the filter is enabled
     * @param list
     * @param index
     * @return does the photo fulfill the filter?
     */
    public boolean filter(final PhotoList list,
                          final int index) {
        final PhotoTrait trait = (PhotoTrait)list.getValueAt(index,a_parameter);
        for (int i =0; i<a_traits.length; i++) {
            if ( trait.equals(a_traits[i]) ) return a_values[i];
        }
        throw new AssertionError("filter on trait is corrupted");
    }
    
    /**
     * @return traits handled by this filter
     */
    public PhotoTrait[] getTraits() {
        return a_traits;
    }
    
    /**
     * @return values of the filter
     */
    public boolean[] getValues() {
        return a_values;
    }
    
    /**
     * @return parameter (i.e. column index) in the PhotoList
     */
    public int getParameter() {
        return a_parameter;
    }
}
