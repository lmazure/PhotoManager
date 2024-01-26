package lmzr.photomngr.data;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import lmzr.photomngr.scheduler.Scheduler;
import lmzr.util.io.StringTableFromToExcel;

/**
 * Cache the header data.
 * This class is designed to be be simply dropped in between Photo and PhotoHeaderData.
 * I should surely break this "simple drop in" rule to have a better implementation saving CPU and memory.
 *
 * @author Laurent Mazuré
 *
 */
public class PhotoHeaderDataCache {

    /**
     * Cache for one image folder.
     *
     */
    private class FolderCache {

        private final Map<String,PhotoHeaderData> a_fileCache;
        private final String a_folderName;
        private boolean a_isDirty;

        public FolderCache(final String folderName) {
            a_folderName = folderName;
            a_fileCache = new HashMap<>();
            a_isDirty = false;
            loadFromDisk();
        }

        public PhotoHeaderData getHeaderData(final String filename,
                                             final DataFormat format) {

            final PhotoHeaderData headerData = a_fileCache.get(filename);

            if ( headerData != null ) {
                return headerData;
            }

            final PhotoHeaderData newHeaderData = new PhotoHeaderData(a_photoDirectory, a_folderName, filename, format);

            synchronized(a_fileCache) {
                a_fileCache.put(filename, newHeaderData);
            }
            a_isDirty = true;

            return newHeaderData;
        }

        private void loadFromDisk() {
            String data[][] = null;
            try {
                data = StringTableFromToExcel.read(getFilename());
            } catch (final IOException e) {
                // do nothing if the file is not available
                return;
            }

            for (final String row[] : data) {
                final PhotoHeaderData newHeaderData = new PhotoHeaderData(row);
                a_fileCache.put(row[0],newHeaderData);
            }
        }

        private void saveToDisk() {

            if ( !a_isDirty ) {
                return;
            }

            final String data[][];
            int i=0;

            synchronized (a_fileCache) {
                data = new String[a_fileCache.size()][];
                for (final PhotoHeaderData headerData: a_fileCache.values()) {
                    if ( headerData.isCorrectlyParsed() ) {
                        data[i++] = headerData.getStringArray();
                    }
                }
            }
            final int size = i;
            a_isDirty = false;

            // create the folder cache if necessary
            final File directory = new File(getFoldername());
            directory.mkdir();

            a_scheduler.submitIO("save header data cache of \""+a_folderName+"\"",
                () -> {
                    try {
                        StringTableFromToExcel.save(getFilename(),Arrays.copyOf(data, size));
                    } catch (final IOException e) {
                        System.err.println("failed to save header data cache in \""+getFilename()+"\"");
                        return;
                    }
                });

        }

        private String getFoldername() {
            return a_cacheDirectory + File.separator + a_folderName;
        }

        private String getFilename() {
            return getFoldername() + File.separator + "headerDataCache.txt";
        }
    }

    private final String a_photoDirectory;
    private final String a_cacheDirectory;
    private final Scheduler a_scheduler;
    private final Map<String,FolderCache> a_folderCache;
    private final Timer a_timerForPeriodicSaves;
    static private final long SAVE_PERIODICITY = 10000;

    /**
     * @param photoDirectory
     * @param cacheDirectory
     * @param scheduler
     */
    public PhotoHeaderDataCache(final String photoDirectory,
                                final String cacheDirectory,
                                final Scheduler scheduler) {

        a_photoDirectory = photoDirectory;
        a_cacheDirectory = cacheDirectory;
        a_scheduler = scheduler;
        a_folderCache = new HashMap<>();
        a_timerForPeriodicSaves = new Timer();
        a_timerForPeriodicSaves.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                save();
            }
        }, SAVE_PERIODICITY, SAVE_PERIODICITY);
    }

    /**
     * @param folderName
     * @param filename
     * @param format
     * @return header data
     */
    public PhotoHeaderData getHeaderData(final String folderName,
                                         final String filename,
                                         final DataFormat format) {

        FolderCache folderCache = a_folderCache.get(folderName);

        if  ( folderCache == null ) {
            folderCache = new FolderCache(folderName);
            synchronized(a_folderCache) {
                a_folderCache.put(folderName, folderCache);
            }
        }

        return folderCache.getHeaderData(filename,format);
    }

    /**
     * Save all the cache on disk.
     */
    private void save() {

        synchronized(a_folderCache) {
            for ( final FolderCache folderCache : a_folderCache.values() ) {
                folderCache.saveToDisk();
            }
        }

    }

}
