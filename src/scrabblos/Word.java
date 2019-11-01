package scrabblos;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Word {
	private ArrayList<Letter> word;
	private String head;
	private String politicien;
	private String signature;
	
	public Word(ArrayList<Letter> word, String hash, String politicien, String signature) {
		this.word = word;
		this.head = hash;
		this.politicien = politicien;
		this.signature = signature;
	}
	
	public ArrayList<Letter> getWord() {
		return word;
	}
	public void setWord(ArrayList<Letter> word) {
		this.word = word;
	}
	public String getHash() {
		return head;
	}
	public void setHash(String hash) {
		this.head = hash;
	}
	public String getPoliticien() {
		return politicien;
	}
	public void setPoliticien(String politicien) {
		this.politicien = politicien;
	}
	public String getSignature() {
		return signature;
	}
	public void setSignature(String signature) {
		this.signature = signature;
	}
	
	public String wordArray() throws JSONException {
		String wordArray ="";
		for(Letter l : word) {
			wordArray += l.toString();
		}
		return wordArray;
	}
	
	public String toString() {
		return "word : "+ this.word+" ,head : "+head+" ,politicien : "+this.politicien + " ,signature : "+this.signature;
				
	}
	
	
	
}
