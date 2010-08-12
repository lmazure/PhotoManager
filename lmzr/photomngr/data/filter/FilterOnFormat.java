package lmzr.photomngr.data.filter;

import lmzr.photomngr.data.DataFormat;
import lmzr.photomngr.data.PhotoList;

/**
 * @author Laurent
 *
 */
public class FilterOnFormat extends FilterBase {


    final private DataFormat[] a_formats;
    final private boolean[] a_values;
    
    /**
     * creates a filter accepting all formats
     * @param formats
     */
    public FilterOnFormat(final DataFormat formats[]) {
    	super(false);
        a_formats = formats;
        a_values = new boolean[formats.length];
        for (int i=0; i<formats.length; i++) a_values[i]=true;
    }

    /**
     * creates a filter with the specified formats
     * @param isEnabled 
     * @param formats
     * @param values
     */
    public FilterOnFormat(final boolean isEnabled,
    		              final DataFormat formats[],
                          final boolean values[]) {
    	super(isEnabled);
        if (formats.length!=values.length) throw new AssertionError("filter on format is corrupted");
        a_formats = formats;
        a_values = values;
    }

    /**
     * this method shall be called only if the filter is enabled
     * @param list
     * @param index
     * @return does the photo fulfill the filter?
     */
    public boolean filter(final PhotoList list, final int index) {
        final DataFormat format = (DataFormat)list.getValueAt(index,PhotoList.PARAM_FORMAT);
        int i = 0;
        while ( format != a_formats[i] ) i++;
        return a_values[i];
    }
    
    /**
     * @return formats handled by this filter
     */
    public DataFormat[] getFormats() {
        return a_formats;
    }
    
    /**
     * @return values of the filter
     */
    public boolean[] getValues() {
        return a_values;
    }
}
