package lmzr.photomngr.ui;

import java.awt.Rectangle;
import java.io.File;

import javax.swing.SwingUtilities;

import lmzr.photomngr.data.ConcretePhotoList;
import lmzr.photomngr.data.ListSelectionManager;
import lmzr.photomngr.data.GPS.GPSDatabase;
import lmzr.photomngr.data.filter.FilteredPhotoList;
import lmzr.photomngr.imagecomputation.SubsampledImageCachedManager;
import lmzr.photomngr.scheduler.Scheduler;
import lmzr.util.chrono.Chrono;


/**
 * @author Laurent Mazuré
 */
public class Main {

    private PhotoDisplayer a_displayer;

    /**
     * @param args
     */
    public static void main(final String[] args) {
        Chrono.setBeginningOfTime();

        if (args.length != 2) {
            System.err.println("Syntax: PhotoManager <photoDirectory> <cacheDirectory>");
            System.exit(1);
        }

        new Main(args[0], args[1]);
    }

    /**
     * @param root directory where are the photo folders and the index file
     * @param cache directory where the cached images are stored
     */
    public Main(final String root,
                final String cache) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override public void run() {start(root, cache); }
        });

    }

    /**
     * @param root directory where are the photo folders and the index file
     * @param cache directory where the cached images are stored
     */
    private void start(final String root,
                       final String cache) {

        final String s_root = root;
        final Scheduler scheduler = new Scheduler();

        final File rootDir = new File(root);
        if (!rootDir.exists()) {
            System.err.println("directory \"" + root + "\" does not exist");
            return;
        }
        if (!rootDir.canRead()) {
            System.err.println("directory \"" + root + "\" is not readable");
            return;
        }

        final ConcretePhotoList a_list = new ConcretePhotoList(s_root + File.separator + "photo_ref.txt",
                                                               s_root,
                                                               cache,
                                                               scheduler);
        final FilteredPhotoList a_filteredList = new FilteredPhotoList(a_list);

        final GPSDatabase a_GPSDatabase = new GPSDatabase(s_root + File.separator + "gps.txt",
                                                          a_list.getLocationFactory());

        final PhotoListDisplay a_listDisplay = new PhotoListDisplay(a_list,
                                                                    a_filteredList,
                                                                    a_GPSDatabase,
                                                                    scheduler);
        final ListSelectionManager selection = new ListSelectionManager(a_filteredList,
                                                                        a_listDisplay.getLineSelectionListModel());

        this.a_displayer = new PhotoDisplayer(scheduler,
                                              a_filteredList,
                                              a_GPSDatabase,
                                              new SubsampledImageCachedManager(cache),
                                              selection,
                                              cache);

        final int i = a_list.getRowCount() - 1;
        a_listDisplay.getLineSelectionListModel().setSelectionInterval(i, i);

        a_listDisplay.setBounds(new Rectangle(0, 0, 1280, 300));
        a_listDisplay.setVisible(true);

        this.a_displayer.setBounds(new Rectangle(100, 300, 1000, 720));
        this.a_displayer.setVisible(true);

        Chrono.getTime(Chrono.EVENT, "end of start");
    }
}
