package lmzr.photomngr.data;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lmzr.util.string.HierarchicalCompoundString;

/**
 * @author Laurent
 *
 */
public class GPSData {

	private HierarchicalCompoundString a_location;
	private double a_latitudeMin;
	private double a_longitudeMin;
    private double a_latitudeMax;
    private double a_longitudeMax;
    
	/**
	 * Create a new GPS point
	 * some coordinate strings can be set to null, this means that the coordinates is undefined
	 * 
	 * @param location
	 * @param latitudeMin
	 * @param longitudeMin
	 * @param latitudeMax
	 * @param longitudeMax
	 */
	public GPSData(final HierarchicalCompoundString location,
			       final String latitudeMin,
			       final String longitudeMin,
			       final String latitudeMax,
			       final String longitudeMax) {
		a_location = location;
		setLagitudeMin(latitudeMin);
		setLongitudeMin(longitudeMin);
		setLagitudeMax(latitudeMax);
		setLongitudeMax(longitudeMax);
	}

	/**
	 * @return true if the GPS coordinates are correct, false otherwise
	 */
	public boolean isComplete() {
		if (Double.isNaN(a_latitudeMin)) return false;
		if (Double.isNaN(a_latitudeMax)) return false;		
		if (Double.isNaN(a_longitudeMin)) return false;
		if (Double.isNaN(a_longitudeMax)) return false;
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
	 * @param laditudeMin
	 */
	public void setLagitudeMin(final String latitudeMin) {
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
	 * @param lagitudeMax
	 */
	public void setLagitudeMax(final String latitudeMax) {
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
	public Double getLatitude() {
		if (Double.isNaN(a_latitudeMin)) return null;
		if (Double.isNaN(a_latitudeMax)) return null;
		return (a_latitudeMin+a_latitudeMax)/2;
	}

   /**
	 * @return minimum latitude
	 *         null if undefined
	 */
	public Double getLatitudeMin() {
		if (Double.isNaN(a_latitudeMin)) return null;
		return a_latitudeMin;
	}

   /**
	 * @return maximum latitude
	 *         null if undefined
	 */
	public Double getLatitudeMax() {
		if (Double.isNaN(a_latitudeMax)) return null;
		return a_latitudeMax;
	}
	
	/**
	 * @return latitude range
	 *         null if undefined
	 */
	public Double getLatitudeRange() {
		if (Double.isNaN(a_latitudeMin)) return null;
		if (Double.isNaN(a_latitudeMax)) return null;
		return Math.abs(a_latitudeMax-a_latitudeMin);
	}

	/**
	 * @return central longitude
	 *         null if undefined
	 */
	public Double getLongitude() {
		if (Double.isNaN(a_longitudeMin)) return null;
		if (Double.isNaN(a_longitudeMax)) return null;
		return (a_longitudeMin+a_longitudeMax)/2;
	}

   /**
	 * @return minimum longitude
	 *         null if undefined
	 */
	public Double getLongitudeMin() {
		if (Double.isNaN(a_longitudeMin)) return null;
		return a_longitudeMin;
	}

   /**
	 * @return maximum longitude
	 *         null if undefined
	 */
	public Double getLongitudeMax() {
		if (Double.isNaN(a_longitudeMax)) return null;
		return a_longitudeMax;
	}
	
	/**
	 * @return longitude range
	 *         null if undefined
	 */
	public Double getLongitudeRange() {
		if (Double.isNaN(a_longitudeMin)) return null;
		if (Double.isNaN(a_longitudeMax)) return null;
		return Math.abs(a_longitudeMax-a_longitudeMin);
	}
	
	/**
	 * @return location
	 */
	public HierarchicalCompoundString getLocation() {
		return a_location;
	}

	/**
	 * parse a string into a longitude
	 * 
	 * @param str
	 * @return
	 */
	private double parseLongitude(final String str) {
		return parseXxitude(str,'E','O');
	}
	
	/**
	 * parse a string into a latitude
	 * 
	 * @param str
	 * @return
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
}
