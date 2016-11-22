package GameContent;


import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.state.StateBasedGame;

public abstract class Sprite {
	protected Image sprite;
	protected Rectangle hitBounds;
	protected InGameState gameState;
	protected StateBasedGame game;
	
	public Sprite(Image img, Rectangle bounds, InGameState state, StateBasedGame stateGame){
		sprite = img;
		hitBounds = bounds;
		gameState = state;
		game = stateGame;
	}
	
	public void draw(Graphics g){
		Rectangle realBounds = gameState.calcRenderRect(hitBounds);
		sprite.draw(realBounds.getX(), realBounds.getY(), gameState.getTextureScaling());
	}
	
	public abstract void update();
	
	public abstract void onPlayerHit(Player p);
	
	public boolean canBeRemoved(){
		return hitBounds.getY() - gameState.getCameraHeight() < -100f;
	}
	
	public Rectangle getBounds(){
		return hitBounds;
	}
}
