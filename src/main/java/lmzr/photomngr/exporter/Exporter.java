package lmzr.photomngr.exporter;

import java.awt.Component;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.Random;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import lmzr.photomngr.data.Photo;
import lmzr.photomngr.data.PhotoList;

/**
 * Trivial export for putting the images on my web site (I know, this is not a good idea to write HTML by end
 * but this is so simple, I don't want to use DOM).
 * A random token is added to avoid strange to guess the directory name.
 *
 * @author Laurent Mazuré
 */
public class Exporter {

    final private Component a_parent;

    /**
     * @param parent
     */
    public Exporter(final Component parent) {
        a_parent = parent;
    }

    /**
     * @param photoList
     */
    public void export(final PhotoList photoList) {
        final Random rand = new Random();
        BufferedWriter index = null;
        final JFileChooser fc = new JFileChooser((File)null);
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fc.showSaveDialog(a_parent);
        final File f = fc.getSelectedFile();
        if ( f == null ) {
            return;
        }
        final boolean success = f.mkdir();
        if (!success) {
            System.err.println("failed to create directory "+f.getPath());
            JOptionPane.showMessageDialog(a_parent,
                                          "failed to create directory "+f.getPath(),
                                          "Copy error",
                                          JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            index = new BufferedWriter(new FileWriter(f.getPath()+File.separator+"index"+String.format("%06d", Integer.valueOf(rand.nextInt(1000000)))+".html"));
            index.write("<HTML><BODY>");
        } catch (final IOException e1) {
            System.err.println("failed to export subjects");
            e1.printStackTrace();
            JOptionPane.showMessageDialog(a_parent,
                                          "Failed to open index file\n"+e1.toString(),
                                          "Save error",
                                          JOptionPane.ERROR_MESSAGE);
        }
        int total = 1;
        for (int i=0; i<photoList.getRowCount(); i++) {
            final Photo photo = photoList.getPhoto(i);
            for (int j=0; j<((Integer)photoList.getValueAt(i, PhotoList.PARAM_COPIES)).intValue(); j++) {
                try {
                    // Create channel on the source
                    final FileChannel srcChannel = new FileInputStream(photo.getFullPath()).getChannel();

                    // Create channel on the destination
                    final String fn1 = photo.getFilename();
                    final int k = fn1.lastIndexOf(".");
                    final String fn2 = fn1.substring(0,k)
                                       +"-"
                                       +(j+1)
                                       +"-"
                                       +String.format("%04d", Integer.valueOf(total))
                                       +"-"
                                       +String.format("%06d", Integer.valueOf(rand.nextInt(1000000)))
                                       +fn1.substring(k);
                    final FileChannel dstChannel = new FileOutputStream(f.getPath()+File.separator+fn2).getChannel();

                    // Copy file contents from source to destination
                    dstChannel.transferFrom(srcChannel, 0, srcChannel.size());

                    // Close the channels
                    srcChannel.close();
                    dstChannel.close();

                    // update index
                    index.write("<A HREF='"+fn2+"'>photo "+String.format("%d", Integer.valueOf(total))+"</A><BR/>");

                    // update increment
                    total++;

                } catch (final IOException e1) {
                    System.err.println("failed to copy file");
                    e1.printStackTrace();
                    JOptionPane.showMessageDialog(a_parent,
                                                  "Failed to copy file "+photo.getFilename()+"\n"+e1.toString(),
                                                  "Copy error",
                                                  JOptionPane.ERROR_MESSAGE);
                }
            }
        }
        try {
            index.write("</BODY></HTML>");
            index.close();
        } catch (final IOException e1) {
            System.err.println("failed to export subjects");
            e1.printStackTrace();
            JOptionPane.showMessageDialog(a_parent,
                                          "Failed to close index file\n"+e1.toString(),
                                          "Save error",
                                          JOptionPane.ERROR_MESSAGE);
        }
    }
}
