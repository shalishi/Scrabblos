package scrabblos;

import java.util.ArrayList;

public class Word {
	private ArrayList<Letter> word;
	private String head;
	private String politicien;
	private String signature;
	
	public Word(ArrayList<Letter> word, String hash, String politicien, String signature) {
		super();
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
	
	
	
}
