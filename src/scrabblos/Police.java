package scrabblos;

import java.util.ArrayList;

public class Police {
	
	public static ArrayList<String> dictionnary;
	private ArrayList<LettreDisp> word_current;
	
	private boolean filterLetter(LettreDisp letter_disp) {
		for(LettreDisp letter : word_current) {
			if(letter.getId_client()==letter_disp.getId_client()) {
				return false;
			}
		}
		return true;
	}
	private void makeWord(LettreDisp lettre_disp) {
		String word="";
		for(LettreDisp p :word_current) {
			word+=p.getLetter();
		}
		
		
	}
	
	public static void main() {
		dictionnary = new ArrayList<String> ();
		dictionnary.add("bonjour");
		dictionnary.add("bon");
		
		LettreDisp letter1 = new LettreDisp('c',5);
		LettreDisp letter2 = new LettreDisp('c',2);
		LettreDisp letter3 = new LettreDisp('o',1);
		LettreDisp letter4 = new LettreDisp('z',1);
		ArrayList<LettreDisp> lettres_disp = new ArrayList<LettreDisp>();
		lettres_disp.add(letter1);
		lettres_disp.add(letter2);
		lettres_disp.add(letter3);
		lettres_disp.add(letter4);
		
	}
	
}
