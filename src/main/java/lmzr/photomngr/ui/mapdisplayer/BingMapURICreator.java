package lmzr.photomngr.ui.mapdisplayer;

import java.net.URI;
import java.net.URISyntaxException;

import lmzr.photomngr.data.GPS.GPSData;
import lmzr.photomngr.data.GPS.GPSDatabase.GPSRecord;

/**
 * @author Laurent Mazuré
 */
public class BingMapURICreator extends MapURICreator {

    /**
     *
     */
    public BingMapURICreator()
    {
    }

    /**
     * @see lmzr.photomngr.ui.mapdisplayer.MapURICreator#getName()
     */
    @Override
    public String getName()
    {
        return "Bing";
    }

    /**
     * @see lmzr.photomngr.ui.mapdisplayer.MapURICreator#createMapURLFromGPSData(lmzr.util.string.HierarchicalCompoundString, lmzr.photomngr.data.GPS.GPSData)
     */
    @Override
    public URI createMapURIFromGPSData(final GPSRecord record) {

        final GPSData data = record.getGPSData();

        URI uri = null;

        try {
            final double rangeInMeters = getRangeInMeters(data);
            final double z = Math.log(rangeInMeters)/Math.log(2);
            final int zoom = 25 - (int)Math.floor(z);

            final String str = "http://bing.com/maps/default.aspx?cp="
                               + data.getLatitudeAsDouble()
                               + "~"
                               + data.getLongitudeAsDouble()
                               + "&lvl="
                               + zoom
                               + "&style=h";
            uri = new URI(str);
        } catch (final URISyntaxException e) {
            // this should never occur
            e.printStackTrace();
        }

        return uri;
    }
}
