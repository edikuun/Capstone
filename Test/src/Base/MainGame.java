package Base;

import java.io.File;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.AppletGameContainer.Container;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.GameState;
import org.newdawn.slick.state.StateBasedGame;

import Debug.DebugInfo;
import GameContent.InGameState;
import GamePause.PauseGameState;
import GameOver.GameOverState_Counter;
import GameOver.GameOverState_Input;
import ScoreData.HighscoreState;
import ImageLoaders.ImageLoader;

public class MainGame extends StateBasedGame{
	
	private static final int xRes=640;
	private static final float yScale=4f/3f;
	
	private GameState gameState, menuState, gameOverState_Counter, gameOverState_Input, highScoreState, pauseState;
	private DebugInfo info;
	
	public static final String DATA_BASEDIR="Data";
	
	public MainGame(String name) {
		super(name);
		
		this.ensureExistanceOfDirectories(DATA_BASEDIR);
	}

	private void ensureExistanceOfDirectories(String... dirs){
		for(String s : dirs){
			File f = new File(s);
			f.mkdirs();
		}
	}


	@Override
	public void initStatesList(GameContainer container) throws SlickException {
		info = new DebugInfo(1, 20, 1, 1,this, container);
		try {
			info.set(0,0,new Object[]{"FPS: ", new Object[]{GameContainer.class.getMethod("getFPS"),container}});
		} catch (NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
		
		ImageLoader.getInstance().setImageBaseDir("Images");
		
		menuState = new MainMenu();
		addState(menuState);
		gameState = new InGameState();
		addState(gameState);
		gameOverState_Counter = new GameOverState_Counter();
		addState(gameOverState_Counter);
		gameOverState_Input = new GameOverState_Input();
		addState(gameOverState_Input);
		highScoreState = new HighscoreState();
		addState(highScoreState);
		pauseState = new PauseGameState();
		addState(pauseState);
	}
	
	@Override
	protected void postRenderState(GameContainer container, Graphics g) throws SlickException{
		info.render(g);
	}
	
	public static void main(String[] args) throws SlickException{
		AppGameContainer agc = new AppGameContainer(new MainGame("Test Project"));
		
		int yRes = agc.getScreenHeight() - 100;
		int xRes =(int)( yRes / yScale);
		
		agc.setDisplayMode(xRes, yRes, false);
		
		System.out.println("Running at " + agc.getWidth() + "x" + agc.getHeight()); //runs at 501x668
		agc.setShowFPS(false);
		
		agc.start();
	}
	
	public GameState getIngameState(){
		return gameState;
	}
	
	public DebugInfo getDebugInfo(){
		return info;
	}
}
