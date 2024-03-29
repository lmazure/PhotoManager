package lmzr.photomngr.ui.player;

/**
 * @author Laurent Mazuré
 */
public class PlayerFactory {

    final static private Player[] s_player = {
        new Player_VideoLAN(),
        new Player_WindowsMediaPlayer(),
        new Player_QuickTime()
        };

    /**
     *
     */
    public PlayerFactory()
    {
    }

    /**
     * @return array of supported players
     */
    public static Player[] getPlayers()
    {
        return s_player;
    }
}
