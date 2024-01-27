package lmzr.photomngr.ui.mapdisplayer;

import java.net.URI;

import lmzr.photomngr.data.GPS.GPSData;
import lmzr.photomngr.data.GPS.GPSDatabase.GPSRecord;

/**
 * @author Laurent Mazuré
 */
public abstract class MapURICreator {

    /**
     *
     */
    static protected final double s_earthRadiusInMeters = 6365000;

    /**
     * @param record
     * @return URL displaying the corresponding map
     */
    public abstract URI createMapURIFromGPSData(final GPSRecord record);

    /**
     * @return name of the site
     */
    public abstract String getName();

    /**
     * @param data
     * @return longitude range in meters
     */
    static public double getLongitudeRangeInMeters(final GPSData data)
    {
        return ( data.getLatitudeRangeAsDouble().doubleValue() / 180.0)  *  Math.PI * s_earthRadiusInMeters * Math.cos(data.getLatitudeRangeAsDouble().doubleValue() / 180.0);
    }

    /**
     * @param data
     * @return latitude range in meters
     */
    static public double getLatitudeRangeInMeters(final GPSData data)
    {
        return ( data.getLatitudeRangeAsDouble().doubleValue() / 180.0)  *  Math.PI * s_earthRadiusInMeters;
    }

    /**
     * @param data
     * @return latitude range in meters
     */
    static public double getRangeInMeters(final GPSData data)
    {
        return Math.max(getLongitudeRangeInMeters(data), getLatitudeRangeInMeters(data));
    }

}
