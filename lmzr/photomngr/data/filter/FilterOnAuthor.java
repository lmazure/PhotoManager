package lmzr.photomngr.data.filter;

import java.util.HashSet;

import lmzr.photomngr.data.PhotoList;

/**
 * @author Laurent
 *
 */
public class FilterOnAuthor {

    final private HashSet<String> a_filteredAuthors; 
    
    /**
     * creates a filter accepting all authors
     */
    public FilterOnAuthor() {
        a_filteredAuthors = null;
    }

    /**
     * creates a filter filtering the specified authors
     * @param filteredAuthors
     */
    public FilterOnAuthor(final HashSet<String> filteredAuthors) {
        a_filteredAuthors = filteredAuthors;             
    }

    /**
     * this method shall be called only if the filter is enabled
     * @param list
     * @param index
     * @return does the photo fulfill the filter?
     */
    public boolean filter(final PhotoList list,
                          final int index) {
        
        final String author = (String)list.getValueAt(index,PhotoList.PARAM_AUTHOR);
        return a_filteredAuthors.contains(author);
    }
    
    /**
     * @return formats handled by this filter
     */
    public HashSet<String> getFilteredAuthors() {
        return a_filteredAuthors;
    }
        
    /**
     * @return indicates if the filter is enabled
     */
    public boolean isEnabled() {
        return (a_filteredAuthors != null );
    }
}
