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
        a_location = locationFactory.create((data.length>2 && data[2].length()>0) ? data[2] : "");
        a_subject = subjectFactory.create((data.length>3 && data[3].length()>0) ? data[3].replaceAll("\\+","\n") : "");
        a_quality = PhotoQuality.parse( (data.length>4 && data[4].length()>0) ? data[4] : null );
        a_originality = PhotoOriginality.parse( (data.length>5 && data[5].length()>0) ? data[5] : null );
        a_privacy = PhotoPrivacy.parse( (data.length>6 && data[6].length()>0) ? data[6] : null );
        a_panorama = (data.length>7) ? data[7] : "";
        a_panoramaFirst = (data.length>8) ? data[8] : "";
        a_author = authorFactory.create((data.length>9) ? data[9] : "");
        if (data.length>10) {
            try {
                a_copies = Integer.parseInt(data[10]);
            } catch (final NumberFormatException e) {
                System.err.println("failed to translate copies \""+data[10]+"\" into an integer");
                a_copies = 0;
            }
        } else {
            a_copies = 0;
        }
        if (data.length>11) {
            try {
                a_zoom = Float.parseFloat(data[11]);
            } catch (final NumberFormatException e) {
                System.err.println("failed to translate zoom \""+data[11]+"\" into a float");
                a_zoom = 1.f;
            }
        } else {
            a_zoom = 1.f;
        }
        if (data.length>12) {
            try {
                a_focus_x = Float.parseFloat(data[12]);
            } catch (final NumberFormatException e) {
                System.err.println("failed to translate focus X \""+data[12]+"\" into a float");
                a_focus_x = 0.f;
            }
        } else {
        	a_focus_x = 0.f;
        }
        if (data.length>13) {
            try {
                a_focus_y = Float.parseFloat(data[13]);
            } catch (final NumberFormatException e) {
                System.err.println("failed to translate focus Y \""+data[13]+"\" into a float");
                a_focus_y = 0.f;
            }
        } else {
        	a_focus_y = 0.f;
        }
        if (data.length>14) {
            try {
                a_rotation = Float.parseFloat(data[14]);
            } catch (final NumberFormatException e) {
                System.err.println("failed to translate rotation \""+data[14]+"\" into a float");
                a_rotation = 0.f;
            }
        } else {
        	a_rotation = 0.f;
        }
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