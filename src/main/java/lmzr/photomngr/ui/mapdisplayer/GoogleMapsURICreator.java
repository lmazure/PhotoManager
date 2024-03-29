package lmzr.photomngr.ui.mapdisplayer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import lmzr.photomngr.data.Photo;
import lmzr.photomngr.data.PhotoHeaderData;
import lmzr.photomngr.data.PhotoList;
import lmzr.photomngr.data.GPS.GPSData;
import lmzr.photomngr.data.GPS.GPSDatabase;
import lmzr.photomngr.data.GPS.GPSDatabase.GPSRecord;
import lmzr.util.string.HierarchicalCompoundString;

/**
 * @author Laurent Mazuré
 */
public class GoogleMapsURICreator extends MapURICreator {

    static final private String s_templateName = "googleMapsTemplate.html";

    static final private String s_apiKeyVarEnvName = "GOOGLE_MAPS_API_KEY";

    static final private String s_apiKeyPlaceholder = "__PLACEHOLDER_APIKEY__";
    static final private String s_areaListPlaceholder = "__PLACEHOLDER_AREALIST__";
    static final private String s_pointListPlaceholder = "__PLACEHOLDER_POINTLIST__";
    static final private String s_mapCenterPlaceholder = "__PLACEHOLDER_MAPCENTER__";
    static final private String s_zoomPlaceholder = "__PLACEHOLDER_ZOOM__";

    static final private String s_defaultCenter = "47.0, 2.0";
    static final private String s_defaultZoom = "6";

    /**
     *
     */
    public GoogleMapsURICreator()
    {
    }

    /**
     * @see lmzr.photomngr.ui.mapdisplayer.MapURICreator#getName()
     */
    @Override
    public String getName()
    {
        return "Google";
    }

    /**
     * @see lmzr.photomngr.ui.mapdisplayer.MapURICreator#createMapURLFromGPSData(lmzr.util.string.HierarchicalCompoundString, lmzr.photomngr.data.GPS.GPSData)
     */
    @Override
    public URI createMapURIFromGPSData(final GPSRecord record) {

        final HierarchicalCompoundString location = record.getLocation();
        final GPSData data = record.getGPSData();

        URI uri = null;

        try {
            final String str = "http://maps.google.com/maps?q="
                               + data.getLatitudeAsDouble()
                               + "+"
                               + data.getLongitudeAsDouble()
                               + "+("
                               + URLEncoder.encode(location.toLongString().replace(">","/"),"UTF-8")
                               +")&ll="
                               + data.getLatitudeAsDouble()
                               + ","
                               + data.getLongitudeAsDouble()
                               + "&spn="
                               + data.getLatitudeRangeAsDouble()
                               + ","
                               + data.getLongitudeRangeAsDouble()
                               + "&t=h&hl=fr";
            uri = new URI(str);
        } catch (final UnsupportedEncodingException | URISyntaxException e) {
            // this should never occur
            e.printStackTrace();
        }

        return uri;
    }

    /**
     * create a file displaying all the GPS area in blue, except for locationToHighlight which will be displayed
     * in red.
     * @param cacheDirectory      name of the cache directory
     * @param folder              name of the folder
     * @param filename            name of the file where to write the HTML and JavaScript data
     * @param photoList           list of photos
     * @param locationToHighlight current location to highlight
     * @param gpsDatabase         GPS database
     * @return created file
     * @throws IOException           if an I/O error occurs
     * @throws IllegalStateException if environment variable GOOGLE_MAPS_API_KEY is undefined
     */
    public static File createMapURIForGPSDebug(final String cacheDirectory,
                                               final String folder,
                                               final String filename,
                                               final PhotoList photoList,
                                               final HierarchicalCompoundString locationToHighlight,
                                               final GPSDatabase gpsDatabase) throws IOException
    {
        final String apiKey = System.getenv(s_apiKeyVarEnvName);
        if (apiKey == null) {
            throw new IllegalStateException("environment variable " + s_apiKeyVarEnvName + " is undefined");
        }
        final File file = new File(cacheDirectory + File.separator + folder + File.separator + filename);

        final File directory = new File(cacheDirectory + File.separator + folder);
        directory.mkdir();

        final InputStream inputStream = GoogleMapsURICreator.class.getClassLoader().getResourceAsStream(s_templateName);
        if (inputStream == null) {
            throw new IllegalArgumentException("Resource file " + s_templateName + " not found");
        }

        try (final InputStreamReader streamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
             final BufferedReader in = new BufferedReader(streamReader);
             final Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF8"))) {
            String str;
            while  ( (str = in.readLine()) != null ) {
                out.write(handleLine(str, apiKey, photoList, locationToHighlight, gpsDatabase));
                out.write('\n');
            }
        } catch (final IOException e) {
            System.err.println("Failed to read resource file ");
            e.printStackTrace();
        }

        return file;
    }

