package ObjectGenerator;

import java.util.Random;

import GameContent.InGameState;
import GameContent.Platform;

public abstract class Biome {
	protected InGameState state;
	protected Generator generator;
	protected Random rnd;
	
	public Biome(InGameState state, Generator generator, Random rnd){
		this.state = state;
		this.generator = generator;
		this.rnd = rnd;
	}
	
	/**
	 * This Method should check if the conditions are right to enter this biome
	 * @param score This is the score corresponding to the highest platform
	 * @return if this biome could enter
	 */
	public abstract boolean checkEnterConditions(int score);
	/**
	 * This Method should check if this biome should be entered
	 * This could for example mean something like
	 * return checkEnterConditions() && rnd.nextBoolean()
	 * in order to add some randomization to the biome choosing.
	 * Note that the biomes list in the generator will always be shuffeled before choosing a new biome.
	 * @param score This is the score corresponding to the highest platform
	 * @return true if this biome should be entered, otherwise false
	 */
	public abstract boolean shouldEnter(int score);
	/**
	 * This Method is supposed to act a bit like a constructor, meaning that it's meant to return a new instance of this generator
	 * @return a new instance
	 */
	public abstract Biome createGeneratorInstance(int score);
	
	/**
	 * This method gets called by the generator in order to generate a (or multiple) platforms.
	 * Generated Platforms can be directly added to the InGameState state avaiable for every Biome
	 * @param playerJumpHeight This is the player jump height in pixels. Notice that it is the difference and not the absolute y position of the player.
	 * @param maxPlatformHeight This is the score corresponding to the height of the highest platform
	 */
	public abstract void generate(float playerJumpHeight, int maxPlatformScore);
	/**
	 * The generator calls this method to check if a new biome has to be chosen
	 * @return true if a new biome can be chosen, otherwise false
	 */
	public abstract boolean isBiomeFinished();
}
