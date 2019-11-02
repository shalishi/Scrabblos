package thread;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.util.ArrayList;

import org.json.JSONException;

import net.i2p.crypto.eddsa.EdDSAPublicKey;
import scrabblos.ED25519;
import scrabblos.Letter;
import scrabblos.LetterPool;
import scrabblos.Utils;
import scrabblos.Word;

public class Politician implements Runnable {

	private static String pk; // public key
	private static KeyPair kp;// signature
	private Word wordAct;
	private ArrayList<String> wordDes;
	private String hash = "";
	ArrayList<String> dictionary;
	final int MILI_PER_SEC = 1000;
	@Override
	public void run() {
		try {
			dictionary = Utils.makeDictionnary("src/dict_dict_100000_1_10.txt");
			CreateKey();
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			String head = Utils.bytesToHex(digest.digest((hash).getBytes()));
			wordAct = new Word(new ArrayList<Letter>(),head,"","");
			System.out.println("Thread Politician" + Thread.currentThread().getId()+ pk + " is running");
			

			long startTime = System.currentTimeMillis();
			while ((System.currentTimeMillis()-startTime)< 2*1000*MILI_PER_SEC){
				Word word = makeWord();
				if (word != null) {
					if(!inWordPool(word)) {
						if(inDictionary(word)) {
							updateWordPool(word);
						}
					}
				}
				//System.out.println("Thread Politician" + Thread.currentThread().getId()+ pk + " is running");
			}
			showWordPool();
			

		} catch (Exception e) {
			// Throwing an exception
			e.printStackTrace();
			System.out.println("Exception is caught : " + e.toString() + e.getCause().getMessage());
		}

	}
	
	private boolean inWordPool(Word word) {
		synchronized (MotorA.getMotorA()) {
			MotorA motor = MotorA.getMotorA();
			ArrayList<Word> wordpool = motor.getWord_pool().getWords();
			for(Word w:wordpool) {
				if(w.getPoliticien() == word.getPoliticien() && w.getWord().size() == word.getWord().size()) {
					return true;
				}
			}
			return false;
		}
	}
	
	
	private boolean inDictionary(Word word) {
		boolean flag = true;
		for(String s : wordDes) {
			for(int i=0;i<word.getWord().size()-1;i++) {
				if(s.length()<=i)continue;
				if(word.getWord().get(i).getLetter().charAt(0) != s.charAt(i)) {
					flag = false;
					break;
				}
			}
			if(flag) return true;
		}
		return false;
	}
	
	private boolean isValid(Letter l) {
		for(Letter letterAct : this.wordAct.getWord()) {
			if(letterAct.getAuthor() == l.getAuthor()) {
				return true;
			}
		}
      return true;
	}

	private void CreateKey() {
		ED25519 ed;
		try {
			ed = new ED25519();
			kp = ed.generateKeys();
			EdDSAPublicKey public_k = (EdDSAPublicKey) kp.getPublic();
			pk = Utils.bytesToHex(public_k.getAbyte());
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchProviderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	protected LetterPool readLetterPool() {
		//System.out.println("Thread Politician is reading letter pool*********************************************");
		synchronized (MotorA.getMotorA()) {

			MotorA motor = MotorA.getMotorA();
			//motor.showLetterPool();
			//System.out.println("Thread Politician is finished of reading letter pool**********************************");
			return motor.getLetter_pool();
		}
	}

	public ArrayList<String> findWordDestinaire(ArrayList<Letter> letters) {
		//System.out.println(" po find   ----> wordAct = " + wordAct.getWord());
		ArrayList<String> dest = new ArrayList<String>();
		for (String s : dictionary) {
			int i = 0;
			//System.out.print(s);
			if (s.length() <= letters.size()) {
				//System.out.println(" --> too short!!!");
				continue;
			} else {
				for (Letter letter : letters) {
					if (letter.getLetter().charAt(0) == s.charAt(i)) {
						i++;
						continue;
					} else {
						//System.out.println(" --> not match!!!");
						break;
					}
				}
				if (i == letters.size())
					dest.add(s);
			}
		}
		return dest;
	}

	public void showWordDes() {
			System.out.println("show WORD DES+++++++++++++++++++++++++++++++++++++++++++++++++++++");
			if (wordDes.size() > 0) {
				for (String s : wordDes) {
					System.out.println(s);
				}
			}
			System.out.println("finish show  WORD DES+++++++++++++++++++++++++++++++++++++++++++++++"); 
	}
	
	protected Word makeWord()
			throws IOException, JSONException, InvalidKeyException, NoSuchAlgorithmException, SignatureException {
		// dans cette class nous devons ecrire l'algo de creation d'un mot
		LetterPool letterPool = readLetterPool();
		ArrayList<Letter> lp =letterPool.getLetters();
		if(lp.isEmpty()) return null;
	
		ArrayList<Letter> word = new ArrayList<Letter>();
		boolean flag = false;		
		if (wordAct.getWord().size() == 0) {
			Letter l = lp.get((int) (Math.random() * (lp.size() - 1)));
			word.add(l);
			wordDes = findWordDestinaire(word);
			//showWordDes();
			flag = true;
		} else {			
			word = (ArrayList<Letter>) this.wordAct.getWord().clone();
			int i = word.size();
			for (String d : wordDes) {
				for (Letter l : lp) {
					if(d.length()<=i)continue;
					if (l.getLetter().charAt(0) == d.charAt(i)) {
						if(isValid(l)) {
							word.add(l);
							wordDes = findWordDestinaire(word);
							flag = true;
							break;
						}
					}
				}
				if (flag)break;
			}
		}
		
		
		
		if (flag) {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			String signture = Utils.bytesToHex(Utils.signature2Poli(word, digest.digest((hash).getBytes()), kp));
			String head = Utils.bytesToHex(digest.digest((hash).getBytes()));
			this.wordAct.setWord(word);
			this.wordAct.setPeriod(letterPool.getCurrent_period());
			this.wordAct.setSignature(signture);
			this.wordAct.setHash(head);
			this.wordAct.setPoliticien(pk);
			return this.wordAct;
		} else {
			return null;
		}
		
	}

	protected void updateWordPool(Word w) {
		System.out.println("update word pool");
		synchronized (MotorA.getMotorA()) {
			MotorA motor = MotorA.getMotorA();
			motor.addWord(w);
			motor.showWordPool();
		}

	}
	
	protected void showWordPool() {
		System.out.println("show word pool");
		synchronized (MotorA.getMotorA()) {
			MotorA motor = MotorA.getMotorA();
			motor.showWordPool();
		}

	}
	
	private Word getLongest() {
		synchronized (MotorA.getMotorA()) {
			MotorA motor = MotorA.getMotorA();
			ArrayList<Word> wordpool =  motor.getWord_pool().getWords();
			Word word = null;
			int max = 0;
			for(Word w: wordpool) {
				if(w.getWord().size()>max) {
					word = w;
					max = w.getWord().size();
				}
			}
			return word;
		}
	}
	
	

}