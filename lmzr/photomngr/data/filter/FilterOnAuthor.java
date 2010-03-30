package lmzr.photomngr.data.filter;

import lmzr.photomngr.data.PhotoList;

/**
 * @author Laurent
 *
 */
public class FilterOnAuthor {


    final private String[] a_authors;
    final private boolean[] a_values;
    
    /**
     * creates a filter accepting all authors
     * @param authors
     */
    public FilterOnAuthor(final String authors[]) {
        a_authors = authors;
        a_values = new boolean[authors.length];
        for (int i=0; i<authors.length; i++) a_values[i]=true;
    }

    /**
     * creates a filter with the specified formats
     * @param authors
     * @param values
     */
    public FilterOnAuthor(final String authors[],
                          final boolean values[]) {
        if (authors.length!=values.length) throw new AssertionError();
        a_authors = authors;
        a_values = values;
    }

    /**
     * @param list
     * @param index
     * @return does the photo fulfill the filter?
     */
    public boolean filter(final PhotoList list,
                          final int index) {
        final String author = (String)list.getValueAt(index,PhotoList.PARAM_AUTHOR);
        int i = 0;
        while ( author != a_authors[i] ) i++;
        return a_values[i];
    }
    
    /**
     * @return formats handled by this filter
     */
    public String[] getAuthors() {
        return a_authors;
    }
    
    /**
     * @return values of the filter
     */
    public boolean[] getValues() {
        return a_values;
    }
    
    /**
     * @return indicates if the filter is enabled
     */
    public boolean isEnabled() {
        boolean isDisabled = true;
        for (int i=0; i<a_authors.length; i++) isDisabled &= a_values[i];
        return !isDisabled;
    }
}
