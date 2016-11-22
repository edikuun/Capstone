package GamePause;

import org.newdawn.slick.Color;
import org.newdawn.slick.Font;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.GameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.state.transition.Transition;

import GameContent.InGameState;
import GameOver.GameOverState_Counter;
import Transitions.FixedAlphaFadingTransition;

public class PauseGameState extends BasicGameState{
	
	public static int fadeTime = 900, KEY_TO_PAUSEUNPAUSE=Input.KEY_P;
	public static final int ID=5;
	
	private Color textColor=Color.white;
	private Font font;
	private GameState ingameState;
	private StateBasedGame game;
	
	@Override
	public void init(GameContainer container, StateBasedGame game)
			throws SlickException {
		font = new TrueTypeFont(new java.awt.Font("verdana", java.awt.Font.BOLD, 20), true);
		ingameState = game.getState(InGameState.ID);
		this.game = game;
	}

	private static String[] text = new String[]{"- PAUSE -", "Game has been Paused"};
	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics g)
			throws SlickException {
		ingameState.render(container, game, g);
		g.setColor(GameOverState_Counter.COLOR_OVERLAY);
		g.fillRect(0, 0, container.getWidth(), container.getHeight());
		
		float ybase = (container.getHeight()-font.getLineHeight()*text.length)/2;
		for(int i = 0; i < text.length; i++){
			float x = (container.getWidth()-font.getWidth(text[i]))/2;
			font.drawString(x, ybase + i * font.getLineHeight(), text[i], textColor);
		}
	}

	@Override
	public void update(GameContainer container, StateBasedGame game, int delta)
			throws SlickException {
	}

	@Override
	public int getID() {
		return ID;
	}

	@Override
	public void keyPressed(int key, char c) {
		if(key == KEY_TO_PAUSEUNPAUSE){
			game.enterState(ingameState.getID(), null, new FixedAlphaFadingTransition(Color.black, GameOverState_Counter.COLOR_OVERLAY.a, 0, fadeTime));
		}
	}
	
	public static Transition createLeaveOtherStateTransition(){
		return new FixedAlphaFadingTransition(GameOverState_Counter.COLOR_OVERLAY, fadeTime);
	}
}
