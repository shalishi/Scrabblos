package scrabblos;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Signature;
import java.security.SignatureException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import net.i2p.crypto.eddsa.EdDSAEngine;
import net.i2p.crypto.eddsa.EdDSAPublicKey;
import net.i2p.crypto.eddsa.spec.EdDSANamedCurveTable;
import net.i2p.crypto.eddsa.spec.EdDSAParameterSpec;

public class Politicien {

	private static Socket socket;
	private static final String HOST = "localhost";
	private static final int PORT = 12345;
	private static int periode = 10;//loneur de periode;
	private static String pk;
	private static KeyPair kp;

	public static void register(Socket socket) throws IOException, JSONException, NoSuchAlgorithmException, NoSuchProviderException {

		OutputStream os = socket.getOutputStream();
		OutputStreamWriter osw = new OutputStreamWriter(os);
		BufferedWriter bw = new BufferedWriter(osw);
		JSONObject json = new JSONObject();
		
		//CREATION DE LA CLE PUBLIQUE
		ED25519 ed = new ED25519();
		kp = ed.generateKeys();
		EdDSAPublicKey public_k = (EdDSAPublicKey) kp.getPublic();
		pk = Utils.bytesToHex(public_k.getAbyte());
		
		//ENVOIE DU MESSAGE
		json.put("register",pk);
		byte[] a = Utils.intToBigEndian(json.toString().length());
		for (int i = a.length - 1; i >= 0; i--) {
			bw.write((char) (a[i]));
		}
		bw.write(json.toString());
		bw.flush();
		
	}

	public static Word make_word(DiffLetterPool dffl,String motDes,Word wordAct) throws IOException, JSONException, InvalidKeyException, NoSuchAlgorithmException, SignatureException {
		//dans cette class nous devons ecrire l'algo de creation d'un mot
		MessageDigest digest = MessageDigest.getInstance("SHA-256");
		String signture =  Utils.bytesToHex(Utils.signature2("a",digest.digest(("").getBytes()), 0, kp));
	    String hash = "";
		
		LetterPool lettepool =  dffl.getLetterpool();
		ArrayList<Letter> letters = lettepool.getLetters();
		//Word word = wordAct.getWord();
		ArrayList<Letter> wordletters = (ArrayList<Letter>) wordAct.getWord().clone();
		int i = wordletters.size();
		for(Letter l:letters) {
			if(l.getLetter().charAt(0) == motDes.charAt(i)) {
				wordletters.add(l);		
			}
		}
		wordAct.setWord(wordletters);
	    wordAct.setSignature(signture);
	    wordAct.setHash(hash);
	    wordAct.setPoliticien(pk);
       return wordAct;
	}

	public static void inject_word(Socket s, Word w) throws IOException, JSONException, NoSuchAlgorithmException, InvalidKeyException, SignatureException {
		MessageDigest digest = MessageDigest.getInstance("SHA-256");
		OutputStream os = socket.getOutputStream();
		OutputStreamWriter osw = new OutputStreamWriter(os);
		BufferedWriter bw = new BufferedWriter(osw);
		JSONObject json = new JSONObject();
		
		
		json.put("word",w.wordArray());
		json.put("head", Utils.bytesToHex(digest.digest(("").getBytes())));
		json.put("politicien", pk);
		json.put("signature",Utils.bytesToHex(Utils.signature2Poli(w,digest.digest(("").getBytes()), kp)));
		
		JSONObject json2 = new JSONObject();
		json2.put("inject_word",json );
		
		byte[] a = Utils.intToBigEndian(json.toString().length());
		
		for (int i = a.length - 1; i >= 0; i--) {
			System.out.println("what" + (char) (a[i]));
			bw.write((char) (a[i]));
		}
		bw.write(json.toString());
		bw.flush();
	}
	
	public static String findWordDestinaire(ArrayList<String> dictionaire,Word wordAct) {
		ArrayList<Letter> letters = wordAct.getWord();
		for(String s: dictionaire) {
			int i = 0;
			if(s.length()<=letters.size()) {
				continue;
			}else {
				//there,we can chose the longest or the first accord or random--i chose first accord
				for(Letter letter : letters) {
					if(letter.getLetter().charAt(0) != s.charAt(i)) {
						break;
					}
				}
				return s;
			}
		}
		return null;	
	}

	public static void main(String args[]) {
		try {
			InetAddress address = InetAddress.getByName(HOST);
			MessageDigest digest = MessageDigest.getInstance("SHA-256");

			// System.err.print(address);
			socket = new Socket(address, PORT);
			OutputStream os = socket.getOutputStream();
			OutputStreamWriter osw = new OutputStreamWriter(os);
			BufferedWriter bw = new BufferedWriter(osw);
			// Send the message to the server
			// generatePublicKey();
			register(socket);
			System.out.println("OUI JE RENTRE");
			/*CommonOperations.get_letterpool_since(socket, bw, 0);
			// System.out.println(LetterBag);
			// inject_Letter(socket,LetterBag,bw);
			// inject_Letter(socket,LetterBag);
			// continous_listen(socket,bw);
			// get_full_letterpool(socket,bw);
			
			 ArrayList<Letter> first =  CommonOperations.get_full_letterpool(socket,bw);
			    int length =  first.size();
			    Letter letter  = first.get((int) (Math.random()*length));
			    char lettre = letter.getLetter().charAt(0);
			 ArrayList<String> dictionairy = Utils.makeDictionnary("src/dict_dict_100000_1_10.txt");
			    //pour meme lettre de commence, il y a des mots different a choisir
			    ArrayList<String> choix = new  ArrayList<String>();
			    for(String s : dictionairy) {
			    	if(s.charAt(0) == lettre) {
			    		choix.add(s);
			    	}
			    }
			    int clength =  choix.size();
			    String motDes  = choix.get((int) (Math.random()*clength));//choisir le mot destinaire
			
			
			int i=0;
			ArrayList<Letter> letters = new ArrayList<Letter>();
			String hash ="";
			String signture =  Utils.bytesToHex(Utils.signature2("a",digest.digest(("").getBytes()), 0, kp));
			Word word = new Word(letters,hash,pk,signture);
			while(i<10) {//10 tour
				ArrayList<Letter> letterpool = CommonOperations.get_letterpool_since(socket,bw,periode*i); 
				LetterPool newletterpool = new LetterPool(periode*i,periode*(i+1),letters);
				DiffLetterPool diff = new DiffLetterPool(periode*i,newletterpool);
				Word makeword = make_word(diff,motDes,word);
				ArrayList<Word> Words = CommonOperations.get_wordpool_since(socket,bw,periode*i);
				for(int k = 0 ;k<Words.size();k++) {
					if(Words.get(k).getWord().size()>= makeword.getWord().size()) {
						word = Words.get(k);
					}
				}
				motDes =  findWordDestinaire(dictionairy,word);
				i++;
			}*/
			

		} catch (IOException exception) {
			exception.printStackTrace();
			System.out.println("exception : " + exception.getMessage());
		} catch (Exception exception) {
			exception.printStackTrace();
			System.out.println("exception : " + exception.getMessage());
		} finally {
			// Closing the socket
			try {
				socket.close();
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("e : " + e.getMessage());
			}
		}
	}

}
