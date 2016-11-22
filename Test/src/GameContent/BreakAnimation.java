package GameContent;

import java.util.HashMap;

import org.newdawn.slick.Animation;
import org.newdawn.slick.Image;
import org.newdawn.slick.SpriteSheet;

import ImageLoaders.SpritesheetLoader;

public class BreakAnimation {
	public final static String BASE_BREAKINGPLATFORM="baseAnimation_breakingPlatform";
	
	private HashMap<String, Animation> animations;
	
	private BreakAnimation(){init();}
	private static BreakAnimation instance;
	public static BreakAnimation getInstance(){
		if(instance==null) instance = new BreakAnimation();
		return instance;
	}
	
	private void init(){
		animations = new HashMap<String,Animation>();
		
		//Load breaking platform animation
		SpriteSheet break1 = SpritesheetLoader.getInstance().getSpriteSheet("break_1", 64, 64);
                SpriteSheet break2 = SpritesheetLoader.getInstance().getSpriteSheet("break_2", 64, 64);
                SpriteSheet break3 = SpritesheetLoader.getInstance().getSpriteSheet("break_3", 64, 64);
		Image[] frames = new Image[]{break1.getSprite(0, 0).getSubImage(0, 0, 64, 64),
				break2.getSprite(0, 0).getSubImage(0, 0, 64, 32),
				break3.getSprite(0, 0).getSubImage(0, 0, 64, 32)};
		Animation a = new Animation(frames, 200);
		a.setLooping(false);
		animations.put(BASE_BREAKINGPLATFORM, a);
	}
	
	public Animation getAnimation(String name){
		return animations.get(name).copy();
	}
	
	public void addAnimation(Animation a, String n){
		animations.put(n, a);
	}
}
