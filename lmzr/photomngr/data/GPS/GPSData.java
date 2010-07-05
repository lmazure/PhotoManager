package lmzr.photomngr.data.GPS;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Laurent
 *
 */
public class GPSData implements Cloneable {

	private double a_latitudeMin;
	private double a_longitudeMin;
    private double a_latitudeMax;
    private double a_longitudeMax;
    
	/**
	 * Create a new GPS point
	 * some coordinate strings can be set to null, this means that the coordinates is undefined
	 * 
	 * @param latitudeMin
	 * @param longitudeMin
	 * @param latitudeMax
	 * @param longitudeMax
	 */
	public GPSData(final String latitudeMin,
			       final String longitudeMin,
			       final String latitudeMax,
			       final String longitudeMax) {
		setLatitudeMin(latitudeMin);
		setLongitudeMin(longitudeMin);
		setLatitudeMax(latitudeMax);
		setLongitudeMax(longitudeMax);
	}

	/**
	 * @see java.lang.Object#clone()
	 */
	@Override
    public GPSData clone() {
		return new GPSData(getLatitudeMin(),getLongitudeMin(),getLatitudeMax(),getLongitudeMax());
	}
	
	/**
	 * @return true if the GPS coordinates are fully defined, false otherwise
	 */
	public boolean isComplete() {
		if (Double.isNaN(a_latitudeMin)) return false;
		if (Double.isNaN(a_latitudeMax)) return false;		
		if (Double.isNaN(a_longitudeMin)) return false;
		if (Double.isNaN(a_longitudeMax)) return false;
		return true;
	}

	/**
     * @return true if at least no GPS coordinates is defined, false otherwise
     */
    public boolean isEmpty() {
        if (!Double.isNaN(a_latitudeMin)) return false;
        if (!Double.isNaN(a_latitudeMax)) return false;      
        if (!Double.isNaN(a_longitudeMin)) return false;
        if (!Double.isNaN(a_longitudeMax)) return false;
        return true;
    }

	/**
	 * Set the minimum longitude
	 * The string can be null, this means that the minimum longitude is undefined
	 * 
	 * @param longitudeMin
	 */
	public void setLongitudeMin(final String longitudeMin) {
		if ( longitudeMin != null ) {
			a_longitudeMin = parseLongitude(longitudeMin);			
		} else {
			a_longitudeMin = Double.NaN;			
		}
	}
	
	/**
	 * Set the maximum longitude
	 * The string can be null, this means that the maximum longitude is undefined
	 * 
	 * @param longitudeMax
	 */
	public void setLongitudeMax(final String longitudeMax) {
		if ( longitudeMax != null ) {
			a_longitudeMax = parseLongitude(longitudeMax);			
		} else {
			a_longitudeMax = Double.NaN;			
		}
	}
	
	/**
	 * Set the minimum latitude
	 * The string can be null, this means that the minimum latitude is undefined
	 * 
	 * @param latitudeMin
	 */
	public void setLatitudeMin(final String latitudeMin) {
		if ( latitudeMin != null ) {
			a_latitudeMin = parseLatitude(latitudeMin);			
		} else {
			a_latitudeMin = Double.NaN;			
		}
	}
	
	/**
	 * Set the maximum latitude
	 * The string can be null, this means that the maximum latitude is undefined
	 * 
	 * @param latitudeMax
	 */
	public void setLatitudeMax(final String latitudeMax) {
		if ( latitudeMax != null ) {
			a_latitudeMax = parseLatitude(latitudeMax);			
		} else {
			a_latitudeMax = Double.NaN;			
		}
	}
	
   /**
	 * @return central latitude
	 *         null if undefined
	 */
	public Double getLatitudeAsDouble() {
		if (Double.isNaN(a_latitudeMin)) return null;
		if (Double.isNaN(a_latitudeMax)) return null;
		return (a_latitudeMin+a_latitudeMax)/2;
	}

   /**
	 * @return minimum latitude
	 *         null if undefined
	 */
	public Double getLatitudeMinAsDouble() {
		if (Double.isNaN(a_latitudeMin)) return null;
		return a_latitudeMin;
	}

   /**
	 * @return minimum latitude
	 *         null if undefined
	 */
	public String getLatitudeMin() {
		if (Double.isNaN(a_latitudeMin)) return null;
		return formatLatitude(a_latitudeMin);
	}

   /**
	 * @return maximum latitude
	 *         null if undefined
	 */
	public Double getLatitudeMaxAsDouble() {
		if (Double.isNaN(a_latitudeMax)) return null;
		return a_latitudeMax;
	}
	
