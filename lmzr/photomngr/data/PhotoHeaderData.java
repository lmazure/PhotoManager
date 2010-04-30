package lmzr.photomngr.data;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import javax.imageio.ImageIO;

import com.drew.imaging.jpeg.JpegMetadataReader;
import com.drew.imaging.jpeg.JpegProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.MetadataException;
import com.drew.metadata.Tag;
import com.drew.metadata.exif.CanonMakernoteDirectory;
import com.drew.metadata.exif.ExifDirectory;

/**
 * @author Laurent Mazuré
 */
public class PhotoHeaderData {

    private int a_width;
    private int a_height;
    private int a_orientation;
    private Date a_date;
    private String a_manufacturer;
    private String a_model;
    private String a_exposure_time;
    private String a_shutter_speed;
    private String a_aperture_value;
    private String a_flash;
    private double a_focal_length;
    private String a_self_timer_mode;
    private String a_canon_self_timer_delay;
    private String a_canon_flash_mode;
    private String a_canon_continuous_drive_mode;
    private String a_canon_focus_mode;
    private String a_canon_iso;
    private int a_canon_subject_distance;
    
    static final private int DEFAULT_ORIENTATION = 1;
    final private Date DEFAULT_DATE = null;
    final private String DEFAULT_MANUFACTURER = null;
    final private String DEFAULT_MODEL = null;
    final private String DEFAULT_EXPOSURE_TIME = null;
    final private String DEFAULT_SHUTTER_SPEED = null;
    final private String DEFAULT_APERTURE_VALUE = null;
    final private String DEFAULT_FLASH = null;
    final private double DEFAULT_FOCAL_LENGTH = -1.0;
    final private String DEFAULT_SELF_TIMER_MODE = null;
    final private String DEFAULT_CANON_SELF_TIMER_DELAY = null;
    final private String DEFAULT_CANON_FLASH_MODE = null;
    final private String DEFAULT_CANON_CONTINUOUS_DRIVE_MODE = null;
    final private String DEFAULT_CANON_FOCUS_MODE = null;
    final private String DEFAULT_CANON_ISO = null;
    final private int DEFAULT_CANON_SUBJECT_DISTANCE = -1;
    
    static final StringPool s_pool = new StringPool(); 

