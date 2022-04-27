package lmzr.photomngr.ui.player;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import lmzr.photomngr.data.DataFormat;

/**
 * @author Laurent Mazuré
 */
public abstract class Player {

    final private String a_name;
    final private File a_path;
    final private Set<DataFormat> a_supportedFormats;

    /**
     * @param name name of the Player
     * @param possiblePaths possible path of the executable
     * @param supportedFormats formats supported by the Player
     */
    protected Player(final String name,
                     final String[] possiblePaths,
                     final DataFormat[] supportedFormats) {

        this.a_name = name;

        this.a_supportedFormats = new HashSet<>(Arrays.asList(supportedFormats));

        for ( String p : possiblePaths) {
            final File f = new File(p);
            if ( f.canExecute() ) {
                this.a_path = f;
                return;
            }
        }

        this.a_path = null;
    }

    /**
     * @return name of the player
     */
    public String getName() {
        return this.a_name;
    }

     /**
     * @return executable file
     */
    public File getExecutable() {
        return this.a_path;
    }

    /**
     * @param format
     * @return true is the format is supported, false otherwise
     */
    public boolean isFormatSupported(DataFormat format) {
        return this.a_supportedFormats.contains(format);
    }
}
