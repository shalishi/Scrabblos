package thread;

import java.util.ArrayList;

import scrabblos.Word;

public class Block {	
	private ArrayList<Word> block;
	private String politicien;
	
	public Block(ArrayList<Word> block, String politicien) {
		super();
		this.block = block;
//		this.head = head;
		this.politicien = politicien;
//		this.signature = signature;
	}
	
	public ArrayList<Word> getBlock() {
		return block;
	}
	public void setBlock(ArrayList<Word> block) {
		this.block = block;
	}
//	public String getHead() {
//		return head;
//	}
//	public void setHead(String head) {
//		this.head = head;
//	}
	public String getPoliticien() {
		return politicien;
	}
	public void setPoliticien(String politicien) {
		this.politicien = politicien;
	}
//	public String getSignature() {
//		return signature;
//	}
//	public void setSignature(String signature) {
//		this.signature = signature;
//	}
//	


}
