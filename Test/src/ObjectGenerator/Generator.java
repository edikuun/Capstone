package ObjectGenerator;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.newdawn.slick.Image;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.geom.Rectangle;

import GameContent.BreakingPlatform;
import GameContent.InGameState;
import GameContent.MovingPlatform;
import GameContent.Platform;
import GameContent.Player;
import ImageLoaders.SpritesheetLoader;

public class Generator {
	private InGameState gameState;
	private static final float generationBorder=500f;
	protected float platformThickness=12;
	protected float platformWidth=64;
	
	private int possiblePlatformsInRow;
	private List<Level> biomes;
	private Level currentBiome, baseBiome;
	private Random rnd;
	
	protected static Image solidPlatform, breakingPlatform, movingPlatform; //platforms

	public Generator(InGameState state){
		gameState = state;
		platformThickness = 12 * state.getTextureScaling();
		platformWidth = 48 * state.getTextureScaling();
		
		possiblePlatformsInRow = (int)(state.getGameScreenBoundings().getWidth()/platformWidth);
		
		SpriteSheet sheet = SpritesheetLoader.getInstance().getSpriteSheet("misc", 64, 64);
                SpriteSheet breakPlat = SpritesheetLoader.getInstance().getSpriteSheet("break", 62, 15);
                SpriteSheet movePlat = SpritesheetLoader.getInstance().getSpriteSheet("move", 59, 19);
                SpriteSheet normPlat = SpritesheetLoader.getInstance().getSpriteSheet("normal", 62, 15);
		solidPlatform = normPlat.getSprite(0, 0).getSubImage(0, 0, 62, 15);
		breakingPlatform = breakPlat.getSprite(0,0).getSubImage(0, 0, 62, 15);
		movingPlatform = movePlat.getSprite(0, 0).getSubImage(0, 0, 59, 19);
		
	
		
		rnd = new Random();
		
		baseBiome = new NormalLevel(state, this, rnd);
		biomes = new LinkedList<Level>();
		
		biomes.add(new BreakableLevel(gameState, this, rnd));
		biomes.add(new MovingLevel(state, this, rnd));
	}
	
	public Generator(long seed, InGameState state){
		this(state);
		rnd = new Random(seed);
	}
	
	public void update(){
		LinkedList<Platform> platforms = gameState.getAllPlatforms();
		Player player = gameState.getPlayer();
		float maximalJumpHeight = 0.5f * player.getMaxSpeed() * player.getMaxSpeed() / player.getGravitation();
		while(Math.abs(platforms.getLast().getHitBounds().getY() - player.getBounds().getY())<generationBorder){
			
			int maxPlatformScore = (int)(platforms.getLast().getHitBounds().getY()*gameState.getScoreFactor());
			
			if(currentBiome == null || currentBiome instanceof NormalLevel){
				Collections.shuffle(biomes);
				for(Level b : biomes){
					if(b.shouldEnter(maxPlatformScore)){
						currentBiome = b.createGeneratorInstance(maxPlatformScore);
						System.out.println("Entered " + currentBiome.toString());
						break;
					}
				}
				if(currentBiome == null){
					currentBiome = baseBiome.createGeneratorInstance(maxPlatformScore);
				}
			}
			
			
			
			currentBiome.generate(maximalJumpHeight, maxPlatformScore);
			
			if(currentBiome.isBiomeFinished()){
				currentBiome = null;
			}
		}
	}
	
	protected boolean intersectsAny(LinkedList<Platform> all, Rectangle toCheck){
		for(Platform p : all){
			if(p.getHitBounds().intersects(toCheck)){
				return true;
			}
		}
		return false;
	}
	
	
	
	/**
	 * Can be used to generate a number in the intervall [min,max]
	 * @param min the inclusive minimal number
	 * @param max the inclusive maximal number
	 * @return a randum number from min to max
	 */
	public int random(int min, int max){
		return min + rnd.nextInt(max - min + 1);
	}
	
	protected Platform createDirtPlatform(float x, float y){
		Rectangle platformBoundings = new Rectangle(x,y,platformWidth,platformThickness);
		return new Platform(platformBoundings, solidPlatform, gameState);
	}
	
	protected Platform createBreakingPlatform(float x, float y){
		Rectangle platformBoundings = new Rectangle(x, y, 0, 0);
		platformBoundings.setWidth(platformWidth + 4 * gameState.getTextureScaling());
		platformBoundings.setX(platformBoundings.getX() - 2 * gameState.getTextureScaling());
		platformBoundings.setHeight(platformThickness - 2*gameState.getTextureScaling());
		platformBoundings.setY(platformBoundings.getY() + gameState.getTextureScaling());
		return new BreakingPlatform(platformBoundings,breakingPlatform,gameState);
	}
	
	protected Platform createMovingPlatform(float x, float y, float minX, float maxX, float speed){
		Rectangle platformBoundings = new Rectangle(x, y, 0, 0);
		platformBoundings.setWidth(movingPlatform.getWidth() * gameState.getTextureScaling());
		platformBoundings.setHeight(movingPlatform.getHeight() * gameState.getTextureScaling());
		
		return new MovingPlatform(platformBoundings, movingPlatform, gameState, minX, maxX, speed);
	}
	
	
}
