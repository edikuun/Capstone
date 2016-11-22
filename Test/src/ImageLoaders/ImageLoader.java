package ImageLoaders;

import java.io.File;
import java.util.HashMap;

import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

public class ImageLoader {
	//Singleton
	private static ImageLoader instance;
	private ImageLoader(){
		try {
			defaultImage = new Image("Images\\default.jpg");
		} catch (SlickException e) {
			System.out.println("Warning: No default image found, creating empty picture");
			try {
				defaultImage = new Image(1,1);
			} catch (SlickException e1) {
				System.out.println("Failed to create default picture");
				e.printStackTrace();
			}
		}
	}
	public static ImageLoader getInstance(){
		if(instance==null) instance = new ImageLoader();
		return instance;
	}
	
	private HashMap<String,Image> images = new HashMap<String,Image>();
	private Image defaultImage;
	
	private String imageBaseDir ="";
	private String textureBaseDir="";
	
	private static String[] extensions = {"png","jpg","gif","jpeg"};
	
	public Image getImage(String name){
		if(images.containsKey(name)){
			return images.get(name);
		}
		
		String basePath = imageBaseDir + "\\" + name;
		String ext = "";
		for(String cext : extensions){
			ext = cext;
			if(new File(basePath + "." + cext).exists()) break;
			ext = "";
		}
		if(!ext.isEmpty()){
			Image img;
			try {
				img = new Image(basePath + "." + ext);
				img.setFilter(Image.FILTER_NEAREST);
				images.put(name,img);
				return img;
			} catch (SlickException e) {
				e.printStackTrace();
			}

		} else {
			System.out.println("WARNING: Could not get image ressource. (NAME: " + name + "; SUBDIR: " + imageBaseDir );
		}
		return defaultImage;
	}
	
	public void loadImages(String[] names){
		for(String s : names) getImage(s);
	}
	public String getImageBaseDir() {
		return imageBaseDir;
	}
	public void setImageBaseDir(String imageBaseDir) {
		this.imageBaseDir = imageBaseDir;
	}
	public String getTextureBaseDir() {
		return textureBaseDir;
	}
	public void setTextureBaseDir(String textureBaseDir) {
		this.textureBaseDir = textureBaseDir;
	}
	public Image getDefaultImage() {
		return defaultImage;
	}
	public void setDefaultImage(Image defaultImage) {
		this.defaultImage = defaultImage;
	}
	public boolean exists(String name){
		getImage(name);
		return images.containsKey(name);
	}
	
	/**
	 * Release all resources used by Images
	 * WARNING: This will eventually destroy Images used by other objects, e.g. SpriteSheets
	 */
	public void cleanAll(){
		images.clear();
	}
}
