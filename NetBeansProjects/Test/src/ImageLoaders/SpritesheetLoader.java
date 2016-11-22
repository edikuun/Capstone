package ImageLoaders;

import java.util.HashMap;

import org.newdawn.slick.Image;
import org.newdawn.slick.SpriteSheet;

public class SpritesheetLoader {
	//Singleton
	private static SpritesheetLoader instance;
	private SpritesheetLoader(){
		
	}
	public static SpritesheetLoader getInstance(){
		if(instance == null) instance = new SpritesheetLoader();
		return instance;
	}
	
	private HashMap<String,SpriteSheet> sheets = new HashMap<String,SpriteSheet>();
	
	public SpriteSheet getSpriteSheet(String name,int tileWidth, int tileHeight){
		if(sheets.containsKey(name)){
			return sheets.get(name);
		}
		
		String fullName = "spritesheet_"+name;
		Image img = ImageLoader.getInstance().getImage(fullName);
		
		if(!ImageLoader.getInstance().exists(fullName)){
			Image def = ImageLoader.getInstance().getDefaultImage();
			Image scaled = def.getScaledCopy(tileWidth, tileHeight);
			return new SpriteSheet(scaled, tileWidth, tileHeight);
		}
		
		SpriteSheet sheet = new SpriteSheet(img, tileWidth, tileHeight);
		sheets.put(name, sheet);
		return sheet;
	}
}
