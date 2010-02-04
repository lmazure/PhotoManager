package lmzr.photomngr.data.filter;

import lmzr.photomngr.data.PhotoList;

/**
 * @author Laurent
 *
 */
public class FilterOnCopies {


    final private long a_min;
    final private long a_max;
    
    /**
     * creates a filter accepting all number of copies
     */
    public FilterOnCopies() {
    	a_min = 0;
    	a_max = Long.MAX_VALUE;
    }

    /**
     * create a filter accepting the given number of copies
     * @param min
     * @param max
     */
    public FilterOnCopies(final long min,
                          final long max) {
        a_min = min;
        a_max = max;
    }
    
    /**
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
        
    /**
     * @return indicates if the filter is enabled
     */
    public boolean isEnabled() {
    	if (a_min>0) return true;
    	if (a_max<Long.MAX_VALUE) return true;
    	return false;
    }
}
