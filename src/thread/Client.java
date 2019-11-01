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
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.json.JSONException;
import org.json.JSONObject;

import net.i2p.crypto.eddsa.EdDSAPublicKey;
import scrabblos.ED25519;
import scrabblos.Letter;
import scrabblos.Utils;

public class Client implements Runnable {
	private static String pk;
	private static KeyPair kp;
	private ArrayList<String> LetterBag = new ArrayList<String>();
	private String hash = "";

	@Override
	public void run() {
		try {
			// Displaying the thread that is running
			System.out.println("Thread Client" + Thread.currentThread().getId() + " is running");
			creatKey();
			// LetterBag = new ArrayList<String>();
			LetterBag.add("a");
			LetterBag.add("b");
			LetterBag.add("c");
			LetterBag.add("d");
			LetterBag.add("e");

			updateLetterPool();
			/*
			 * while(true) { TimeUnit.SECONDS.sleep(10); updateLetterPool(); }
			 */
		} catch (Exception e) {
			// Throwing an exception
			System.out.println("Exception is caught");
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

	private Letter choose_Letter(ArrayList<String> LetterBag, String hash) {
		MessageDigest digest;
		try {
			digest = MessageDigest.getInstance("SHA-256");
			Collections.shuffle(LetterBag);
			String letter = LetterBag.get(0);
			String signature = Utils.bytesToHex(Utils.signature2(letter, digest.digest((hash).getBytes()), 0, kp));
			String head = Utils.bytesToHex(digest.digest((hash).getBytes()));
			long period = 0;
			return new Letter(letter, period, head, pk, signature);
		} catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	protected void updateLetterPool() {
		try {
			System.out.println("update letter pool");
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			Letter l = choose_Letter(LetterBag, hash);
			JSONObject json = new JSONObject();
			json.put("letter", l.getLetter());
			json.put("author", l.getAuthor());
			json.put("signature", l.getSignature());
			json.put("head", l.getHash());
			json.put("period", l.getPeriod());

			JSONObject json2 = new JSONObject();
			json2.put("inject_letter", json);

			String j = "{ \"inject_letter\": { \"letter\":\"a\", \"period\":0, \"head\":\"e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855\", \"author\":\"b7b597e0d64accdb6d8271328c75ad301c29829619f4865d31cc0c550046a08f\", \"signature\":\"8b6547447108e11c0092c95e460d70f367bc137d5f89c626642e1e5f2ce\" }}";

			synchronized (MotorA.getMotorA()) {
				MotorA motor = MotorA.getMotorA();
				motor.addLetter(json2.toString());
				motor.showLetterPool();
			}
			// showLetterPool();
		} catch (NoSuchAlgorithmException | JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
