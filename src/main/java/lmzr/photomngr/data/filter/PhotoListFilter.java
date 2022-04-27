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
        this.a_filterOnOriginality = filterOnOriginality;
        this.a_filterOnPrivacy = filterOnPrivacy;
        this.a_filterOnQuality = filterOnQuality;
        this.a_filterOnLocation = filterOnLocation;
        this.a_filterOnSubject1 = filterOnSubject1;
        this.a_filterOnSubject2 = filterOnSubject2;
        this.a_filterOnSubject3 = filterOnSubject3;
        this.a_filterOnSubject4 = filterOnSubject4;
        this.a_filterOnFormat = filterOnFormat;
        this.a_filterOnAuthor = filterOnAuthor;
        this.a_filterOnCopies = filterOnCopies;
    }

    /**
     * @param list
     * @param index
     * @return does the photo fulfill the filter?
     */
    boolean filter(final PhotoList list, final int index) {
        if ( this.a_filterOnFormat.isEnabled() && !this.a_filterOnFormat.filter(list,index) ) return false;
        if ( this.a_filterOnOriginality.isEnabled() && !this.a_filterOnOriginality.filter(list,index) ) return false;
        if ( this.a_filterOnPrivacy.isEnabled() && !this.a_filterOnPrivacy.filter(list,index) ) return false;
        if ( this.a_filterOnQuality.isEnabled() && !this.a_filterOnQuality.filter(list,index) ) return false;
        if ( this.a_filterOnAuthor.isEnabled() && !this.a_filterOnAuthor.filter(list, index)) return false;
        if ( this.a_filterOnCopies.isEnabled() && !this.a_filterOnCopies.filter(list, index)) return false;
        if ( this.a_filterOnLocation.isEnabled() && !this.a_filterOnLocation.filter(list,index) ) return false;
        if ( this.a_filterOnSubject1.isEnabled() && !this.a_filterOnSubject1.filter(list,index) ) return false;
        if ( this.a_filterOnSubject2.isEnabled() && !this.a_filterOnSubject2.filter(list,index) ) return false;
        if ( this.a_filterOnSubject3.isEnabled() && !this.a_filterOnSubject3.filter(list,index) ) return false;
        if ( this.a_filterOnSubject4.isEnabled() && !this.a_filterOnSubject4.filter(list,index) ) return false;
        return true;
    }

    /**
     * @return filter on originality
     */
    public FilterOnPhotoTrait getFilterOnOriginality() {
        return this.a_filterOnOriginality;
    }

    /**
     * @return filter on quality
     */
    public FilterOnPhotoTrait getFilterOnQuality() {
        return this.a_filterOnQuality;
    }

    /**
     * @return filter on privacy
     */
    public FilterOnPhotoTrait getFilterOnPrivacy() {
        return this.a_filterOnPrivacy;
    }

    /**
     * @return filter on location
     */
    public FilterOnHierarchicalCompoundString getFilterOnLocation() {
        return this.a_filterOnLocation;
    }

    /**
     * @return first filter on subject
     */
    public FilterOnHierarchicalCompoundString getFilterOnSubject1() {
        return this.a_filterOnSubject1;
    }

    /**
     * @return second filter on subject
     */
    public FilterOnHierarchicalCompoundString getFilterOnSubject2() {
        return this.a_filterOnSubject2;
    }

    /**
     * @return third filter on subject
     */
    public FilterOnHierarchicalCompoundString getFilterOnSubject3() {
        return this.a_filterOnSubject3;
    }

    /**
     * @return fourth filter on subject
     */
    public FilterOnHierarchicalCompoundString getFilterOnSubject4() {
        return this.a_filterOnSubject4;
    }

    /**
     * @return filter on format
     */
    public FilterOnFormat getFilterOnFormat() {
        return this.a_filterOnFormat;
    }

    /**
     * @return filter on author
     */
    public FilterOnAuthor getFilterOnAuthor() {
        return this.a_filterOnAuthor;
    }

    /**
     * @return filter on copies
     */
    public FilterOnCopies getFilterOnCopies() {
        return this.a_filterOnCopies;
    }
}
