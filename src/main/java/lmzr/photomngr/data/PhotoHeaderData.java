package lmzr.photomngr.data;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.imaging.jpeg.JpegMetadataReader;
import com.drew.imaging.jpeg.JpegProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.MetadataException;
import com.drew.metadata.Tag;
import com.drew.metadata.exif.ExifDirectoryBase;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import com.drew.metadata.exif.GpsDirectory;
import com.drew.metadata.exif.makernotes.CanonMakernoteDirectory;

/**
 * Header data of an image/video.
 * 
 * @author Laurent Mazuré
 */
public class PhotoHeaderData {

    final private String a_filename;
    final private boolean a_isCorrectlyParsed;
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
    final private double a_latitude;
    final private double a_longitude;
    final private double a_altitude;
    
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
    static final private double DEFAULT_LATITUDE = Double.NaN;
    static final private double DEFAULT_LONGITUDE = Double.NaN;
    static final private double DEFAULT_ALTITUDE = Double.NaN;
    
    static final StringPool s_pool = new StringPool(); 

    /**
     * @param photoDirectory
     * @param folderName
     * @param fileName
     * @param format
     */
    public PhotoHeaderData(final String photoDirectory,
    		               final String folderName,
    		               final String fileName,
    		               final DataFormat format) {
    	
    	a_filename = fileName;
    	
    	if ( ( format != DataFormat.JPEG ) && ( format != DataFormat.AVI ) ) {
    		// the other formats are not supported for the time being
    		a_isCorrectlyParsed = false;
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
	    	a_latitude = DEFAULT_LATITUDE;
	    	a_longitude = DEFAULT_LONGITUDE;
	    	a_altitude = DEFAULT_ALTITUDE; 
	    	return;    		
    	}
    	
    	String overridenFilename = photoDirectory + File.separator + folderName + File.separator + fileName;
    	
    	// if this is an AVI file, try to read the corresponding THM file (for Canon videos)
    	if ( format == DataFormat.AVI ) {
    		String f = "";
    		if ( overridenFilename.endsWith(".AVI") ) f = overridenFilename.substring(0,overridenFilename.length()-3) + "THM";
    		if ( overridenFilename.endsWith(".avi") ) f = overridenFilename.substring(0,overridenFilename.length()-3) + "thm";
    		final File ff = new File(f);
    		if ( !ff.exists()) {
        		a_isCorrectlyParsed = false;
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
    	    	a_latitude = DEFAULT_LATITUDE;
    	    	a_longitude = DEFAULT_LONGITUDE;
    	    	a_altitude = DEFAULT_ALTITUDE; 
    	    	return;
    		}
    		overridenFilename = f;
    	}
    	
    	boolean isCorrectlyParsed = true;
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
    	String latitudeRef = null;
    	String latitude = null;
    	String longitudeRef = null;
    	String longitude = null;
    	String altitudeRef = null;
    	String altitude = null;
        
        try {
            final File file = new File(overridenFilename);
            
            
            final Metadata metadata = ImageMetadataReader.readMetadata(file);

            for (Directory directory : metadata.getDirectories()) {
                for (Tag tag : directory.getTags()) {

                    /*
                    try {
                        System.out.println("tag="+tag.getTagTypeHex());
                        System.out.println("tag name="+tag.getTagName());
                        System.out.println("directory name="+tag.getDirectoryName());
                        System.out.println("value="+tag.getDescription());
                        System.out.println("--------------------------------");
                    } catch (Exception e) {
                    }
                    */
                    try {
                    	if (tag.getDirectoryName().equals("Jpeg")) {
                    		if (tag.getTagType() == 0x0001) {
                    			height = directory.getInt(tag.getTagType());
                    		} else if (tag.getTagType() == 0x0003) {
                    			width = directory.getInt(tag.getTagType());
                    		}                    		
                    	} else if (tag.getDirectoryName().equals("Exif")) {
                    		if (tag.getTagType() == ExifDirectoryBase .TAG_ORIENTATION) {
                    			orientation = directory.getInt(tag.getTagType());
                    		} else if (tag.getTagType() == ExifDirectoryBase .TAG_DATETIME) {
                    			date = directory.getDate(tag.getTagType());
                    		} else if (tag.getTagType() == ExifDirectoryBase .TAG_MAKE) {
                    			manufacturer = s_pool.replace(directory.getDescription(tag.getTagType()));
                    		} else if (tag.getTagType() == ExifDirectoryBase .TAG_MODEL) {
                    			model = s_pool.replace(directory.getDescription(tag.getTagType()));
                    		} else if (tag.getTagType() == ExifDirectoryBase .TAG_EXPOSURE_TIME) {
                    			exposure_time = s_pool.replace(directory.getDescription(tag.getTagType()));
                    		} else if (tag.getTagType() == ExifDirectoryBase .TAG_SHUTTER_SPEED) {
                    			shutter_speed = s_pool.replace(directory.getDescription(tag.getTagType()));
                    		} else if (tag.getTagType() == ExifDirectoryBase .TAG_APERTURE) {
                    			aperture_value = s_pool.replace(directory.getDescription(tag.getTagType()));
                    		} else if (tag.getTagType() == ExifDirectoryBase .TAG_FLASH) {
                    			flash = s_pool.replace(directory.getDescription(tag.getTagType()));
                    		} else if (tag.getTagType() == ExifDirectoryBase .TAG_FOCAL_LENGTH) {
                    			try {
                    				final String value = directory.getDescription(tag.getTagType()).replace(" mm","").replace(",",".");
                    				focal_length = Double.parseDouble(value);
                    			} catch (final NumberFormatException e) {
                    				System.err.println("unexpected value for TAG_FOCAL_LENGTH");
                    				e.printStackTrace();
                    			}
                    		} else if (tag.getTagType() == ExifDirectoryBase.TAG_SELF_TIMER_MODE_TIFF_EP ) {
                    			self_timer_mode = s_pool.replace(directory.getDescription(tag.getTagType()));
                    		}
                    	}	 else if (tag.getDirectoryName().equals("Canon Makernote")) {
                    		if (tag.getTagType() == CanonMakernoteDirectory.CameraSettings.TAG_SELF_TIMER_DELAY) {
                    			canon_self_timer_delay = s_pool.replace(directory.getDescription(tag.getTagType()));
                    		} else if (tag.getTagType() == CanonMakernoteDirectory.CameraSettings.TAG_FLASH_MODE) {
                    			canon_flash_mode = s_pool.replace(directory.getDescription(tag.getTagType()));
                    		} else if (tag.getTagType() == CanonMakernoteDirectory.CameraSettings.TAG_CONTINUOUS_DRIVE_MODE) {
                    			canon_continuous_drive_mode = s_pool.replace(directory.getDescription(tag.getTagType()));
                    		} else if (tag.getTagType() == CanonMakernoteDirectory.CameraSettings.TAG_FOCUS_MODE_1) {
                    			canon_focus_mode = s_pool.replace(directory.getDescription(tag.getTagType()));
                    		} else if (tag.getTagType() == CanonMakernoteDirectory.CameraSettings.TAG_ISO) {
                    			canon_iso = s_pool.replace(directory.getDescription(tag.getTagType()));
                    		} else if (tag.getTagType() == CanonMakernoteDirectory.FocalLength.TAG_SUBJECT_DISTANCE) {
                    			try {
                    				final String value = directory.getDescription(tag.getTagType());
                    				canon_subject_distance = Integer.parseInt(value);
                    			} catch (final NumberFormatException e) {
                    				System.err.println("unexpected value for TAG_SUBJECT_DISTANCE");
                    				e.printStackTrace();
                    			}
                    		}
                    	}	 else if (tag.getDirectoryName().equals("GPS")) {
                    		if (tag.getTagType() == GpsDirectory.TAG_LATITUDE_REF) {
                    			latitudeRef = directory.getDescription(tag.getTagType());
                    		} else if (tag.getTagType() == GpsDirectory.TAG_LATITUDE) {
                    			latitude = directory.getDescription(tag.getTagType());
                    		} else if (tag.getTagType() == GpsDirectory.TAG_LONGITUDE_REF) {
                    			longitudeRef = directory.getDescription(tag.getTagType());
                    		} else if (tag.getTagType() == GpsDirectory.TAG_LONGITUDE) {
                    			longitude = directory.getDescription(tag.getTagType());
                    		} else if (tag.getTagType() == GpsDirectory.TAG_ALTITUDE_REF) {
                    			altitudeRef = directory.getDescription(tag.getTagType());
                    		} else if (tag.getTagType() == GpsDirectory.TAG_ALTITUDE) {
                    			altitude = directory.getDescription(tag.getTagType());
                    		}
                    	}
                    } catch (final MetadataException e) {
                    	// should never occur, the data is corrupted
                    	isCorrectlyParsed = false;
                    	System.err.println("failed to parse "+overridenFilename);
                    	e.printStackTrace();
                    }
                }
            }
        } catch (final IOException | ImageProcessingException e) {
			// should never occur, the data is corrupted
        	isCorrectlyParsed = false;
        	System.err.println("failed to parse "+overridenFilename);
			e.printStackTrace();
    		a_isCorrectlyParsed = false;
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
	    	a_latitude = DEFAULT_LATITUDE;
	    	a_longitude = DEFAULT_LONGITUDE;
	    	a_altitude = DEFAULT_ALTITUDE; 
	    	return;
        }
        
        // JPEG file with no tag (I'll have to read the JPEG and EXIF specifications...)
        if ( format == DataFormat.JPEG &&
        	( width == 0 || height == 0 ) ) {
        	final File file = new File(overridenFilename);
        	if (file.exists()) {
	        	try {
	        	    final BufferedImage image = ImageIO.read(file);
	        	    height = image.getHeight();
	        	    width = image.getWidth();
	        	} catch (final IOException e) {
                	isCorrectlyParsed = false;
                	System.err.println("failed to parse "+overridenFilename);
        			e.printStackTrace();    		a_isCorrectlyParsed = false;
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
        	    	a_latitude = DEFAULT_LATITUDE;
        	    	a_longitude = DEFAULT_LONGITUDE;
        	    	a_altitude = DEFAULT_ALTITUDE; 
        	    	return;	
	        	}
            }
        }
        
        a_isCorrectlyParsed = isCorrectlyParsed;
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
    	
		final Double lat = parseLatitude(latitude, latitudeRef);
		if ( lat!=null ) {
			a_latitude = lat.doubleValue();
		} else {
	    	a_latitude = DEFAULT_LONGITUDE;
		}

		final Double lon = parseLongitude(longitude, longitudeRef);
		if ( lon!=null ) {
			a_longitude = lon.doubleValue();
		} else {
	    	a_longitude = DEFAULT_LONGITUDE;
		}

		final Double alt = parseAltitude(altitude, altitudeRef);
		if ( alt!=null ) {
			a_altitude = alt.doubleValue();
		} else {
	    	a_altitude = DEFAULT_ALTITUDE;
		}
    }

