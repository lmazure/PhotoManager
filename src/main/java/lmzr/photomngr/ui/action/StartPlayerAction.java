package lmzr.photomngr.ui.action;

import java.awt.event.ActionEvent;
import java.io.IOException;

import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

import lmzr.photomngr.data.PhotoProvider;
import lmzr.photomngr.ui.player.Player;

/**
 * Action to start a image/movie displayer
 *
 * @author Laurent Mazuré
 */
public class StartPlayerAction extends PhotoManagerAction {

    final private PhotoProvider a_photoProvider;
    final private Player a_player;

    /**
     * @param text
     * @param mnemonic
     * @param accelerator
     * @param tooltipText
     * @param photoProvider
     * @param player
     */
    public StartPlayerAction(final String text,
                             final int mnemonic,
                             final KeyStroke accelerator,
                             final String tooltipText,
                             final PhotoProvider photoProvider,
                             final Player player) {

        super(text, mnemonic, accelerator, tooltipText);

        this.a_photoProvider = photoProvider;
        this.a_player = player;
    }


    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    @Override
    public void actionPerformed(final ActionEvent e) {

        if ( this.a_player.getExecutable()==null ) {
            JOptionPane.showMessageDialog(null,
                                          "Unable to find executable of \n"+this.a_player.getName(),
                                          "Launch error",
                                          JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            final String[] commandLine = { this.a_player.getExecutable().getAbsolutePath(),
                                           this.a_photoProvider.getPhoto().getFullPath() };
            final Process p = Runtime.getRuntime().exec(commandLine);
            // the two next lines are necessary to get VLC running correctly
            p.getInputStream().close();
            p.getErrorStream().close();
        } catch (final IOException e1) {
            e1.printStackTrace();
        }
    }

    /**
     * @return return the player launched by the action
     */
    public Player getPlayer() {
        return this.a_player;
    }

}
