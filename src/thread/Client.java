
package thread;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.util.ArrayList;
import java.util.Collections;
import net.i2p.crypto.eddsa.EdDSAPublicKey;
import scrabblos.ED25519;
import scrabblos.Letter;
import scrabblos.LetterPool;
import scrabblos.Utils;
import scrabblos.Word;

public class Client implements Runnable {
	private volatile String pk;
	private volatile KeyPair kp;
	private volatile ArrayList<String> LetterBag = new ArrayList<String>();
	private volatile String hash = "" + Math.random() * 100;
	private volatile int current_period = -1;//start at 0 round
	private char[] bags = { 'b', 'd', 'u', 'q', 's', 'y', 'o', 'r', 'r', 'p', 'm', 'e', 'p', 'y', 's', 'l', 't', 'h',
			'u', 'i', 'n', 'p', 'w', 't', 'w', 'a', 'e', 's', 'r', 'c', 'y', 'c', 'u', 'j', 'x', 't', 'i', 'o', 'k',
			'k', 'c', 'c', 'l', 'w', 'y', 'c', 'w', 'o', 'y', 'x', 'g', 'c', 'u', 'y', 'g', 's', 's', 'c', 'q', 'q',
			'a', 'x', 'd', 'm', 'j', 'e', 'l', 'f', 'f', 'g', 'k', 'x', 'p', 'm', 'j', 'x', 'a', 'y', 'g', 'p', 'd',
			'g', 'g', 'i', 'j', 'o', 'g', 'w', 'r', 'a', 'd', 'b', 'p', 'm', 'o', 'e', 'p', 'v', 't', 'h', 'a', 'm',
			'v', 'm', 'f', 'f', 'e', 's', 'v', 'r', 'o', 'v', 'h', 'o', 'v', 'q', 'a', 'm', 'c', 'b', 'e', 'q', 'g',
			'd', 'p', 'd', 'x', 'q', 'k', 'f', 'p', 'k', 'f', 'a', 's', 'k', 'c', 'x', 'q', 'h', 'i', 'r', 'w', 's',
			's', 'r', 'r', 'q', 'u', 'g', 's', 'n', 'x', 'f', 't', 'q', 'a', 'v', 'r', 'p', 't', 'n', 'h', 'p', 's',
			'j', 'w', 'p', 'y', 'x', 'b', 't', 'v', 'v', 'g', 'a', 'y', 'q', 'm', 'k', 'm', 'x', 'c', 't', 'u', 'n',
			'g', 'e', 'o', 'w', 'o', 'l', 'l', 'f', 'o', 'd', 'l', 'b', 'p', 'x' };

	@Override
	public void run() {
		try {
			// Displaying the thread that is running
			System.out.println("Thread Client" + Thread.currentThread().getId() + " is running");
			creatKey();
			rigister(pk);
			for (char c : bags) {
				LetterBag.add(c + "");
			}
			while (getCurrentPeriod() <= MotorA.MAX_ROUND) {
				//System.out.println("client current_period ="+current_period+" current_period = "+getCurrentPeriod());
				if (current_period<getCurrentPeriod()) {
					//System.out.println("Thread Client" + Thread.currentThread().getId() + " throw letter in round "+ getCurrentPeriod());
					updateLetterPool();
					//updateState(pk, true);
					current_period++;
				}
			}

		} catch (Exception e) {
			// Throwing an exception
			System.out.println("Exception is caught");
		}

	}

	
	private void rigister(String public_key) {
		synchronized (MotorA.getMotorA()) {
			MotorA motor = MotorA.getMotorA();
			motor.registerClient(public_key);
		}
	}
	
	private void updateState(String public_key,boolean state) {
		synchronized (MotorA.getMotorA()) {
			MotorA motor = MotorA.getMotorA();
			motor.updateClientState(public_key,state);
		}
	}
	
	private boolean getState(String public_key) {
		synchronized (MotorA.getMotorA()) {
			MotorA motor = MotorA.getMotorA();
			return motor.getClients_states().get(public_key);
		}
	}
	
	protected int getPeriod() {
		synchronized (MotorA.getMotorA()) {
			MotorA motor = MotorA.getMotorA();
			LetterPool letterpool = motor.getLetter_pool();
			int periode = 0;
			for (Letter letter : letterpool.getLetters()) {
				if (letter.getPeriod() > periode)
					periode = letter.getPeriod();
			}
			return periode;
		}
	}

