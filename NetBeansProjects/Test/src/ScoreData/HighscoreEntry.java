package ScoreData;

import java.io.Serializable;

public class HighscoreEntry implements Comparable<HighscoreEntry>, Serializable{

	private static final long serialVersionUID = 1L;
	private int score;
	private String name;
	
	public HighscoreEntry(String name, int score){
		this.name = name;
		this.score = score;
	}
	
	@Override
	public int compareTo(HighscoreEntry o) {
		return o.getScore() - score;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	@Override
	public String toString(){
		return name +":   " + score;
	}
}
