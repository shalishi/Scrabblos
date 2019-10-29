package scrabblos;

public class Letter {
	private String letter;
	private long period;
	private String head;
	private String author;
	private String signature;
	
	public Letter(String letter, long period, String hash, String author, String signature) {
		super();
		this.letter = letter;
		this.period = period;
		this.head = hash;
		this.author = author;
		this.signature = signature;
	}
	
	public String getLetter() {
		return letter;
	}
	public void setLetter(String letter) {
		this.letter = letter;
	}
	public long getPeriod() {
		return period;
	}
	public void setPeriod(int period) {
		this.period = period;
	}
	public String getHash() {
		return head;
	}
	public void setHash(String hash) {
		this.head = hash;
	}
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public String getSignature() {
		return signature;
	}
	public void setSignature(String signature) {
		this.signature = signature;
	}
	
	public String toString2() {
		return "{ \"letter\" : " + letter + "\"	period \" :" + period + "\" head\" :"+ head + "\"author\" :"+ author +"\"signature\" :" + signature+"}"; 
	}
	
	public String toString() {
		return "{ \"letter\" : \"" + letter + "\",\"period\" : \"" + period + "\",\"head\" :\""+ head + "\",\"author\" :\""+ author + "\",\"signature\" :\"" + signature+"\"}"; 
	}
	
}
