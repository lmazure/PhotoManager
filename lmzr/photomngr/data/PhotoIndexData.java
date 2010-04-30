package lmzr.photomngr.data;

import lmzr.photomngr.data.phototrait.PhotoOriginality;
import lmzr.photomngr.data.phototrait.PhotoPrivacy;
import lmzr.photomngr.data.phototrait.PhotoQuality;
import lmzr.util.string.HierarchicalCompoundString;
import lmzr.util.string.HierarchicalCompoundStringFactory;
import lmzr.util.string.MultiHierarchicalCompoundString;
import lmzr.util.string.MultiHierarchicalCompoundStringFactory;

/**
 * @author Laurent Mazuré
 */
public class PhotoIndexData {

    private HierarchicalCompoundString a_location;
    private MultiHierarchicalCompoundString a_subject;
    private PhotoQuality a_quality;
    private PhotoOriginality a_originality;
    private PhotoPrivacy a_privacy;
    private String a_panorama;
    private String a_panoramaFirst;
    private String a_author;
    private int a_copies;
    private float a_zoom;
    private float a_focus_x;
    private float a_focus_y;
    private float a_rotation;

    /**
     * @param data
     * @param locationFactory
     * @param subjectFactory
     * @param authorFactory 
     */
    public PhotoIndexData(final String data[],
                          final HierarchicalCompoundStringFactory locationFactory,
                          final MultiHierarchicalCompoundStringFactory subjectFactory,
                          final AuthorFactory authorFactory) {
        a_location = locationFactory.create(data[2]);
        a_subject = subjectFactory.create(data[3]);
        a_quality = PhotoQuality.parse(data[4]);
        a_originality = PhotoOriginality.parse(data[5]);
        a_privacy = PhotoPrivacy.parse(data[6]);
        a_panorama = data[7];
        a_panoramaFirst = data[8];
        a_author = authorFactory.create(data[9]);
        a_copies = 0;
        a_zoom = 1.f;
        a_focus_x = 0.f;
        a_focus_y = 0.f;
        a_rotation = 0.f;
        try {
            a_copies = Integer.parseInt(data[10]);
            a_zoom = Float.parseFloat(data[11]);
            a_focus_x = Float.parseFloat(data[12]);
            a_focus_y = Float.parseFloat(data[13]);
            a_rotation = Float.parseFloat(data[14]);
        } catch (final NumberFormatException e) {
            System.err.println("failed to parse data");
            e.printStackTrace();
        }
    }

    /**
     * @param locationFactory
     * @param subjectFactory
     * @param authorFactory 
     */
    public PhotoIndexData(final HierarchicalCompoundStringFactory locationFactory,
                          final MultiHierarchicalCompoundStringFactory subjectFactory,
                          final AuthorFactory authorFactory) {
        a_location = locationFactory.create("");
        a_subject = subjectFactory.create("");
        a_quality = PhotoQuality.parse("");
        a_originality = PhotoOriginality.parse("");
        a_privacy = PhotoPrivacy.parse("");
        a_panorama = "";
        a_panoramaFirst = "";
        a_author = authorFactory.create("");
        a_copies = 0;
        a_zoom = 1.f;
        a_focus_x = 0.f;
        a_focus_y = 0.f;
        a_rotation = 0.f;
    }

    /**
     * @return subject
     */
    public MultiHierarchicalCompoundString getSubject() {
        return a_subject;
    }

    /**
     * @param value
     */
    public void setSubject(final MultiHierarchicalCompoundString value) {
        a_subject = value;
    }

    /**
     * may be null if undefined in the photo index file
     * @return quality
     */
    public PhotoQuality getQuality() {
        return a_quality;
    }
    
    /**
     * may be null if undefined in the photo index file
     * @return originality
     */
    public PhotoOriginality getOriginality() {
        return a_originality;
    }

    /**
     * may be null if undefined in the photo index file
     * @return privacy
     */
    public PhotoPrivacy getPrivacy() {
        return a_privacy;
    }
    

    /**
     * @param value
     */
    public void setQuality(final PhotoQuality value) {
        a_quality = value;
    }
    
    /**
     * @param value
     */
    public void setOriginality(final PhotoOriginality value) {
        a_originality = value;
    }

    /**
     * @param value
     */
    public void setPrivacy(final PhotoPrivacy value) {
        a_privacy = value;
    }
    
   /**
     * @return geographical location where the photo has been taken
     */
    public HierarchicalCompoundString getLocation() {
        return a_location;
    }
    
    /**
     * @param value
     */
    public void setLocation(final HierarchicalCompoundString value) {
        a_location = value;
    }

    /**
     * @return the position in the list of photos of the panorama
     */
    public String getPanorama() {
        return a_panorama;
    }

    /**
     * @return the first photo of the list of photos of the panorama
     */
    public String getPanoramaFirst() {
        return a_panoramaFirst;
    }

    /**
     * @return author
     */
    public String getAuthor() {
        return a_author;
    }

    /**
     * @param author
     */
    public void setAuthor(final String author) {
    	a_author = author;
    }
    /**
     * @return number of copies to print
     */
    public int getCopies() {
        return a_copies;
    }
    
    /**
     * @param copies
     */
    public void setCopies(final int copies) {
        a_copies = copies;
    }
    
    /**
     * @return zoom factor
     */
    public float getZoom() {
    	return a_zoom;
    }
    
    /**
     * @param zoom
     */
    public void setZoom(final float zoom) {
    	a_zoom = zoom;
    }
    
    /**
     * @return focus X
     */
    public float getFocusX() {
    	return a_focus_x;
    }
    
    /**
     * @param focusX
     */
    public void setFocusX(final float focusX) {
    	a_focus_x = focusX;
    }
    
    /**
     * @return focus Y
     */
    public float getFocusY() {
    	return a_focus_y;
    }
    
    /**
     * @param focusY
     */
    public void setFocusY(final float focusY) {
    	a_focus_y = focusY;
    }
    
    
    /**
     * @return rotation
     */
    public float getRotation() {
    	return a_rotation;
    }
    
    /**
     * @param rotation
     */
    public void setRotation(final float rotation) {
    	a_rotation = rotation;
    }

    /**
     * @param panorama
     */
    public void setPanorama(final String panorama) {
    	a_panorama = panorama;
    }

    /**
     * @param panoramaFirst
     */
    public void setPanoramaFirst(final String panoramaFirst) {
    	a_panoramaFirst = panoramaFirst;
    }

}