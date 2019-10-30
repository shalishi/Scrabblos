package scrabblos;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONException;

import net.i2p.crypto.eddsa.EdDSAPublicKey;

public class Main {

	private static Socket socket;
	private static final String HOST = "localhost";
	private static final int PORT = 12345;
	private static int periode = 10;// loneur de periode;

	public static void main(String args[]) throws IOException, InvalidKeyException, NoSuchAlgorithmException,
			SignatureException, JSONException, NoSuchProviderException {

		InetAddress address = InetAddress.getByName(HOST);
		socket = new Socket(address, PORT);
		OutputStream os = socket.getOutputStream();
		OutputStreamWriter osw = new OutputStreamWriter(os);
		BufferedWriter bw = new BufferedWriter(osw);
		 
		Client c = new Client();
		ArrayList<String> LetterBag = c.register(socket);
		c.inject_Letter(socket, LetterBag, "", bw);
		System.out.println("inject_Letter : ");
		c.inject_Letter(socket,LetterBag,"",bw);
		System.out.println("inject_Letter : " );
		c.inject_Letter(socket,LetterBag,"",bw);
        
		LetterPool lp = CommonOperations.get_full_letterpool(socket, bw);
		System.out.println("LetterPool : " +lp.getLetters());
		ArrayList<String> dictionaire = Utils.makeDictionnary("src/dict_dict_100000_1_10.txt");
		
		// CREATION DE LA CLE PUBLIQUE
		ED25519 ed = new ED25519();
		KeyPair kp = ed.generateKeys();
		EdDSAPublicKey public_k = (EdDSAPublicKey) kp.getPublic();
		String pk = Utils.bytesToHex(public_k.getAbyte());
		
		
		Politicien po1 = new Politicien(socket, pk, kp);
		po1.register(socket);
		LetterPool lp2 = CommonOperations.get_full_letterpool(socket, bw);
	
		
//		System.out.println("dictionaire : " + dictionaire);
		ArrayList<Letter> letters = new ArrayList<Letter>();
		ArrayList<Letter> word = new ArrayList<Letter>();
		String sig = po1.getSign();
		Word wordAct = new Word(word, "", pk, sig);
		letters.addAll(lp.getLetters());
		Letter l = letters.get((int) (Math.random() * (letters.size() - 1)));
		System.out.println("wordAct = " + l.getLetter());
		word.add(l);
		String wordDest = po1.findWordDestinaire(dictionaire, wordAct);
		wordAct = po1.make_word(lp, wordDest, wordAct);
		if(po1.isWord(dictionaire, wordAct)) {
			po1.inject_word(socket,wordAct);
		}
//		WordPool wordpool = CommonOperations.get_full_wordpool(socket,bw);
//		DiffWordPool dwordpool = CommonOperations.get_wordpool_since(socket,bw,0);
//		ArrayList<Word> words= wordpool.getWords();
//		//<Word> dwords= dwordpool.getWordpool().getWords();
//		for(Word w :words) {
//			System.out.println("get word: "+w.wordArray());
//		}

	}

}