    /**
     * edit a line by replacing the placeholders by their real content
     *
     * @param string              current template line
     * @param apiKey              API key
     * @param photoList           list of photos
     * @param locationToHighlight current location to highlight
     * @param gpsDatabase         GPS database
     * @return
     */
    static private String handleLine(final String string,
                                     final String apiKey,
                                     final PhotoList photoList,
                                     final HierarchicalCompoundString locationToHighlight,
                                     final GPSDatabase gpsDatabase) {

        String str = string;

        if (string.indexOf(s_apiKeyPlaceholder) >= 0) {
            str = str.replace(s_apiKeyPlaceholder, apiKey);
        }

        if (string.indexOf(s_areaListPlaceholder) >= 0) {
            final Map<HierarchicalCompoundString,GPSRecord> locations = getLocations(photoList, gpsDatabase);
            str = str.replace(s_areaListPlaceholder, listOfGPSAreas(locationToHighlight, locations));
        }

        if (string.indexOf(s_pointListPlaceholder) >= 0) {
            str = str.replace(s_pointListPlaceholder, listOfGPSPoints(photoList));
        }

        if (string.indexOf(s_mapCenterPlaceholder) >= 0) {
            str = str.replace(s_mapCenterPlaceholder, center(locationToHighlight, gpsDatabase));
        }

        if (string.indexOf(s_zoomPlaceholder) >= 0) {
            str = str.replace(s_zoomPlaceholder, zoom(locationToHighlight, gpsDatabase));
        }

        return str;
    }

    static private Map<HierarchicalCompoundString,GPSRecord> getLocations(final PhotoList photoList,
                                                                          final GPSDatabase gpsDatabase)
    {
        final Map<HierarchicalCompoundString,GPSRecord> locations = new HashMap<>();
        final Set<HierarchicalCompoundString> allLocations = new HashSet<>();

        for (int i = 0; i < photoList.getRowCount(); i++) {
            final Photo photo = photoList.getPhoto(i);
            final HierarchicalCompoundString location = photo.getIndexData().getLocation();
            if ( !allLocations.contains(location))
            {
                allLocations.add(location);
                final GPSRecord record = gpsDatabase.getGPSData(location);
                if ( record!= null )
                {
                    final HierarchicalCompoundString l = record.getLocation();
                    if (!locations.containsKey(l))
                    {
                        locations.put(l, record);
                    }
                }
            }
        }

        return locations;
    }

    static private String center(final HierarchicalCompoundString locationToHighlight,
                                 final GPSDatabase gpsDatabase)
    {
        String center = s_defaultCenter;
        final GPSRecord record = gpsDatabase.getGPSData(locationToHighlight);
        if (record != null) {
            final GPSData gps = record.getGPSData();
            if ((gps != null) && gps.isComplete()) {
                final Double latitude = gps.getLatitudeAsDouble();
                final Double longitude = gps.getLongitudeAsDouble();
                center = latitude.toString() + "," + longitude.toString();
            }
        }

        return center;
    }

    static private String zoom(final HierarchicalCompoundString locationToHighlight,
            final GPSDatabase gpsDatabase)
    {
        String zoom = s_defaultZoom;
        final GPSRecord record = gpsDatabase.getGPSData(locationToHighlight);
        if (record != null) {
            final GPSData gps = record.getGPSData();
            if ( gps != null && gps.isComplete() ) {
                final double rangeInMeters = getRangeInMeters(gps);
                final double z = Math.log(rangeInMeters)/Math.log(2);
                final int zoomAsInt = 25 - (int)Math.floor(z);
                zoom = Integer.toString(zoomAsInt);
            }
        }

        return zoom;
    }

    /**
     * @param photoList
     * @return
     */
    static private String listOfGPSPoints(final PhotoList photoList)
    {
        final StringBuilder str = new StringBuilder();
        boolean stringHasBeenAdded = false;

        for (int i = 0; i < photoList.getRowCount(); i++) {
            final Photo photo = photoList.getPhoto(i);
            final PhotoHeaderData headerData = photo.getHeaderData();
            final double latitude = headerData.getLatitude();
            final double longitude = headerData.getLongitude();
            final String character = "%E2%80%A2"; //TODO l'encoding HTML devrait être fait par le code JS au moment où il génère l'URL
            final String color ="AAAA00";
            if ( !Double.isNaN(latitude) && !Double.isNaN(longitude) ) {
                if ( stringHasBeenAdded ) {
                    str.append(",\n");
                }
                str.append("new GPSPoint(\"");
                str.append(photo.getFullPath().replace("\\", "\\\\"));
                str.append("\",");
                str.append(latitude);
                str.append(",");
                str.append(longitude);
                str.append(",");
                str.append("\""+character+"\"");
                str.append(",");
                str.append("\""+color+"\"");
                str.append(")");
                stringHasBeenAdded = true;
            }
        }

        return str.toString();
    }

    static private String listOfGPSAreas(final HierarchicalCompoundString locationToHighlight,
                                         final Map<HierarchicalCompoundString, GPSRecord> locations)
    {
        final StringBuilder str = new StringBuilder();
        boolean stringHasBeenAdded = false;

        for (final HierarchicalCompoundString location: locations.keySet())
        {
            final GPSData data = locations.get(location).getGPSData();
            final String color = location.equals(locationToHighlight)?"#FF0000":"#0000FF";
            if ( stringHasBeenAdded ) {
                str.append(",\n");
            }
            str.append("new GPSRectangle(\"");
            str.append(location);
            str.append("\",");
            str.append(data.getLatitudeMinAsDouble());
            str.append(",");
            str.append(data.getLatitudeMaxAsDouble());
            str.append(",");
            str.append(data.getLongitudeMinAsDouble());
            str.append(",");
            str.append(data.getLongitudeMaxAsDouble());
            str.append(",");
            str.append("\""+color+"\"");
            str.append(")");
            stringHasBeenAdded = true;
        }

        return str.toString();
    }
}
