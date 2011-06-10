package lmzr.photomngr.ui.player;

public class PlayerFactory {

	final static private Player[] s_player = new Player[] {
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
	 * @return list of supported players
	 */
	public Player[] getPlayers()
	{
		return s_player;
	}
}
