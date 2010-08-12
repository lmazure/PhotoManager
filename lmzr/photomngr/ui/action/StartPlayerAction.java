package lmzr.photomngr.ui.action;

import java.awt.event.ActionEvent;
import java.io.IOException;

import javax.swing.KeyStroke;

import lmzr.photomngr.data.ListSelectionManager;
import lmzr.photomngr.data.PhotoList;
import lmzr.photomngr.ui.player.Player;

/**
 * Action to start a image/movie displayer
 */
public class StartPlayerAction extends PhotoManagerAction {

    final private PhotoList a_photoList;
    final private ListSelectionManager a_selection;
    final private Player a_player;

    /**
     * @param text
     * @param mnemonic
     * @param accelerator
     * @param tooltipText
     * @param photoList
     * @param selection
     * @param player
     */
    public StartPlayerAction(final String text,
                             final int mnemonic,
                             final KeyStroke accelerator,
                             final String tooltipText,
                             final PhotoList photoList,
                             final ListSelectionManager selection,
                             final Player player) {

        super(text, mnemonic, accelerator, tooltipText);

        a_photoList = photoList;
        a_selection = selection;
        a_player = player;
    }


    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(final ActionEvent e) {

        try {
            final String[] commandLine = { a_player.getExecutable().getAbsolutePath(),
                    a_photoList.getPhoto(a_selection.getSelection()[0]).getFullPath() };
            Runtime.getRuntime().exec(commandLine);
        } catch (final IOException e1) {
            e1.printStackTrace();
        }
    }

    /**
     * @return return the player launched by the action
     */
    public Player getPlayer() {
        return a_player;
    }

}
