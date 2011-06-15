package lmzr.photomngr.data.filter;

import lmzr.photomngr.data.PhotoList;

/**
 * @author Laurent Mazuré
 */
public class FilterOnCopies extends FilterBase {

    final private long a_min;
    final private long a_max;
    
    /**
     * creates a filter accepting all number of copies
     */
    public FilterOnCopies() {
    	super(false);
    	a_min = 0;
    	a_max = Long.MAX_VALUE;
    }

    /**
     * create a filter accepting the given number of copies
     * @param isEnabled 
     * @param min
     * @param max
     */
    public FilterOnCopies(final boolean isEnabled,
    		              final long min,
                          final long max) {
    	super(isEnabled);
        a_min = min;
        a_max = max;
    }
    
    /**
     * this method shall be called only if the filter is enabled
     * @param list
     * @param index
     * @return does the photo fulfill the filter?
     */
    public boolean filter(final PhotoList list, final int index) {
        final int copies = ((Integer)list.getValueAt(index,PhotoList.PARAM_COPIES)).intValue();
        return ( copies >= a_min) && ( copies <= a_max );
    }
    
    /**
     * @return min number of copies
     */
    public long getMin() {
        return a_min;
    }
    
    /**
     * @return max number of copies
     */
    public long getMax() {
        return a_max;
    }
}
