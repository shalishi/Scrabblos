package scrabblos;

public class LettreDisp {
	char letter;
	int id_client;
	
    LettreDisp(char letter, int id_client) {
		this.letter=letter;
		this.id_client = id_client;
	}
    LettreDisp() {
		
	}
	public char getLetter() {
		return letter;
	}
	public void setLetter(char letter) {
		this.letter = letter;
	}
	
	public int getId_client() {
		return id_client;
	}
	public void setId_client(int id_client) {
		this.id_client = id_client;
	}
	
}
