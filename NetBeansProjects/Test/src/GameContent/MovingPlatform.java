package GameContent;

import org.newdawn.slick.Image;
import org.newdawn.slick.geom.Rectangle;

public class MovingPlatform extends Platform{

	float minX, maxX;
	float speed;
	
	public MovingPlatform(Rectangle hitBounds, Image sprite,
			InGameState gameState, float minX, float maxX, float speed) {
		super(hitBounds, sprite, gameState);
		this.minX = minX;
		this.maxX = maxX;
		this.speed = speed;
	}

	@Override
	public void update() {
		super.update();
		
		checkBorder();
		
		this.hitBounds.setX(hitBounds.getX() + speed);
	}
	
	private void checkBorder(){
		if(this.hitBounds.getX() < minX){
			speed = Math.abs(speed);
		}
		else if (this.hitBounds.getX() > maxX){
			speed = -Math.abs(speed);
		}
	}
	
	@Override
	public boolean movePlayerWithPlatform() {
		return true;
	}
}
