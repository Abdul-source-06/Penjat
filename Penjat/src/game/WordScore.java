package game;

import java.io.Serializable;

public class WordScore implements Serializable{

	private String word;
    private int points;

    public WordScore(String word, int points) {
        this.setWord(word);
        this.setPoints(points);
    }

	public String getWord() {
		return word;
	}

	public void setWord(String word) {
		this.word = word;
	}

	public int getPoints() {
		return points;
	}

	public void setPoints(int points) {
		this.points = points;
	}
	
	@Override
    public String toString() {
        return "Paraula: " + word + "Punts: " + points;
    }
	  
    
	
}
