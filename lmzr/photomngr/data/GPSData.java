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
		a_latitudeMin = parse(latitudeMin);
		a_longitudeMin = parse(longitudeMin);
		a_latitudeMax = parse(latitudeMax);
		a_longitudeMax = parse(longitudeMax);
	}

	/**
	 * @return the a_latitude
	 */
	public double getLatitude() {
		return (a_latitudeMin+a_latitudeMax)/2;
	}

	/**
	 * @return the a_latitudeRange
	 */
	public double getLatitudeRange() {
		return Math.abs(a_latitudeMax-a_latitudeMin);
	}

	/**
	 * @return the a_location
	 */
	public HierarchicalCompoundString getLocation() {
		return a_location;
	}

	/**
	 * @return the a_longitude
	 */
	public double getLongitude() {
		return (a_longitudeMin+a_longitudeMax)/2;
	}

	/**
	 * @return the a_longitudeRange
	 */
	public double getLongitudeRange() {
		return Math.abs(a_longitudeMax-a_longitudeMin);
	}
	
	/**
	 * parse a coordinate into a double
	 * @param str
	 * @return coordinate as a double
	 */
	private double parse(final String str) {
		final Pattern pattern = Pattern.compile("([EONS]) (\\d{1,2})° (\\d{1,2})' (\\d{1,2})''");
		final Matcher matcher = pattern.matcher(str);
		if ( !matcher.matches() ) throw new IllegalArgumentException();
		final String s1 = matcher.group(1);
		double value = Integer.parseInt(matcher.group(2)) + Integer.parseInt(matcher.group(3))/60.0 + Integer.parseInt(matcher.group(4))/3600.0;
		if ( (s1.compareTo("S")==0) || (s1.compareTo("O")==0) ) {
			return -value;  
		}
		return value;
	}
}
