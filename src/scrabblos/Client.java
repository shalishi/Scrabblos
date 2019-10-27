package scrabblos;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.CharConversionException;
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
	
	private static String bytesToHex(byte[] hashInBytes) {

        StringBuilder sb = new StringBuilder();
        for (byte b : hashInBytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();

    }
	
	public static ArrayList<Character> register(Socket socket) throws IOException, JSONException, NoSuchAlgorithmException, NoSuchProviderException {
		
		OutputStream os = socket.getOutputStream();
		OutputStreamWriter osw = new OutputStreamWriter(os);
		BufferedWriter bw = new BufferedWriter(osw);	
		JSONObject json = new JSONObject();
		
		//CREEATION DE LA CLE PUBLIQUE
		ED25519 ed = new ED25519();
		byte[] bytes = ed.generateKeys().getPublic().getEncoded();
		System.out.println(bytesToHex(bytes).length());
		pk = bytesToHex(bytes).substring(0,64)																																																																																																																																																																																																																																																								;
		kp = ed.generateKeys();
		json.put("register",pk);
		//json.put("register", "b7b597e0d64accdb6d8271328c75ad301c29829619f4865d31cc0c550046a08f");
		System.out.println(json.toString());
		byte [] a =intToBigEndian(json.toString().length());
		for(int i = a.length-1 ;i>=0 ;i--) {
			bw.write((char)(a[i]));
		}
		bw.write(json.toString());
		bw.flush();
		//RECUPERATION DU SAC DE LETTRES
		int c;
		StringBuilder response= new StringBuilder();
		ArrayList<Character> Letterbag = new ArrayList<Character>();
		
		InputStream is = socket.getInputStream();
		InputStreamReader isr = new InputStreamReader(is);
		BufferedReader br = new BufferedReader(isr);
		while (br.ready()) {
			c = br.read();
			if(c>=34 && c<=127) {
				if((char)c != '1' && (char)c !=',' && (char)c != '\"' && (char)c != '[' && (char)c != ']' && (char)c != '_' && (char)c != ':' && (char)c != '{' && (char)c != '}') {
					Letterbag.add((char)c);
					
				}
			}
		}
		return Letterbag;
	}
	
	public static void continous_listen (Socket socket, BufferedWriter bw) throws JSONException, IOException {
		
		OutputStream os = socket.getOutputStream();
		OutputStreamWriter osw = new OutputStreamWriter(os);
							
		byte [] a =intToBigEndian("{\"listen\":null}".length());
		for(int i = a.length-1 ;i>=0 ;i--) {
			bw.write((char)(a[i]));
		}
		System.out.println("{\"listen\" : null}");
		bw.write("{\"listen\":null}");
		bw.flush();
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
	
	public static void stop_listen(Socket socket) throws JSONException, IOException {
		
		OutputStream os = socket.getOutputStream();
		OutputStreamWriter osw = new OutputStreamWriter(os);
		BufferedWriter bw = new BufferedWriter(osw);
					
		
		byte [] a =intToBigEndian("{ \"stop_listen\" : null }".length());
		for(int i = a.length-1 ;i>=0 ;i--) {
			bw.write((char)(a[i]));
		}
		System.out.println("{ \"stop_listen\" : null }");
		bw.write("{ \"stop_listen\" : null }");
		bw.flush();
	}
	
	public static void inject_Letter(Socket s, ArrayList<Character> LetterBag,BufferedWriter bw) throws IOException, JSONException, InvalidKeyException, NoSuchAlgorithmException, SignatureException {
		Collections.shuffle(LetterBag);
	    //Character letter = LetterBag.get(0);
		JSONObject json = new JSONObject();
		json.put("letter", "a");
		
		//signature		

		//json.put("signature","8b6547447108e11c0092c95e460d70f367bc137d5f89c626642e1e5f2ceb6108043d4a080223b467bb810c52b5975960eea96a2203a877f32bbd6c4dac16ec07");
		json.put("signature",bytesToHex(ED25519.sign(kp, signature("a", 0, pk))));
		json.put("author",pk);
		//json.put("head","e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855");
		json.put("head","");
		json.put("period", 0);
		//json.put("letter", letter);
		String j = "{ \"inject_letter\": { \"letter\":\"a\", \"period\":0, \"head\":\"e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855\", \"author\":\"b7b597e0d64accdb6d8271328c75ad301c29829619f4865d31cc0c550046a08f\", \"signature\":\"8b6547447108e11c0092c95e460d70f367bc137d5f89c626642e1e5f2ce\" }}";
		JSONObject json2 = new JSONObject();
		json2.put("inject_letter", json);
		System.out.println("taille" + json2.toString().length());
		byte [] a =intToBigEndian(json2.toString().length());
		System.out.println(a);

		//byte [] a =intToBigEndian(json2.toString().length());
		//byte [] a =intToBigEndian(("{ \"Inject_letter\" : "+  letter+ " }").length());
		for(int i = a.length-1 ;i>=0 ;i--) {
			System.out.println((char)(a[i]));
			bw.write((char)(a[i]));
		}
		//System.out.println("{ \"Inject_letter\" : " + letter + " }");
		System.out.println(j);
		bw.write(json2.toString());
		//bw.write("{ \"Inject_letter\" : " +  letter +" }");
		bw.flush();
	}
	
	
	public static void get_full_letterpool(Socket socket,BufferedWriter bw) throws JSONException, IOException {
		
		
		byte [] a =intToBigEndian("{ \"get_full_letterpool\": null}".length());
		for(int i = a.length-1 ;i>=0 ;i--) {
			bw.write((char)(a[i]));
		}
		System.out.println("{ \"listen\" : null }");
		bw.write("{ \"get_full_letterpool\": null}");
		bw.flush();
	}
	
	public static void get_letterpool_since(Socket socket,BufferedWriter bw,int period) throws JSONException, IOException {
		
		
		byte [] a =intToBigEndian(("{ \"get_letterpool_since\":" + period + "null}").length());
		for(int i = a.length-1 ;i>=0 ;i--) {
			bw.write((char)(a[i]));
		}
		System.out.println("{ \"listen\" : null }");
		bw.write("{ \"get_letterpool_since\":" + period + "}");
		bw.flush();
	}


	public static void get_full_wordpool(Socket socket,BufferedWriter bw) throws JSONException, IOException {
		
		byte [] a =intToBigEndian("{ \"get_full_wordpool\": null}".length());
		for(int i = a.length-1 ;i>=0 ;i--) {
			bw.write((char)(a[i]));
		}
		System.out.println("{ \"listen\" : null }");
		bw.write("{ \"get_full_wordpool\": null}");
		bw.flush();
	}
	
	public static void get_wordpool_since(Socket socket,BufferedWriter bw,int period) throws JSONException, IOException {
		
		
		byte [] a =intToBigEndian(("{ \"get_wordpool_since\":" + period + "null}").length());
		for(int i = a.length-1 ;i>=0 ;i--) {
			bw.write((char)(a[i]));
		}
		System.out.println("{ \"listen\" : null }");
		bw.write("{ \"get_wordpool_since\":" + period + "}");
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
			ArrayList<Character> LetterBag = register(socket);
			//System.out.println(LetterBag);
			inject_Letter(socket,LetterBag,bw);
			//inject_Letter(socket,LetterBag);
			//continous_listen(socket,bw);
			//get_full_letterpool(socket,bw);
			
			
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
		//System.out.println(bb);
		return bb.array();
		
	}

	
}