    /**
     * create from a String Array
     * @see #getStringArray()
     * @param data string array encoding this object
     */
    public PhotoHeaderData(final String data[]) {

		a_filename = data[0];

		a_isCorrectlyParsed = true;

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
			//TODO suppress this useless test
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

    	a_latitude = Double.parseDouble(data[19]);
    	a_longitude = Double.parseDouble(data[20]);
    	a_altitude = Double.parseDouble(data[21]);
    }
    
    /**
     * return as a String array
     * very dirty kludge: the first element is the filename and PhotoHeaderDataCache knows about it
     * 
     * @return string array encoding this object
     */
    public String[] getStringArray() {
    	
    	final String array[] = new String[22];
    	
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
        array[19] = Double.toString(a_latitude);
        array[20] = Double.toString(a_longitude);
        array[21] = Double.toString(a_altitude);
 
        return array;
    }
    
    /**
     * @return true is the file was correctly parse, false otherwise
     */
    public boolean isCorrectlyParsed() {
    	return a_isCorrectlyParsed;
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
     * 1 = The 0th row represents the visual top of the image, and the 0th column represents the visual left-hand side.<br/>
  	 * 2 = The 0th row represents the visual top of the image, and the 0th column represents the visual right-hand side.<br/>
	 * 3 = The 0th row represents the visual bottom of the image, and the 0th column represents the visual right-hand side.<br/>
	 * 4 = The 0th row represents the visual bottom of the image, and the 0th column represents the visual left-hand side.<br/>
	 * 5 = The 0th row represents the visual left-hand side of the image, and the 0th column represents the visual top.<br/>
	 * 6 = The 0th row represents the visual right-hand side of the image, and the 0th column represents the visual top.<br/>
	 * 7 = The 0th row represents the visual right-hand side of the image, and the 0th column represents the visual bottom.<br/>
	 * 8 = The 0th row represents the visual left-hand side of the image, and the 0th column represents the visual bottom.<br/>
	 * 
     * @return orientation of the photo (tag 0x0112)
     */
    public int getOrientation() {
        return a_orientation;
    }
    
    /**
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

    /**
     * @return the latitude, NaN if undefined
     */
    public double getLatitude() {
    	return a_latitude;
    }
    
    /**
     * @return the longitude, NaN if undefined
     */
    public double getLongitude() {
    	return a_longitude;
    }
    
    /**
     * @return the altitude, NaN if undefined
     */
    public double getAltitude() {
    	return a_altitude;
    }
    
    /**
     * @param latitude
     * @param latitudeRef
     * @return latitude if this one is correctly defined
     * null otherwise
     */
    private Double parseLatitude(final String latitude,
    							 final String latitudeRef)
    {
    	if (latitude==null) return null;
    	final Double x = parseXxitude(latitude);
    	if ( x == null ) return null;
    	
    	if ( latitudeRef==null ) return null;
    	if ( latitudeRef.equals("N")) return x;
    	if ( latitudeRef.equals("S")) return Double.valueOf(-x.doubleValue());
    	return null;
    }

    /**
     * @param longitude
     * @param longitudeRef
     * @return longitude if this one is correctly defined
     * null otherwise
     */
    private Double parseLongitude(final String longitude,
    		 				      final String longitudeRef)
    {
    	if (longitude==null) return null;
    	final Double x = parseXxitude(longitude);
    	if ( x == null ) return null;
    	
    	if ( longitudeRef==null ) return null;
    	if ( longitudeRef.equals("E")) return x;
    	if ( longitudeRef.equals("O")) return Double.valueOf(-x.doubleValue());
    	return null;
    }

    /**
     * @param Xxitude
     * @return unsigned latitude/longitude if this one is correctly defined
     * null otherwise
     */
    private Double parseXxitude(final String Xxitude)
    {
		final Pattern pattern = Pattern.compile( "(\\d+)\"(\\d+)'(\\d+\\.\\d*)");
		final Matcher matcher = pattern.matcher(Xxitude);
		if ( !matcher.matches() ) return null;
		
		final String s1 = matcher.group(1);
		int value1 = Integer.parseInt(s1);
		final String s2 = matcher.group(2);
		int value2 = Integer.parseInt(s2);
		final String s3 = matcher.group(3);
		double value3 = Double.parseDouble(s3);
		
		double value = (double)value1 + (double)value2/60.0 + value3/3600.0; 

		return Double.valueOf(value);
    }
    
    /**
     * @param altitude
     * @param altitudeRef
     * @return altitude if this one is correctly defined
     * null otherwise
     */
    private Double parseAltitude(final String altitude,
    							 final String altitudeRef)
    {
    	if (altitude==null) return null;
    	if (altitudeRef==null) return null;
    	
    	if ( !altitudeRef.equals("Sea level")) return null;
    	
		final Pattern pattern = Pattern.compile( "(\\d+)/(\\d+) metres");
        final Matcher matcher = pattern.matcher(altitude);
        if ( !matcher.matches() ) return null;
        
        final String s1 = matcher.group(1);
        int value1 = Integer.parseInt(s1);
        final String s2 = matcher.group(2);
        int value2 = Integer.parseInt(s2);
        if (value2==0) return null;
        
        return Double.valueOf((double)value1/(double)value2);
    }
}
