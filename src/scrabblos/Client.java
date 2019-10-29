package scrabblos;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.CharConversionException;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.ObjectInputStream.GetField;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.time.Period;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;

import net.i2p.crypto.eddsa.EdDSAEngine;
import net.i2p.crypto.eddsa.EdDSAKey;
import net.i2p.crypto.eddsa.EdDSAPublicKey;
import net.i2p.crypto.eddsa.spec.EdDSANamedCurveTable;
import net.i2p.crypto.eddsa.spec.EdDSAParameterSpec;
import net.i2p.crypto.eddsa.spec.EdDSAPublicKeySpec;

public class Client {

	private static Socket socket;
	private static String pk;
	private static KeyPair kp;
	
	public static ArrayList<String> register(Socket socket) throws IOException, JSONException, NoSuchAlgorithmException, NoSuchProviderException {
		
		OutputStream os = socket.getOutputStream();
		OutputStreamWriter osw = new OutputStreamWriter(os);
		BufferedWriter bw = new BufferedWriter(osw);	
		JSONObject json = new JSONObject();
		
		//CREATION DE LA CLE PUBLIQUE
		ED25519 ed = new ED25519();
		kp = ed.generateKeys();
		EdDSAPublicKey public_k = (EdDSAPublicKey) kp.getPublic();
		pk = Utils.bytesToHex(public_k.getAbyte());
		
		//ENVOIE DU MESSAGE AU SERVEUR
		json.put("register",pk);
		byte [] a =Utils.intToBigEndian(json.toString().length());
		for(int i = a.length-1 ;i>=0 ;i--) {
			bw.write((char)(a[i]));
		}
		bw.write(json.toString());
		bw.flush();
		InputStream is = socket.getInputStream();
		//InputStreamReader isr = new InputStreamReader(is);
		DataInputStream di = new DataInputStream(is);
		JSONObject j = new JSONObject(Utils.readAnswer(di));
		JSONArray alphabet = j.getJSONArray("letters_bag");
		System.out.println(alphabet.toString());
		ArrayList<String> letterbag = new ArrayList<String>();
		for(int i = 0; i<alphabet.length();i++) {
			letterbag.add(alphabet.getString(i));
		}
		//RECUPERATION DU SAC DE LETTRES
		System.out.println(letterbag);
		return letterbag;
	}
	
	public static Letter choose_Letter(ArrayList<String> LetterBag, String hash) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException, IOException {
		MessageDigest digest = MessageDigest.getInstance("SHA-256");
		Collections.shuffle(LetterBag);
		String letter = LetterBag.get(0);
		String signature = Utils.bytesToHex(Utils.signature2(letter,digest.digest((hash).getBytes()), 0, kp));
		String head = Utils.bytesToHex(digest.digest((hash).getBytes()));
		long period = 0;
		return new Letter(letter, period, head, pk, signature);
	}
	
	public static void inject_Letter(Socket s, ArrayList<String> LetterBag,String hash,BufferedWriter bw) throws IOException, JSONException, InvalidKeyException, NoSuchAlgorithmException, SignatureException {
		MessageDigest digest = MessageDigest.getInstance("SHA-256");

		Letter l = choose_Letter(LetterBag,hash);
		JSONObject json = new JSONObject();
		json.put("letter", l.getLetter());
		json.put("author",l.getAuthor());
		json.put("signature", l.getSignature());
		json.put("head",l.getHash());
		json.put("period", l.getPeriod());
		
		String j = "{ \"inject_letter\": { \"letter\":\"a\", \"period\":0, \"head\":\"e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855\", \"author\":\"b7b597e0d64accdb6d8271328c75ad301c29829619f4865d31cc0c550046a08f\", \"signature\":\"8b6547447108e11c0092c95e460d70f367bc137d5f89c626642e1e5f2ce\" }}";
		JSONObject json2 = new JSONObject();
		json2.put("inject_letter", json);
		System.out.println("taille" + json2.toString().length());
		byte [] a =Utils.intToBigEndian(json2.toString().length());
		System.out.println(a);
		for(int i = a.length-1 ;i>=0 ;i--) {
			System.out.println((char)(a[i]));
			bw.write((char)(a[i]));
		}
		System.out.println(j);
		bw.write(json2.toString());
		bw.flush();
	}
	
	public static void main(String args[]) {
		try {
			String host = "localhost";
			int port = 12345;
			InetAddress address = InetAddress.getByName(host);
			
			// System.err.print(address);
			socket = new Socket(address, port);
			OutputStream os = socket.getOutputStream();
			OutputStreamWriter osw = new OutputStreamWriter(os);
			BufferedWriter bw = new BufferedWriter(osw);
			// Send the message to the server
			//ArrayList<Character> LetterBag = 
			ArrayList<String> letterBag = register(socket);
			//System.out.println(LetterBag);
			inject_Letter(socket,letterBag,"",bw);
			inject_Letter(socket,letterBag,"",bw);
			inject_Letter(socket,letterBag,"",bw);
			inject_Letter(socket,letterBag,"",bw);

			//inject_Letter(socket,LetterBag);
			//continous_listen(socket,bw);
			LetterPool lp = CommonOperations.get_full_letterpool(socket,bw);
			//DiffLetterPool dif = CommonOperations.get_letterpool_since(socket,bw,0);
			System.out.println("ah la la" + lp.getLetters());
			
			
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

