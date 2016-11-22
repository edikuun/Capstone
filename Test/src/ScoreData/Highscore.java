package ScoreData;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import Base.MainGame;

public class Highscore implements Serializable{
	private static final long serialVersionUID = 1L;
	public static final String standardFileName=MainGame.DATA_BASEDIR + "\\scores";
	private LinkedList<HighscoreEntry> entries = new LinkedList<HighscoreEntry>();
	private int maximalNumberOfEntries=10;
	
	public int add(String name, int score){
		return add(name,score,true);
	}
	
	public int add(String name, int score, boolean autoSave){
		HighscoreEntry newEntry = new HighscoreEntry(name, score);
		entries.add(newEntry);
		Collections.sort(entries);
		while(entries.size() > maximalNumberOfEntries) entries.removeLast();
		
		if(autoSave){
			save(standardFileName);
		}
		return entries.indexOf(newEntry);
	}
	
	public int getMaximalNumberOfEntries() {
		return maximalNumberOfEntries;
	}
	public void setMaximalNumberOfEntries(int maximalNumberOfEntries) {
		this.maximalNumberOfEntries = maximalNumberOfEntries;
	}
	public List<HighscoreEntry> getEntries() {
		return entries;
	}
	public int getNumberOfEntries(){
		return entries.size();
	}
	public void save(String fileName){
		try {
			OutputStream stream = new FileOutputStream(new File(fileName));
			ObjectOutputStream objStream = new ObjectOutputStream(stream);
			objStream.writeObject(this);
			objStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public static Highscore load(String fileName){
		Highscore score = null;
		try {
			InputStream stream = new FileInputStream(new File(fileName));
			ObjectInputStream objStream = new ObjectInputStream(stream);
			score = (Highscore)objStream.readObject();
			objStream.close();
			stream.close();
		} catch (IOException | ClassNotFoundException e) {
			new Highscore().save(standardFileName);
			score = loadAR(standardFileName);
		}
		return score;
	}
	private static Highscore loadAR(String fileName){
		Highscore score = null;
		try {
			InputStream stream = new FileInputStream(new File(fileName));
			ObjectInputStream objStream = new ObjectInputStream(stream);
			score = (Highscore)objStream.readObject();
			objStream.close();
			stream.close();
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		return score;
	}
}
