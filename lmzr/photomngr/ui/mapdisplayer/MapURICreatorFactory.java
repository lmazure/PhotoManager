package lmzr.photomngr.ui.mapdisplayer;

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
	
	public MapURICreator[] getMapDisplayers()
	{
		return s_mapDisplayers;
	}
}
