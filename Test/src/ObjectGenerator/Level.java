package ObjectGenerator;

import java.util.Random;

import GameContent.InGameState;
import GameContent.Platform;

public abstract class Level {
	protected InGameState state;
	protected Generator generator;
	protected Random rnd;
	
	public Level(InGameState state, Generator generator, Random rnd){
		this.state = state;
		this.generator = generator;
		this.rnd = rnd;
	}
	
	
	public abstract boolean checkEnterConditions(int score);
	
	public abstract boolean shouldEnter(int score);
	
	public abstract Level createGeneratorInstance(int score);
	
	public abstract void generate(float playerJumpHeight, int maxPlatformScore);
	
	public abstract boolean isBiomeFinished();
}
