package lmzr.photomngr.data;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Date;
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

    final private String a_filename;
    final private int a_width;
    final private int a_height;
    final private int a_orientation;
    final private Date a_date;
    final private String a_manufacturer;
    final private String a_model;
    final private String a_exposure_time;
    final private String a_shutter_speed;
    final private String a_aperture_value;
    final private String a_flash;
    final private double a_focal_length;
    final private String a_self_timer_mode;
    final private String a_canon_self_timer_delay;
    final private String a_canon_flash_mode;
    final private String a_canon_continuous_drive_mode;
    final private String a_canon_focus_mode;
    final private String a_canon_iso;
    final private int a_canon_subject_distance;
    
    static final private int DEFAULT_ORIENTATION = 1;
    static final private Date DEFAULT_DATE = new Date(0);
    static final private String DEFAULT_MANUFACTURER = "";
    static final private String DEFAULT_MODEL = "";
    static final  private String DEFAULT_EXPOSURE_TIME = "";
    static final private String DEFAULT_SHUTTER_SPEED = "";
    static final private String DEFAULT_APERTURE_VALUE = "";
    static final private String DEFAULT_FLASH = "";
    static final private double DEFAULT_FOCAL_LENGTH = -1.0;
    static final private String DEFAULT_SELF_TIMER_MODE = "";
    static final private String DEFAULT_CANON_SELF_TIMER_DELAY = "";
    static final private String DEFAULT_CANON_FLASH_MODE = "";
    static final private String DEFAULT_CANON_CONTINUOUS_DRIVE_MODE = "";
    static final private String DEFAULT_CANON_FOCUS_MODE = "";
    static final private String DEFAULT_CANON_ISO = "";
    static final private int DEFAULT_CANON_SUBJECT_DISTANCE = -1;
    
    static final StringPool s_pool = new StringPool(); 

    /**
     * @param foldername
     * @param filename
     * @param format 
     */
    public PhotoHeaderData(final String filename,
    		               final DataFormat format) {
    	
    	a_filename = new File(filename).getName();
    	
    	String overridenfilename;
    	
    	// if this is an AVI file, try to read the corresponding THM file
    	if ( format == DataFormat.AVI ) {
    		String f = "";
    		if ( filename.endsWith(".AVI") ) f = filename.substring(0,filename.length()-3) + "THM";
    		if ( filename.endsWith(".avi") ) f = filename.substring(0,filename.length()-3) + "thm";
    		final File ff = new File(f);
    		if ( !ff.exists()) {
    	    	a_width = 0;
    	    	a_height= 0;
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
    	    	return;
    		}
    		overridenfilename = f;
    	} else {
    		overridenfilename = filename;
    	}
    	
    	int width = 0;
    	int height= 0;
    	int orientation = DEFAULT_ORIENTATION;
    	Date date = DEFAULT_DATE;
    	String manufacturer = DEFAULT_MANUFACTURER;
    	String model = DEFAULT_MODEL;
    	String exposure_time = DEFAULT_EXPOSURE_TIME;
    	String shutter_speed = DEFAULT_SHUTTER_SPEED;
    	String aperture_value = DEFAULT_APERTURE_VALUE;
    	String flash = DEFAULT_FLASH;
    	double focal_length = DEFAULT_FOCAL_LENGTH;
    	String self_timer_mode = DEFAULT_SELF_TIMER_MODE;
    	String canon_self_timer_delay = DEFAULT_CANON_SELF_TIMER_DELAY;
    	String canon_flash_mode = DEFAULT_CANON_FLASH_MODE;
    	String canon_continuous_drive_mode = DEFAULT_CANON_CONTINUOUS_DRIVE_MODE;
    	String canon_focus_mode = DEFAULT_CANON_FOCUS_MODE;
    	String canon_iso = DEFAULT_CANON_ISO;
    	int canon_subject_distance = DEFAULT_CANON_SUBJECT_DISTANCE;
        
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
                    		height = directory.getInt(tag.getTagType());
                    	} else if (tag.getTagType() == 0x0003) {
                    		width = directory.getInt(tag.getTagType());
                    	} else if (tag.getTagType() == ExifDirectory.TAG_ORIENTATION) {
                    		orientation = directory.getInt(tag.getTagType());
                    	} else if (tag.getTagType() == ExifDirectory.TAG_DATETIME) {
                    		date = directory.getDate(tag.getTagType());
                    	} else if (tag.getTagType() == ExifDirectory.TAG_MAKE) {
                    		manufacturer = s_pool.replace(directory.getDescription(tag.getTagType()));
                    	} else if (tag.getTagType() == ExifDirectory.TAG_MODEL) {
                    		model = s_pool.replace(directory.getDescription(tag.getTagType()));
                    	} else if (tag.getTagType() == ExifDirectory.TAG_EXPOSURE_TIME) {
                    		exposure_time = s_pool.replace(directory.getDescription(tag.getTagType()));
                    	} else if (tag.getTagType() == ExifDirectory.TAG_SHUTTER_SPEED) {
                    		shutter_speed = s_pool.replace(directory.getDescription(tag.getTagType()));
                    	} else if (tag.getTagType() == ExifDirectory.TAG_APERTURE) {
                    		aperture_value = s_pool.replace(directory.getDescription(tag.getTagType()));
                    	} else if (tag.getTagType() == ExifDirectory.TAG_FLASH) {
                    		flash = s_pool.replace(directory.getDescription(tag.getTagType()));
                    	} else if (tag.getTagType() == ExifDirectory.TAG_FOCAL_LENGTH) {
                			try {
                    			final String value = directory.getDescription(tag.getTagType()).replace(" mm","").replace(",",".");
                    			focal_length = Double.parseDouble(value);
                			} catch (final NumberFormatException e) {
                    			System.err.println("unexpected value for TAG_FOCAL_LENGTH");
                    			e.printStackTrace();
                    		}
                    	} else if (tag.getTagType() == ExifDirectory.TAG_SELF_TIMER_MODE) {
                    		self_timer_mode = s_pool.replace(directory.getDescription(tag.getTagType()));
                    	} else if (getManufacturer()!=null && getManufacturer().equalsIgnoreCase("canon")) { 
                    		if (tag.getTagType() == CanonMakernoteDirectory.TAG_CANON_STATE1_SELF_TIMER_DELAY) {
                    			canon_self_timer_delay = s_pool.replace(directory.getDescription(tag.getTagType()));
                    		} else if (tag.getTagType() == CanonMakernoteDirectory.TAG_CANON_STATE1_FLASH_MODE) {
                    			canon_flash_mode = s_pool.replace(directory.getDescription(tag.getTagType()));
                    		} else if (tag.getTagType() == CanonMakernoteDirectory.TAG_CANON_STATE1_CONTINUOUS_DRIVE_MODE) {
                    			canon_continuous_drive_mode = s_pool.replace(directory.getDescription(tag.getTagType()));
                    		} else if (tag.getTagType() == CanonMakernoteDirectory.TAG_CANON_STATE1_FOCUS_MODE_1) {
                    			canon_focus_mode = s_pool.replace(directory.getDescription(tag.getTagType()));
                    		} else if (tag.getTagType() == CanonMakernoteDirectory.TAG_CANON_STATE1_ISO) {
                    			canon_iso = s_pool.replace(directory.getDescription(tag.getTagType()));
                    		} else if (tag.getTagType() == CanonMakernoteDirectory.TAG_CANON_STATE2_SUBJECT_DISTANCE) {
                    			try {
	                    			final String value = directory.getDescription(tag.getTagType());
	                    			canon_subject_distance = Integer.parseInt(value);
                    			} catch (final NumberFormatException e) {
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
			// should never occur, the data is corrupted
			e.printStackTrace();
        }
        
        // JPEG file with no tag (I'll have to read the JPEG and EXIF specifications...)
        if ( format == DataFormat.JPEG &&
        	( width == 0 || height == 0 ) ) {
        	final File file = new File(overridenfilename);
        	if (file.exists()) {
	        	try {
	        	    final BufferedImage image = ImageIO.read(file);
	        	    height = image.getHeight();
	        	    width = image.getWidth();
	        	} catch (final IOException e) {
	        		// do nothing;
	        	}
            }
        }
        
		a_width = width;
		a_height = height;
    	a_orientation = orientation;
    	a_date = date;
    	a_manufacturer = manufacturer;
    	a_model = model;
    	a_exposure_time = exposure_time;
    	a_shutter_speed = shutter_speed;
    	a_aperture_value = aperture_value;
    	a_flash = flash;
    	a_focal_length = focal_length;
    	a_self_timer_mode = self_timer_mode;
    	a_canon_self_timer_delay = canon_self_timer_delay;
    	a_canon_flash_mode = canon_flash_mode;
    	a_canon_continuous_drive_mode = canon_continuous_drive_mode;
    	a_canon_focus_mode = canon_focus_mode;
    	a_canon_iso = canon_iso;
    	a_canon_subject_distance = canon_subject_distance;
    }

    /**
     * create from a String Array
     * @see getStringArray()
     * @param data
     */
    public PhotoHeaderData(final String data[]) {

		a_filename = data[0];

		int width;
		try {
			width = Integer.parseInt(data[1]);
		} catch (final NumberFormatException e) {
			// should never occur, the data is corrupted
			e.printStackTrace();
			width = 0;
		}
		a_width = width;

		int height;
		try {
			height = Integer.parseInt(data[2]);
		} catch (final NumberFormatException e) {
			// should never occur, the data is corrupted
			e.printStackTrace();
			height = 0;
		}
		a_height = height;

		int orientation;
		try {	
			orientation = Integer.parseInt(data[3]);
		} catch (final NumberFormatException e) {
			// should never occur, the data is corrupted
			e.printStackTrace();
			orientation = DEFAULT_ORIENTATION;
		}
		a_orientation = orientation;
		
		Date date;
		try {
			date = new Date(Long.parseLong(data[4]));
		} catch (final NumberFormatException e) {
			// should never occur, the data is corrupted
			e.printStackTrace();
			date = DEFAULT_DATE;
		}
		a_date = date;
			
		a_manufacturer = s_pool.replace(data[5]);
        a_model = s_pool.replace(data[6]);
        a_exposure_time = s_pool.replace(data[7]);
        a_shutter_speed = s_pool.replace(data[8]);
        a_aperture_value = s_pool.replace(data[9]);
        a_flash = s_pool.replace(data[10]);
		
        double focal_length;
        try {    
            focal_length = Double.parseDouble(data[11]);
		} catch (final NumberFormatException e) {
			// should never occur, the data is corrupted
			e.printStackTrace();
			focal_length = DEFAULT_FOCAL_LENGTH;
		}
		a_focal_length = focal_length;
		
        a_self_timer_mode = s_pool.replace(data[12]);
        a_canon_self_timer_delay = s_pool.replace(data[13]);
        a_canon_flash_mode = s_pool.replace(data[14]);
        a_canon_continuous_drive_mode = s_pool.replace(data[15]);
        a_canon_focus_mode = s_pool.replace(data[16]);
        a_canon_iso = s_pool.replace(data[17]);
        
        int canon_subject_distance;
		try {        
			canon_subject_distance = Integer.parseInt(data[18]);
		} catch (final NumberFormatException e) {
			// should never occur, the data is corrupted
			e.printStackTrace();
			canon_subject_distance = DEFAULT_CANON_SUBJECT_DISTANCE;
		}
		a_canon_subject_distance = canon_subject_distance;

    	
    }
    
    /**
     * return as a String array
     * @return
     */
    public String[] getStringArray() {
    	
    	final String array[] = new String[19];
    	
    	array[0] = a_filename;
    	array[1] = Integer.toString(a_width);
    	array[2] = Integer.toString(a_height);
    	array[3] = Integer.toString(a_orientation);
    	array[4] = Long.toString(a_date.getTime());
    	array[5] = a_manufacturer;
    	array[6] = a_model;
        array[7] = a_exposure_time;
       	array[8] = a_shutter_speed;
       	array[9] = a_aperture_value;
        array[10] = a_flash;
        array[11] = Double.toString(a_focal_length);
        array[12] = a_self_timer_mode;
        array[13] = a_canon_self_timer_delay;
        array[14] = a_canon_flash_mode;
        array[15] = a_canon_continuous_drive_mode;
        array[16] = a_canon_focus_mode;
        array[17] = a_canon_iso;
        array[18] = Integer.toString(a_canon_subject_distance);
 
        return array;
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
