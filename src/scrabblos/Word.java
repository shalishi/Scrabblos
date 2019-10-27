package scrabblos;

import java.util.ArrayList;

public class Word {
	private ArrayList<Letter> word;
	private String hash;
	private String politicien;
	private String signature;
	
	public Word(ArrayList<Letter> word, String hash, String politicien, String signature) {
		super();
		this.word = word;
		this.hash = hash;
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
		return hash;
	}
	public void setHash(String hash) {
		this.hash = hash;
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
	
	
	
}
