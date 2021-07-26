package solitiare;

import graphics.GameBoardDrawer;
import graphics.PileDrawer;
import graphics.IPileDrawer;
import graphics.IGameBoardDrawer;
/**
 * Organizes the game configuration for Solitaire and allows access to instances of IPileDrawer and IGameBoardDrawer.
 * @author Liam Orndorff
 *
 */
public class Configuration {
	private IPileDrawer pileDrawer;
	private IGameBoardDrawer gameBoardDrawer;
	
	private static Configuration config;
	
	/**
	 * creates a new Configuration objects which initializes the fields using calls to
	 * PileDrawer.getInstances() and GameBoardDrawer.getInstance().
	 */
	public Configuration(){
		pileDrawer = PileDrawer.getInstance();
		gameBoardDrawer = GameBoardDrawer.getInstance();
	}
	
	/**
	 * gets an instance of the Configuration class.
	 * @return config instance of the Configuration class.
	 */
	public static Configuration getInstance() {
		if(config ==null) {
			config = new Configuration();
		}
		return config;
	}
	
	/**
	 * gets the PileDrawer instance.
	 * @return pileDrawer PileDrawer instance.
	 */
	public IPileDrawer getPileDrawer() {
		return pileDrawer;
	}
	
	/**
	 * gets the GameBoardDrawer instance.
	 * @return gameBoardDrawer GameBoardDrawer instance.
	 */
	public IGameBoardDrawer getGameBoardDrawer() {
		return gameBoardDrawer;
	}
}