    /**
     * @param foldername
     * @param filename
     * @param format 
     */
    public PhotoHeaderData(final String filename,
    		               final DataFormat format) {
    	
    	String overridenfilename;
    	
    	// if this is an AVI file, try to read the corresponding THM file
    	if ( format == DataFormat.AVI ) {
    		String f = "";
    		if ( filename.endsWith(".AVI") ) f = filename.substring(0,filename.length()-3) + "THM";
    		if ( filename.endsWith(".avi") ) f = filename.substring(0,filename.length()-3) + "thm";
    		final File ff = new File(f);
    		if ( !ff.exists()) return;
    		overridenfilename = f;
    	} else {
    		overridenfilename = filename;
    	}
    	
        a_canon_subject_distance = DEFAULT_CANON_SUBJECT_DISTANCE;
        
        try {
            final File file = new File(overridenfilename);
            final Metadata metadata = JpegMetadataReader.readMetadata(file);
            final Iterator<?> directories = metadata.getDirectoryIterator();
            while (directories.hasNext()) {
                final Directory directory = (Directory)directories.next();
                final Iterator<?> tags = directory.getTagIterator();
                while (tags.hasNext()) {
                    final Tag tag = (Tag)tags.next();
//                    try {
//                        System.out.println("tag="+tag.getTagTypeHex());
//                        System.out.println("name="+tag.getTagName());
//                        System.out.println("value="+tag.getDescription());
//                        System.out.println("--------------------------------");
//                    } catch (Exception e) {
//                        //
//                    }
                    try {
                    	if (tag.getTagType() == 0x0001) {
                    		a_height = directory.getInt(tag.getTagType());
                    	} else if (tag.getTagType() == 0x0003) {
                    		a_width = directory.getInt(tag.getTagType());
                    	} else if (tag.getTagType() == ExifDirectory.TAG_ORIENTATION) {
                    		a_orientation = directory.getInt(tag.getTagType());
                    	} else if (tag.getTagType() == ExifDirectory.TAG_DATETIME) {
                    		a_date = directory.getDate(tag.getTagType());
                    	} else if (tag.getTagType() == ExifDirectory.TAG_MAKE) {
                    		a_manufacturer = s_pool.replace(directory.getDescription(tag.getTagType()));
                    	} else if (tag.getTagType() == ExifDirectory.TAG_MODEL) {
                    		a_model = s_pool.replace(directory.getDescription(tag.getTagType()));
                    	} else if (tag.getTagType() == ExifDirectory.TAG_EXPOSURE_TIME) {
                    		a_exposure_time = s_pool.replace(directory.getDescription(tag.getTagType()));
                    	} else if (tag.getTagType() == ExifDirectory.TAG_SHUTTER_SPEED) {
                    		a_shutter_speed = s_pool.replace(directory.getDescription(tag.getTagType()));
                    	} else if (tag.getTagType() == ExifDirectory.TAG_APERTURE) {
                    		a_aperture_value = s_pool.replace(directory.getDescription(tag.getTagType()));
                    	} else if (tag.getTagType() == ExifDirectory.TAG_FLASH) {
                    		a_flash = s_pool.replace(directory.getDescription(tag.getTagType()));
                    	} else if (tag.getTagType() == ExifDirectory.TAG_FOCAL_LENGTH) {
                			try {
                    			final String value = directory.getDescription(tag.getTagType()).replace(" mm","").replace(",",".");
                    			a_focal_length = Double.parseDouble(value);
                			} catch (final NumberFormatException e) {
                	        	a_focal_length = DEFAULT_FOCAL_LENGTH;
                    			System.err.println("unexpected value for TAG_FOCAL_LENGTH");
                    			e.printStackTrace();
                    		}
                    	} else if (tag.getTagType() == ExifDirectory.TAG_SELF_TIMER_MODE) {
                    		a_self_timer_mode = s_pool.replace(directory.getDescription(tag.getTagType()));
                    	} else if (getManufacturer()!=null && getManufacturer().equalsIgnoreCase("canon")) { 
                    		if (tag.getTagType() == CanonMakernoteDirectory.TAG_CANON_STATE1_SELF_TIMER_DELAY) {
                    			a_canon_self_timer_delay = s_pool.replace(directory.getDescription(tag.getTagType()));
                    		} else if (tag.getTagType() == CanonMakernoteDirectory.TAG_CANON_STATE1_FLASH_MODE) {
                    			a_canon_flash_mode = s_pool.replace(directory.getDescription(tag.getTagType()));
                    		} else if (tag.getTagType() == CanonMakernoteDirectory.TAG_CANON_STATE1_CONTINUOUS_DRIVE_MODE) {
                    			a_canon_continuous_drive_mode = s_pool.replace(directory.getDescription(tag.getTagType()));
                    		} else if (tag.getTagType() == CanonMakernoteDirectory.TAG_CANON_STATE1_FOCUS_MODE_1) {
                    			a_canon_focus_mode = s_pool.replace(directory.getDescription(tag.getTagType()));
                    		} else if (tag.getTagType() == CanonMakernoteDirectory.TAG_CANON_STATE1_ISO) {
                    			a_canon_iso = s_pool.replace(directory.getDescription(tag.getTagType()));
                    		} else if (tag.getTagType() == CanonMakernoteDirectory.TAG_CANON_STATE2_SUBJECT_DISTANCE) {
                    			try {
	                    			final String value = directory.getDescription(tag.getTagType());
	                    			a_canon_subject_distance = Integer.parseInt(value);
                    			} catch (final NumberFormatException e) {
                    				a_canon_subject_distance = DEFAULT_CANON_SUBJECT_DISTANCE;
	                    			System.err.println("unexpected value for TAG_CANON_STATE2_SUBJECT_DISTANCE");
	                    			e.printStackTrace();
	                    		}
                    		}
                    	}
                    } catch (final MetadataException e) {
                    	// do nothing
                    }
                }
            }
        } catch (final JpegProcessingException e) {
        	a_orientation = DEFAULT_ORIENTATION;
        	a_date = DEFAULT_DATE;
        	a_manufacturer = DEFAULT_MANUFACTURER;
        	a_model = DEFAULT_MODEL;
        	a_exposure_time = DEFAULT_EXPOSURE_TIME;
        	a_shutter_speed = DEFAULT_SHUTTER_SPEED;
        	a_aperture_value = DEFAULT_APERTURE_VALUE;
        	a_flash = DEFAULT_FLASH;
        	a_focal_length = DEFAULT_FOCAL_LENGTH;
        	a_self_timer_mode = DEFAULT_SELF_TIMER_MODE;
        	a_canon_self_timer_delay = DEFAULT_CANON_SELF_TIMER_DELAY;
        	a_canon_flash_mode = DEFAULT_CANON_FLASH_MODE;
        	a_canon_continuous_drive_mode = DEFAULT_CANON_CONTINUOUS_DRIVE_MODE;
        	a_canon_focus_mode = DEFAULT_CANON_FOCUS_MODE;
        	a_canon_iso = DEFAULT_CANON_ISO;
        	a_canon_subject_distance = DEFAULT_CANON_SUBJECT_DISTANCE;
        }
        
        // JPEG file with no tag (I'll have to read the JPEG and EXIF specifications...)
        if ( format == DataFormat.JPEG &&
        	( a_width == 0 || a_height == 0 ) ) {
        	final File file = new File(overridenfilename);
        	if (file.exists()) {
	        	try {
	        	    final BufferedImage image = ImageIO.read(file);
	        	    a_height = image.getHeight();
	        	    a_width = image.getWidth();
	        	} catch (final IOException e) {
	        		// do nothing;
	        	}
            }
        }
    }

