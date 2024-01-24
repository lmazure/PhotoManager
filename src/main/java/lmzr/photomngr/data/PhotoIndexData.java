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
        this.a_location = locationFactory.create(data[2]);
        this.a_subject = subjectFactory.create(data[3]);
        this.a_quality = PhotoQuality.parse(data[4]);
        this.a_originality = PhotoOriginality.parse(data[5]);
        this.a_privacy = PhotoPrivacy.parse(data[6]);
        this.a_panorama = data[7];
        this.a_panoramaFirst = data[8];
        this.a_author = authorFactory.create(data[9]);
        this.a_copies = 0;
        this.a_zoom = 1.f;
        this.a_focus_x = 0.f;
        this.a_focus_y = 0.f;
        this.a_rotation = 0.f;
        try {
            this.a_copies = Integer.parseInt(data[10]);
            this.a_zoom = Float.parseFloat(data[11]);
            this.a_focus_x = Float.parseFloat(data[12]);
            this.a_focus_y = Float.parseFloat(data[13]);
            this.a_rotation = Float.parseFloat(data[14]);
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
        this.a_location = locationFactory.create("");
        this.a_subject = subjectFactory.create("");
        this.a_quality = PhotoQuality.parse("");
        this.a_originality = PhotoOriginality.parse("");
        this.a_privacy = PhotoPrivacy.parse("");
        this.a_panorama = "";
        this.a_panoramaFirst = "";
        this.a_author = authorFactory.create("");
        this.a_copies = 0;
        this.a_zoom = 1.f;
        this.a_focus_x = 0.f;
        this.a_focus_y = 0.f;
        this.a_rotation = 0.f;
    }

    /**
     * @return subject
     */
    public MultiHierarchicalCompoundString getSubject() {
        return this.a_subject;
    }

    /**
     * @param value
     */
    public void setSubject(final MultiHierarchicalCompoundString value) {
        this.a_subject = value;
    }

    /**
     * may be null if undefined in the photo index file
     * @return quality
     */
    public PhotoQuality getQuality() {
        return this.a_quality;
    }

    /**
     * may be null if undefined in the photo index file
     * @return originality
     */
    public PhotoOriginality getOriginality() {
        return this.a_originality;
    }

    /**
     * may be null if undefined in the photo index file
     * @return privacy
     */
    public PhotoPrivacy getPrivacy() {
        return this.a_privacy;
    }


    /**
     * @param value
     */
    public void setQuality(final PhotoQuality value) {
        this.a_quality = value;
    }

    /**
     * @param value
     */
    public void setOriginality(final PhotoOriginality value) {
        this.a_originality = value;
    }

    /**
     * @param value
     */
    public void setPrivacy(final PhotoPrivacy value) {
        this.a_privacy = value;
    }

   /**
     * @return geographical location where the photo has been taken
     */
    public HierarchicalCompoundString getLocation() {
        return this.a_location;
    }

    /**
     * @param value
     */
    public void setLocation(final HierarchicalCompoundString value) {
        this.a_location = value;
    }

    /**
     * @return the position in the list of photos of the panorama
     */
    public String getPanorama() {
        return this.a_panorama;
    }

    /**
     * @return the first photo of the list of photos of the panorama
     */
    public String getPanoramaFirst() {
        return this.a_panoramaFirst;
    }

    /**
     * @return author
     */
    public String getAuthor() {
        return this.a_author;
    }

    /**
     * @param author
     */
    public void setAuthor(final String author) {
        this.a_author = author;
    }
    /**
     * @return number of copies to print
     */
    public int getCopies() {
        return this.a_copies;
    }

    /**
     * @param copies
     */
    public void setCopies(final int copies) {
        this.a_copies = copies;
    }

    /**
     * @return zoom factor
     */
    public float getZoom() {
        return this.a_zoom;
    }

    /**
     * @param zoom
     */
    public void setZoom(final float zoom) {
        this.a_zoom = zoom;
    }

    /**
     * @return focus X
     */
    public float getFocusX() {
        return this.a_focus_x;
    }

    /**
     * @param focusX
     */
    public void setFocusX(final float focusX) {
        this.a_focus_x = focusX;
    }

    /**
     * @return focus Y
     */
    public float getFocusY() {
        return this.a_focus_y;
    }

    /**
     * @param focusY
     */
    public void setFocusY(final float focusY) {
        this.a_focus_y = focusY;
    }


    /**
     * @return rotation
     */
    public float getRotation() {
        return this.a_rotation;
    }

    /**
     * @param rotation
     */
    public void setRotation(final float rotation) {
        this.a_rotation = rotation;
    }

    /**
     * @param panorama
     */
    public void setPanorama(final String panorama) {
        this.a_panorama = panorama;
    }

    /**
     * @param panoramaFirst
     */
    public void setPanoramaFirst(final String panoramaFirst) {
        this.a_panoramaFirst = panoramaFirst;
    }

}