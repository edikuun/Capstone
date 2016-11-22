package ObjectGenerator;

import java.util.LinkedList;
import java.util.Random;

import GameContent.InGameState;
import GameContent.Platform;

public class NormalLevel extends Level{

	private float springChance=0.9f;
	
	public NormalLevel(InGameState state, Generator generator, Random rnd) {
		super(state, generator, rnd);
	}

	@Override
	public boolean checkEnterConditions(int score) {
		return false;
	}

	@Override
	public boolean shouldEnter(int score) {
		return false;
	}

	@Override
	public Level createGeneratorInstance(int score) {
		return new NormalLevel(state, generator, rnd);
	}

	
	@Override
	public void generate(float playerJumpHeight, int maxPlatformScore) {		
		LinkedList<Platform> platforms = state.getAllPlatforms();
		
		float nextY = platforms.getLast().getHitBounds().getY()+getHeightOfNextPlatform(playerJumpHeight, maxPlatformScore);
		
		int newPlatformScore = (int)(nextY*state.getScoreFactor());
		int numberOfPlatforms = getNumberOfPlatforms(newPlatformScore);
		
		for(int i = 0; i < numberOfPlatforms; i++){
			int x = generator.random(0, (int)(state.getGameScreenBoundings().getWidth()-generator.platformWidth));
			
			Platform p = rnd.nextFloat() > 0.333f?generator.createDirtPlatform(x, nextY):generator.createBreakingPlatform(x, nextY);
			
			for(int failCount = 0; generator.intersectsAny(state.getAllPlatforms(), p.getHitBounds()) && failCount < 100; failCount++){
				p.getHitBounds().setX(generator.random(0, (int)(state.getGameScreenBoundings().getWidth()-generator.platformWidth)));
			}
		
			/*if(rnd.nextFloat()>springChance){
				float sx = p.getHitBounds().getX() + ((p.getHitBounds().getMaxX() -generator.getSpringWidth() - p.getHitBounds().getX())*rnd.nextFloat());
				float y = p.getHitBounds().getY() + p.getHitBounds().getHeight();
				
				state.addSprite(generator.createSpring(sx, y));
				
				System.out.println("Generated spring");
			}*/
			
			state.addPlatform(p);
		}
	}

	@Override
	public boolean isBiomeFinished() {
		return false;
	}

	private int getMinPlatformInRowCount(int score){
		return 1;
	}
	
	private int getMaxPlatformInRowCount(int score){
		return 1;
	}
	
	private float getMinimalHeightDifference(float maxJmpHeight, int score){
		return maxJmpHeight*(score<100?0.2f:score<500?0.33f:score<800?0.43f:score<1000?0.53f:score<1200?0.63f:score<1500?0.73f:score<2000?0.83f:0.93f); //TODO: come up with a better solution for this
	}
	
	private float getMaximalHeightDifference(float maxJmpHeight, int score){
		return maxJmpHeight*(score<100?0.33f:score<500?0.43f:score<800?0.53f:score<1000?0.63f:score<1200?0.73f:score<1500?0.83f:score<2000?0.93f:1f); //TODO: see above
	}
	
	private int getNumberOfPlatforms(int score){
		int min = getMinPlatformInRowCount(score);
		int max = getMaxPlatformInRowCount(score);
		
		return generator.random(min, max);
	}
	
	private float getHeightOfNextPlatform(float maxJmpHeight, int score){
		int min = (int)(getMinimalHeightDifference(maxJmpHeight, score)/generator.platformThickness);
		int max = (int)(getMaximalHeightDifference(maxJmpHeight, score)/generator.platformThickness);
		
		return generator.random(min,max) * generator.platformThickness;
	}
	
	@Override
	public String toString(){
		return "normal biome";
	}
}
