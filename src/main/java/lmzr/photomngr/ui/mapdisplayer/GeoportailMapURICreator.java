package lmzr.photomngr.ui.mapdisplayer;

import java.net.URI;
import java.net.URISyntaxException;

import lmzr.photomngr.data.GPS.GPSData;
import lmzr.photomngr.data.GPS.GPSDatabase.GPSRecord;

/**
 * @author Laurent Mazuré
 */
public class GeoportailMapURICreator extends MapURICreator {

    /**
     *
     */
    public GeoportailMapURICreator()
    {
    }

    /**
     * @see lmzr.photomngr.ui.mapdisplayer.MapURICreator#getName()
     */
    @Override
    public String getName()
    {
        return "Geoportail";
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
            final double z = rangeInMeters / ( 2.0 * s_earthRadiusInMeters );

            final String str = "http://www.geoportail.fr/accueil?c="
                               + data.getLongitudeAsDouble()
                               + ","
                               + data.getLatitudeAsDouble()
                               + "&z="
                               + z
                               + "&l=GEOGRAPHICALGRIDSYSTEMS.MAPS.3D$GEOPORTAIL:OGC:WMTS@aggregate(1)&permalink=yes";
            uri = new URI(str);
        } catch (final URISyntaxException e) {
            // this should never occur
            e.printStackTrace();
        }

        return uri;
    }

}