	private String getWordSignature() {
		ArrayList<Word> wordPool = readLastWordPool();
		
		if (wordPool.size() > 0) {
			int maxSize = 0;
			int maxSizeIndex = 0;
			for (Word w : wordPool) {
				String str = w.getHash();
				int size = getPreSize(str);
				//System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! pre size"+size);
				size += w.getWord().size();
				if (size > maxSize) {
					maxSize = size;
					maxSizeIndex = wordPool.indexOf(w);
				}
			}
			return wordPool.get(maxSizeIndex).getSignature();
		}else {
			MessageDigest digest;
			try {
				digest = MessageDigest.getInstance("SHA-256");
				return Utils.bytesToHex(digest.digest(("").getBytes()));
			} catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return "";
	}

	private int getPreSize(String hash) {
		ArrayList<Word> wordPool = readWordPool();
		MessageDigest digest;
		try {
			for (Word w : wordPool) {
				digest = MessageDigest.getInstance("SHA-256");
				String sighash = w.getSignature();//Utils.bytesToHex(digest.digest((w.getSignature()).getBytes()));
				if (sighash == hash) {
					int size = w.getWord().size();
					if (w.getPeriod() > 0) {
						hash = w.getHash();
						//System.out.println("size  = " + size);
						return size + getPreSize(hash);
					}
					if (w.getPeriod() == 0) {
						//System.out.println("size  = " + size);
						return size;
					}
				}
			}
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;

	}

	private ArrayList<Word> readWordPool() {
		/*System.out.println("Thread Client " + Thread.currentThread().getId()
				+ " is reading word pool*********************************************");
				*/
		synchronized (MotorA.getMotorA()) {
			MotorA motor = MotorA.getMotorA();
			return motor.getWord_pool().getWords();
		}
	}

	private ArrayList<Word> readCurrentWordPool() {
		/*System.out.println("Thread Client " + Thread.currentThread().getId()
				+ " is reading current word pool*********************************************");*/
		synchronized (MotorA.getMotorA()) {

			MotorA motor = MotorA.getMotorA();
			return motor.getCurrentWord_pool();
		}
	}

	private ArrayList<Word> readLastWordPool() {
		/*System.out.println("Thread Client " + Thread.currentThread().getId()
				+ " is reading last word pool*********************************************");*/
		synchronized (MotorA.getMotorA()) {

			MotorA motor = MotorA.getMotorA();
			return motor.getLastWord_pool();
		}
	}

	private void creatKey() {
		// CREATION DE LA CLE PUBLIQUE
		ED25519 ed;
		try {
			ed = new ED25519();
			kp = ed.generateKeys();
			EdDSAPublicKey public_k = (EdDSAPublicKey) kp.getPublic();
			pk = Utils.bytesToHex(public_k.getAbyte());
		} catch (NoSuchAlgorithmException | NoSuchProviderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private Letter chooseLetter(ArrayList<String> LetterBag, String hash) {
		MessageDigest digest;
		try {
			digest = MessageDigest.getInstance("SHA-256");
			Collections.shuffle(LetterBag);
			String letter = LetterBag.get(0);
			String signature = Utils.bytesToHex(Utils.signature2(letter, digest.digest((hash).getBytes()), 0, kp));
			String head = hash;//Utils.bytesToHex(digest.digest((hash).getBytes()));
			//int period = getLetterPool().getCurrent_period(); 
			return new Letter(letter, getLetterPool().getCurrent_period(), head, pk, signature);
		} catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	protected void updateLetterPool() {

		Letter l = chooseLetter(LetterBag, getWordSignature());
		synchronized (MotorA.getMotorA()) {
			MotorA motor = MotorA.getMotorA();
			ArrayList<Letter> letterpool = motor.getLetter_pool().getLetters();
			boolean add = true;
			for(Letter letter :letterpool) {
				if(letter.getPeriod() == l.getPeriod() && letter.getAuthor() == l.getAuthor()) {
					add = false;
					System.out.println(l.getPeriod()+" ," +l.getLetter()+" :have added in letter pool as :"+letter.getLetter());
					break;
				}
			}
			if(add) motor.addLetter(l);

			motor.showLetterPool();
		}
	}

	protected LetterPool getLetterPool() {

		synchronized (MotorA.getMotorA()) {
			MotorA motor = MotorA.getMotorA();
			return motor.getLetter_pool();
		}

	}

	protected int getCurrentPeriod() {

		synchronized (MotorA.getMotorA()) {
			MotorA motor = MotorA.getMotorA();
			return motor.getCurrentPeriod();
		}

	}

	

}
