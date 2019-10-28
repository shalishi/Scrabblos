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
import net.i2p.crypto.eddsa.spec.EdDSANamedCurveTable;
import net.i2p.crypto.eddsa.spec.EdDSAParameterSpec;

public class Politicien {

	private static Socket socket;
	private static final String HOST = "localhost";
	private static final int PORT = 12345;
	private static int periode = 10;//loneur de periode;
	private static String pk;
	private static KeyPair kp;
	
	
	private static String bytesToHex(byte[] hashInBytes) {

        StringBuilder sb = new StringBuilder();
        for (byte b : hashInBytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();

    }

	public static ArrayList<String> makeDictionnary(String fileName) {
		
		String file = "src/dict_dict_100000_1_10.txt";
		try {
			String line = null;
			FileReader fileReader = new FileReader(fileName);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			ArrayList<String> strings = new ArrayList<String>();

			while ((line = bufferedReader.readLine()) != null) {
				strings.add(line);
			}
			return strings;

		} catch (Exception e) {
			return null;
		}
	}

	public static void register(Socket socket) throws IOException, JSONException {

		OutputStream os = socket.getOutputStream();
		OutputStreamWriter osw = new OutputStreamWriter(os);
		BufferedWriter bw = new BufferedWriter(osw);

		JSONObject json = new JSONObject();
		json.put("register", "b7b597e0d64accdb6d8271328c75ad301c29829619f4865d31cc0c550046a08f");
		System.out.println(json.toString());
		byte[] a = intToBigEndian(json.toString().length());
		for (int i = a.length - 1; i >= 0; i--) {
			bw.write((char) (a[i]));
		}
		bw.write(json.toString());
		bw.flush();
		int c;
	}

	public static void continous_listen(Socket socket, BufferedWriter bw) throws JSONException, IOException {

		OutputStream os = socket.getOutputStream();
		OutputStreamWriter osw = new OutputStreamWriter(os);
		// BufferedWriter bw = new BufferedWriter(osw);

		byte[] a = intToBigEndian("{\"listen\":null}".length());
		for (int i = a.length - 1; i >= 0; i--) {
			bw.write((char) (a[i]));
		}
		System.out.println("{\"listen\" : null}");
		bw.write("{\"listen\":null}");
		bw.flush();
	}

	public static void stop_listen(Socket socket) throws JSONException, IOException {

		OutputStream os = socket.getOutputStream();
		OutputStreamWriter osw = new OutputStreamWriter(os);
		BufferedWriter bw = new BufferedWriter(osw);

		byte[] a = intToBigEndian("{ \"stop_listen\" : null }".length());
		for (int i = a.length - 1; i >= 0; i--) {
			bw.write((char) (a[i]));
		}
		System.out.println("{ \"stop_listen\" : null }");
		bw.write("{ \"stop_listen\" : null }");
		bw.flush();
	}

	public static ArrayList<Letter> get_full_letterpool(Socket socket, BufferedWriter bw)
			throws JSONException, IOException {

		byte[] a = intToBigEndian("{ \"get_full_letterpool\": null}".length());
		for (int i = a.length - 1; i >= 0; i--) {
			bw.write((char) (a[i]));
		}
		System.out.println("{ \"listen\" : null }");
		bw.write("{ \"get_full_letterpool\": null}");
		bw.flush();

		// RECUPERATION DE LA REPONSE SOUS FORME DE STRING
		int c;
		InputStream is = socket.getInputStream();
		InputStreamReader isr = new InputStreamReader(is);
		BufferedReader br = new BufferedReader(isr);
		String res = "";
		Boolean debutRep = false;
		while (br.ready()) {
			c = br.read();

			if (debutRep) {
				if (c >= 34 && c <= 127) {
					res = res + (char) c;
					System.out.println("char : " + (char) c);
				}
			}
			if ((char) c == '^') {
				debutRep = true;
			}
			// System.out.println("Message received from the server : " + (char)c );
		}
		// TRANSFORMATION DE LA REPONSE SOUS FORME DE LISTE DE LETTER
		System.out.println("je suis res" + res);
		Gson gson = new Gson();
		JSONObject j = new JSONObject(res);
		System.out.println(j.toString());
		JSONObject j2 = (JSONObject) j.get("diff_letterpool");
		System.out.println(j2);
		JSONObject letterpool = (JSONObject) j2.get("letterpool");
		System.out.println(letterpool);
		JSONArray letters = letterpool.getJSONArray("letters");
		System.out.println(letters.toString());
		ArrayList<Letter> lettersA = new ArrayList<Letter>();
		for (int i = 0; i < letters.length(); i++) {
			lettersA.add(gson.fromJson(letters.getJSONObject(i).toString(), Letter.class));
		}

		return lettersA;
	}

	public static ArrayList<Letter> get_letterpool_since(Socket socket, BufferedWriter bw, int period)
			throws JSONException, IOException {

		// ENVOIE DE LA REQUETE
		byte[] a = intToBigEndian(("{ \"get_letterpool_since\":" + period + "}").length());
		for (int i = a.length - 1; i >= 0; i--) {
			bw.write((char) (a[i]));
		}
		System.out.println("{ \"listen\" : null }");
		bw.write("{ \"get_letterpool_since\":" + period + "}");
		bw.flush();

		// RECUPERATION DE LA REPONSE SOUS FORME DE STRING
		int c;
		InputStream is = socket.getInputStream();
		InputStreamReader isr = new InputStreamReader(is);
		BufferedReader br = new BufferedReader(isr);
		String res = "";
		Boolean debutRep = false;
		while (br.ready()) {
			c = br.read();

			if (debutRep) {
				if (c >= 34 && c <= 127) {
					res = res + (char) c;
					System.out.println("char : " + (char) c);
				}
			}
			if ((char) c == '^') {
				debutRep = true;
			}
			// System.out.println("Message received from the server : " + (char)c );
		}
		// TRANSFORMATION DE LA REPONSE SOUS FORME DE LISTE DE LETTER
		System.out.println("je suis res" + res);
		Gson gson = new Gson();
		JSONObject j = new JSONObject(res);
		System.out.println(j.toString());
		JSONObject j2 = (JSONObject) j.get("diff_letterpool");
		System.out.println(j2);
		JSONObject letterpool = (JSONObject) j2.get("letterpool");
		System.out.println(letterpool);
		JSONArray letters = letterpool.getJSONArray("letters");
		System.out.println(letters.toString());
		ArrayList<Letter> lettersA = new ArrayList<Letter>();
		for (int i = 0; i < letters.length(); i++) {
			lettersA.add(gson.fromJson(letters.getJSONObject(i).toString(), Letter.class));
		}

		return lettersA;
	}

	public static ArrayList<Word> get_full_wordpool(Socket socket, BufferedWriter bw) throws JSONException, IOException {

		byte[] a = intToBigEndian("{ \"get_full_wordpool\": null}".length());
		for (int i = a.length - 1; i >= 0; i--) {
			bw.write((char) (a[i]));
		}
		System.out.println("{ \"listen\" : null }");
		bw.write("{ \"get_full_wordpool\": null}");
		bw.flush();
		

		ArrayList<Word> WordA = new ArrayList<Word>();
		
		return WordA;

	}

	public static ArrayList<Word> get_wordpool_since(Socket socket, BufferedWriter bw, int period)
			throws JSONException, IOException {

		byte[] a = intToBigEndian(("{ \"get_wordpool_since\":" + period + "null}").length());
		for (int i = a.length - 1; i >= 0; i--) {
			bw.write((char) (a[i]));
		}
		System.out.println("{ \"listen\" : null }");
		bw.write("{ \"get_wordpool_since\":" + period + "}");
		bw.flush();
		
		ArrayList<Word> WordA = new ArrayList<Word>();
		
		return WordA;

	}

	public static byte[] signature(String lettre,int p,String PK) throws NoSuchAlgorithmException {
		//Faut rajouter une condition comme quoi si le wordpool est vide on passe ici
		MessageDigest digest = MessageDigest.getInstance("SHA-256");
		byte[] hash = digest.digest("".getBytes());
		byte[] letter =  lettre.getBytes();
		byte[] period = intToBigEndian(p);
		byte[] publicK = PK.getBytes();
		byte[] f = concaten(publicK,concaten(period,concaten(hash, letter)));
		byte[] hashf = digest.digest(f);
		
		return f;
	}
	
	public static byte[] concaten(byte[] a, byte[] b) {
		byte[] res = new byte[a.length+b.length];
		for(int i=0 ; i < a.length; i++) {
			res[i] = a[i];
		}
		for(int i=a.length ; i < res.length; i++) {
			res[i] = b[i-a.length];
		}
		return res;
	}
	
	public static Word make_word(DiffLetterPool dffl,String motDes,Word wordAct) throws IOException, JSONException, InvalidKeyException, NoSuchAlgorithmException, SignatureException {
		//dans cette class nous devons ecrire l'algo de creation d'un mot
	    String signture =  bytesToHex(ED25519.sign(kp, signature("a", 0, pk)));
	    String hash = "";
		
		LetterPool lettepool =  dffl.getLetterpool();
		ArrayList<Letter> letters = lettepool.getLetters();
		//Word word = wordAct.getWord();
		ArrayList<Letter> wordletters = (ArrayList<Letter>) wordAct.getWord().clone();
		int i = wordletters.size();
		for(Letter l:letters) {
			if(l.getLetter() == motDes.charAt(i)) {
				wordletters.add(l);		
			}
		}
		wordAct.setWord(wordletters);
	    wordAct.setSignature(signture);
	    wordAct.setHash(hash);
	    wordAct.setPoliticien(pk);
       return wordAct;
	}

	public static void inject_word(Socket s, String word) throws IOException, JSONException {
		OutputStream os = socket.getOutputStream();
		OutputStreamWriter osw = new OutputStreamWriter(os);
		BufferedWriter bw = new BufferedWriter(osw);
		JSONObject json = new JSONObject();
		json.put("inject_word", word);
		byte[] a = intToBigEndian(json.toString().length());
		System.out.println(a);
		// byte [] a =intToBigEndian(("{ \"Inject_letter\" : "+ letter+ " }").length());
		for (int i = a.length - 1; i >= 0; i--) {
			System.out.println("what" + (char) (a[i]));
			bw.write((char) (a[i]));
		}
		// System.out.println("{ \"Inject_letter\" : " + letter + " }");
		bw.write(json.toString());
		// bw.write("{ \"Inject_letter\" : " + letter +" }");
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
					if(letter.getLetter() != s.charAt(i)) {
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

			// System.err.print(address);
			socket = new Socket(address, PORT);
			OutputStream os = socket.getOutputStream();
			OutputStreamWriter osw = new OutputStreamWriter(os);
			BufferedWriter bw = new BufferedWriter(osw);
			// Send the message to the server
			// generatePublicKey();
			register(socket);
			System.out.println("OUI JE RENTRE");
			get_letterpool_since(socket, bw, 0);
			// System.out.println(LetterBag);
			// inject_Letter(socket,LetterBag,bw);
			// inject_Letter(socket,LetterBag);
			// continous_listen(socket,bw);
			// get_full_letterpool(socket,bw);
			
			 ArrayList<Letter> first =  get_full_letterpool(socket,bw);
			    int length =  first.size();
			    Letter letter  = first.get((int) (Math.random()*length));
			    char lettre = letter.getLetter();
			 ArrayList<String> dictionairy = makeDictionnary("src/dict_dict_100000_1_10.txt");
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
			String signture =  bytesToHex(ED25519.sign(kp, signature("a", 0, pk)));
			Word word = new Word(letters,hash,pk,signture);
			while(i<10) {//10 tour
				ArrayList<Letter> letterpool = get_letterpool_since(socket,bw,periode*i); 
				LetterPool newletterpool = new LetterPool(periode*i,periode*(i+1),letters);
				DiffLetterPool diff = new DiffLetterPool(periode*i,newletterpool);
				Word makeword = make_word(diff,motDes,word);
				ArrayList<Word> Words = get_wordpool_since(socket,bw,periode*i);
				for(int k = 0 ;k<Words.size();k++) {
					if(Words.get(k).getWord().size()>= makeword.getWord().size()) {
						word = Words.get(k);
					}
				}
				motDes =  findWordDestinaire(dictionairy,word);
				i++;
			}
			

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

	private static byte[] intToBigEndian(int numero) {
		ByteBuffer bb = ByteBuffer.allocate(8);
		bb.rewind();
		bb.order(ByteOrder.LITTLE_ENDIAN);
		bb.putInt((int) numero);
		// System.out.println(bb);
		return bb.array();

	}

}
