package lmzr.photomngr.data.filter;

import lmzr.photomngr.data.PhotoList;

/**
 * 
 */
public class PhotoListFilter {

    final private FilterOnPhotoTrait a_filterOnOriginality;
    final private FilterOnPhotoTrait a_filterOnPrivacy;
    final private FilterOnPhotoTrait a_filterOnQuality;
    final private FilterOnHierarchicalCompoundString a_filterOnLocation;
    final private FilterOnHierarchicalCompoundString a_filterOnSubject1;
    final private FilterOnHierarchicalCompoundString a_filterOnSubject2;
    final private FilterOnHierarchicalCompoundString a_filterOnSubject3;
    final private FilterOnHierarchicalCompoundString a_filterOnSubject4;
    final private FilterOnFormat a_filterOnFormat;
    final private FilterOnAuthor a_filterOnAuthor;
    final private FilterOnCopies a_filterOnCopies;

    /**
     * @param filterOnOriginality
     * @param filterOnPrivacy
     * @param filterOnQuality
     * @param filterOnLocation 
     * @param filterOnSubject1 
     * @param filterOnSubject2 
     * @param filterOnSubject3 
     * @param filterOnSubject4 
     * @param filterOnFormat 
     * @param filterOnAuthor 
     * @param filterOnCopies 
     */
    public PhotoListFilter(final FilterOnPhotoTrait filterOnOriginality,
                           final FilterOnPhotoTrait filterOnPrivacy,
                           final FilterOnPhotoTrait filterOnQuality,
                           final FilterOnHierarchicalCompoundString filterOnLocation,
                           final FilterOnHierarchicalCompoundString filterOnSubject1,
                           final FilterOnHierarchicalCompoundString filterOnSubject2,
                           final FilterOnHierarchicalCompoundString filterOnSubject3,
                           final FilterOnHierarchicalCompoundString filterOnSubject4,
                           final FilterOnFormat filterOnFormat,
                           final FilterOnAuthor filterOnAuthor,
                           final FilterOnCopies filterOnCopies) {
        a_filterOnOriginality = filterOnOriginality;
        a_filterOnPrivacy = filterOnPrivacy;
        a_filterOnQuality = filterOnQuality;
        a_filterOnLocation = filterOnLocation;
        a_filterOnSubject1 = filterOnSubject1;
        a_filterOnSubject2 = filterOnSubject2;
        a_filterOnSubject3 = filterOnSubject3;
        a_filterOnSubject4 = filterOnSubject4;
        a_filterOnFormat = filterOnFormat;
        a_filterOnAuthor = filterOnAuthor;
        a_filterOnCopies = filterOnCopies;
    }
    
    /**
     * @param list
     * @param index does the photo fulfils the filter?
     * @return does the photo fulfils the filter?
     */
    boolean filter(final PhotoList list, final int index) {
        if ( !a_filterOnFormat.filter(list,index) ) return false;
        if ( !a_filterOnOriginality.filter(list,index) ) return false;
        if ( !a_filterOnPrivacy.filter(list,index) ) return false;
        if ( !a_filterOnQuality.filter(list,index) ) return false;
        if ( !a_filterOnAuthor.filter(list, index)) return false;
        if ( !a_filterOnCopies.filter(list, index)) return false;
        if ( !a_filterOnLocation.filter(list,index) ) return false;
        if ( !a_filterOnSubject1.filter(list,index) ) return false;
        if ( !a_filterOnSubject2.filter(list,index) ) return false;
        if ( !a_filterOnSubject3.filter(list,index) ) return false;
        if ( !a_filterOnSubject4.filter(list,index) ) return false;
        return true;
    }
    
    /**
     * @return filter on originality
     */
    public FilterOnPhotoTrait getFilterOnOriginality() {
        return a_filterOnOriginality;
    }
    
    /**
     * @return filter on quality
     */
    public FilterOnPhotoTrait getFilterOnQuality() {
        return a_filterOnQuality;
    }
    
    /**
     * @return filter on privacy
     */
    public FilterOnPhotoTrait getFilterOnPrivacy() {
        return a_filterOnPrivacy;
    }

    /**
     * @return filter on location
     */
    public FilterOnHierarchicalCompoundString getFilterOnLocation() {
        return a_filterOnLocation;
    }

    /**
     * @return first filter on subject
     */
    public FilterOnHierarchicalCompoundString getFilterOnSubject1() {
        return a_filterOnSubject1;
    }
    
    /**
     * @return second filter on subject
     */
    public FilterOnHierarchicalCompoundString getFilterOnSubject2() {
        return a_filterOnSubject2;
    }
    
    /**
     * @return third filter on subject
     */
    public FilterOnHierarchicalCompoundString getFilterOnSubject3() {
        return a_filterOnSubject3;
    }

    /**
     * @return fourth filter on subject
     */
    public FilterOnHierarchicalCompoundString getFilterOnSubject4() {
        return a_filterOnSubject4;
    }
    
    /**
     * @return filter on format
     */
    public FilterOnFormat getFilterOnFormat() {
        return a_filterOnFormat;
    }

    /**
     * @return filter on author
     */
    public FilterOnAuthor getFilterOnAuthor() {
        return a_filterOnAuthor;
    }

    /**
     * @return filter on copies
     */
    public FilterOnCopies getFilterOnCopies() {
        return a_filterOnCopies;
    }
}