    /**
     * @return image height
     */
    public int getHeight() {
       return a_height;	
    }
    
    /**
     * @return image width
     */
    public int getWidth() {
       return a_width;	
    }
    
    /**
     * 1 = The 0th row represents the visual top of the image, and the 0th column represents the visual left-hand side.
  	 * 2 = The 0th row represents the visual top of the image, and the 0th column represents the visual right-hand side.
	 * 3 = The 0th row represents the visual bottom of the image, and the 0th column represents the visual right-hand side.
	 * 4 = The 0th row represents the visual bottom of the image, and the 0th column represents the visual left-hand side.
	 * 5 = The 0th row represents the visual left-hand side of the image, and the 0th column represents the visual top.
	 * 6 = The 0th row represents the visual right-hand side of the image, and the 0th column represents the visual top.
	 * 7 = The 0th row represents the visual right-hand side of the image, and the 0th column represents the visual bottom.
	 * 8 = The 0th row represents the visual left-hand side of the image, and the 0th column represents the visual bottom.
     * @return orientation of the photo (tag 0x0112)
     */
    public int getOrientation() {
        return a_orientation;
    }
    
    /**
     * may be null if undefined in the photo file
     * @return date when the photo has been taken (tag 0x0132)
     */
    public Date getDate() {
        return a_date;
    }
    
    /**
     * @return manufacturer (tag 0x010F)
     */
    public String getManufacturer() {
        return a_manufacturer;    	
    }

    /**
     * @return model (tag 0x0110)
     */
    public String getModel() {
        return a_model;    	
    }
    
    /**
     * @return exposure time (tag 0x829A)
     */
    public String getExposureTime() {
        return a_exposure_time;    	
    }
    
    /**
     * @return shutter speed (tag 0x9201)
     */
    public String getShutterSpeed() {
        return a_shutter_speed;    	
    }
    
    /**
     * @return aperture value (tag 0x9202)
     */
    public String getApertureValue() {
        return a_aperture_value;    	
    }
    
    /**
     * @return flash (tag 0x9209)
     */
    public String getFlash() {
        return a_flash;    	
    }
    
    /**
     * @return focal length (tag 0x920A)
     */
    public Double getFocalLength() {
        return a_focal_length;    	
    }

    /**
     * @return Self timer mode
     */
    public String getSelfTimerMode() {
        return a_self_timer_mode;    	
    }

    /**
     * @return Canon self timer delay (tag 0xC102)
     */
    public String getCanonSelfTimerDelay() {
        return a_canon_self_timer_delay;    	
    }

    /**
     * @return Canon flash mode(tag 0xC104)
     */
    public String getCanonFlashMode() {
        return a_canon_flash_mode;    	
    }

    /**
     * @return Canon continuous drive mode (tag 0xC105)
     */
    public String getCanonContinuousDriveMode() {
        return a_canon_continuous_drive_mode;    	
    }

    /**
     * @return Canon focus mode(tag 0xC107)
     */
    public String getCanonFocusMode() {
        return a_canon_focus_mode;    	
    }

    /**
     * @return Canon ISO (tag 0xC110)
     */
    public String getCanonISO() {
        return a_canon_iso;    	
    }

    /**
     * @return Canon subject distance (tag 0xC213), -1 if undefined
     */
    public int getCanonSubjectDistance() {
        return a_canon_subject_distance;    	
    }

}
