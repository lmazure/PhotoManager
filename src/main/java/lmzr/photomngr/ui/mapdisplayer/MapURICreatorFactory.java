package lmzr.photomngr.ui.mapdisplayer;

/**
 * @author Laurent Mazuré
 */
public class MapURICreatorFactory {

	final static MapURICreator[] s_mapDisplayers = new MapURICreator[] {
		new BingMapURICreator(),
		new GeoportailMapURICreator(),
		new GoogleMapsURICreator()
		};
	
	/**
	 * 
	 */
	public MapURICreatorFactory()
	{
	}
	
	/**
	 * @return array of available players
	 */
	public MapURICreator[] getMapDisplayers()
	{
		return s_mapDisplayers;
	}
}