   /**
	 * @return maximum latitude
	 *         null if undefined
	 */
	public String getLatitudeMax() {
		if (Double.isNaN(a_latitudeMax)) return null;
		return formatLatitude(a_latitudeMax);
	}

	/**
	 * @return latitude range
	 *         null if undefined
	 */
	public Double getLatitudeRangeAsDouble() {
		if (Double.isNaN(a_latitudeMin)) return null;
		if (Double.isNaN(a_latitudeMax)) return null;
		return Math.abs(a_latitudeMax-a_latitudeMin);
	}

	/**
	 * @return central longitude
	 *         null if undefined
	 */
	public Double getLongitudeAsDouble() {
		if (Double.isNaN(a_longitudeMin)) return null;
		if (Double.isNaN(a_longitudeMax)) return null;
		return (a_longitudeMin+a_longitudeMax)/2;
	}

   /**
	 * @return minimum longitude
	 *         null if undefined
	 */
	public Double getLongitudeMinAsDouble() {
		if (Double.isNaN(a_longitudeMin)) return null;
		return a_longitudeMin;
	}

   /**
	 * @return minimum longitude
	 *         null if undefined
	 */
	public String getLongitudeMin() {
		if (Double.isNaN(a_longitudeMin)) return null;
		return formatLongitude(a_longitudeMin);
	}

   /**
	 * @return maximum longitude
	 *         null if undefined
	 */
	public String getLongitudeMax() {
		if (Double.isNaN(a_longitudeMax)) return null;
		return formatLongitude(a_longitudeMax);
	}

	/**
	 * @return maximum longitude
	 *         null if undefined
	 */
	public Double getLongitudeMaxAsDouble() {
		if (Double.isNaN(a_longitudeMax)) return null;
		return a_longitudeMax;
	}
	
	/**
	 * @return longitude range
	 *         null if undefined
	 */
	public Double getLongitudeRangeAsDouble() {
		if (Double.isNaN(a_longitudeMin)) return null;
		if (Double.isNaN(a_longitudeMax)) return null;
		return Math.abs(a_longitudeMax-a_longitudeMin);
	}	

	/**
	 * parse a string into a longitude
	 * 
	 * @param str longitude as a string
	 * @return longitude as a double
	 */
	private double parseLongitude(final String str) {
		return parseXxitude(str,'E','O');
	}
	
	/**
	 * parse a string into a latitude
	 * 
	 * @param str latitude as a string
	 * @return latitude as a double
	 */
	private double parseLatitude(final String str) {
		return parseXxitude(str,'N','S');
	}

	/**
	 * parse a string into a coordinate
	 * 
	 * @param str string to parse
	 * @param positiveLetter letter for positive value
	 * @param negativeLetter letter for negative value
	 * @return coordinate as a double
	 */
	private double parseXxitude(final String str,
			                    final char positiveLetter,
			                    final char negativeLetter) {
		final Pattern pattern = Pattern.compile( "([" +
				                                 positiveLetter +
				                                 negativeLetter +
				                                 "]) (\\d{1,2})° (\\d{1,2})' (\\d{1,2})''");
		final Matcher matcher = pattern.matcher(str);
		if ( !matcher.matches() ) throw new IllegalArgumentException();
		final String s1 = matcher.group(1);
		double value = Integer.parseInt(matcher.group(2)) + Integer.parseInt(matcher.group(3))/60.0 + Integer.parseInt(matcher.group(4))/3600.0;
		if ( s1.charAt(0) == negativeLetter ) {
			return -value;  
		}
		return value;
	}
	
	/**
	 * format a longitude expressed as a double into a string
	 * 
	 * @param latitude as a double
	 * @return latitude as a string
	 */
	private String formatLongitude(final double latitude) {
		return formatXxitude(latitude,'E','O');
	}

	/**
	 * format a latitude expressed as a double into a string
	 * 
	 * @param longitude as a double
	 * @return longitude as a string
	 */
	private String formatLatitude(final double longitude) {
		return formatXxitude(longitude,'N','S');
	}
	
	private String formatXxitude(final double coordinate,
                                 final char positiveLetter,
                                 final char negativeLetter) {
		
		final double val = Math.abs(coordinate)+1./7200.; // to avoid rounding errors
		final int degrees = (int)Math.floor(val);
		final int minutes = (int)Math.floor((val-degrees)*60);
		final int seconds = (int)Math.floor(((val-degrees)*60-minutes)*60);
		
		return "" +
		       ( (coordinate>0) ? positiveLetter : negativeLetter ) +
		       " " +
		       degrees +
		       "° " +
		       minutes +
		       "' " +
		       seconds +
		       "''";
	}

}
