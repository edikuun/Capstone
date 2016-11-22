package ObjectGenerator;

import java.util.Random;

import GameContent.InGameState;
import GameContent.Platform;

public class MovingLevel extends Level{

	private int platformCounter;
	private float minHeightDifferenceFac=0.6f, maxHeightDifferenceFac=0.9f, minSpeed=2, maxSpeed = 5;
	private static float chance=0.9f;
	private static int cooldown;
	
	public MovingLevel(InGameState state, Generator generator,
			Random rnd) {
		super(state, generator, rnd);
	}
	
	public MovingLevel(InGameState state, Generator generator,
			Random rnd, int platforms) {
		super(state, generator, rnd);
		platformCounter = platforms;
	}

	@Override
	public boolean checkEnterConditions(int score) {
		return score > 10;
	}

	@Override
	public boolean shouldEnter(int score) {
		if(checkEnterConditions(score)) cooldown--;
		return checkEnterConditions(score) && rnd.nextFloat() > chance && cooldown <= 0;
	}

	@Override
	public Level createGeneratorInstance(int score) {
		cooldown = 17;
		return new MovingLevel(state, generator, rnd, generator.random(20, score>1000?30:25));
	}

	@Override
	public void generate(float playerJumpHeight, int maxPlatformScore) {
		float nextHeight = (minHeightDifferenceFac + ((maxHeightDifferenceFac-minHeightDifferenceFac)*rnd.nextFloat()))*playerJumpHeight;
		float nextY = state.getAllPlatforms().getLast().getHitBounds().getY() + nextHeight;
		float nextX = generator.random(0, (int)(state.getGameScreenBoundings().getWidth()-generator.platformWidth));
		
		Platform p = generator.createMovingPlatform(nextX, nextY, 0, state.getGameScreenBoundings().getWidth() - 64 * state.getTextureScaling(), 
				minSpeed + ((maxSpeed - minSpeed)*rnd.nextFloat()));
		state.addPlatform(p);
		platformCounter--;
	}

	@Override
	public boolean isBiomeFinished() {
		return platformCounter <= 0;
	}

	@Override
	public String toString(){
		return "Moving platforms biome with " + platformCounter + " platforms left";
	}
}
