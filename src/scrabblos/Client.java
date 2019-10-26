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

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Signature;
import java.util.ArrayList;
import java.util.Collections;

import net.i2p.crypto.eddsa.EdDSAEngine;
import net.i2p.crypto.eddsa.spec.EdDSANamedCurveTable;
import net.i2p.crypto.eddsa.spec.EdDSAParameterSpec;
import net.i2p.crypto.eddsa.spec.EdDSAPublicKeySpec;

public class Client {

	private static Socket socket;
	private String getPublicKey() {
		try {
			Signature sgr = new EdDSAEngine(MessageDigest.getInstance("SHA-512"));
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		EdDSAParameterSpec spec = EdDSANamedCurveTable.getByName("ed25519-sha-512");

		return "";
	}
	public static ArrayList<Character> register(Socket socket) throws IOException, JSONException {
		
		OutputStream os = socket.getOutputStream();
		OutputStreamWriter osw = new OutputStreamWriter(os);
		BufferedWriter bw = new BufferedWriter(osw);
					
		JSONObject json = new JSONObject();
		json.put("register", "b7b597e0d64accdb6d8271328c75ad301c29829619f4865d31cc0c550046a08f");
		System.out.println(json.toString());
		byte [] a =intToBigEndian(json.toString().length());
		for(int i = a.length-1 ;i>=0 ;i--) {
			bw.write((char)(a[i]));
		}
		bw.write(json.toString());
		bw.flush();
		int c;
		StringBuilder response= new StringBuilder();
		ArrayList<Character> Letterbag = new ArrayList<Character>();
		
		InputStream is = socket.getInputStream();
		InputStreamReader isr = new InputStreamReader(is);
		BufferedReader br = new BufferedReader(isr);
		while (br.ready()) {
			c = br.read();
		    // Since c is an integer, cast it to a char.
		    // If c isn't -1, it will be in the correct range of char.
			if(c>=34 && c<=127) {
				if((char)c != '1' && (char)c !=',' && (char)c != '\"' && (char)c != '[' && (char)c != ']' && (char)c != '_' && (char)c != ':' && (char)c != '{' && (char)c != '}') {
					Letterbag.add((char)c);
					
		    
		    //System.out.println("char : " + (char)c);
			}
			}
			 //System.out.println("Message received from the server : " + (char)c  );
		}
		//System.out.println(Letterbag);
		return Letterbag;
	}
	public static void continous_listen (Socket socket, BufferedWriter bw) throws JSONException, IOException {
		
		OutputStream os = socket.getOutputStream();
		OutputStreamWriter osw = new OutputStreamWriter(os);
		//BufferedWriter bw = new BufferedWriter(osw);
					
		
		byte [] a =intToBigEndian("{\"listen\":null}".length());
		for(int i = a.length-1 ;i>=0 ;i--) {
			bw.write((char)(a[i]));
		}
		System.out.println("{\"listen\" : null}");
		bw.write("{\"listen\":null}");
		bw.flush();
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
	
	public static void inject_Letter(Socket s, ArrayList<Character> LetterBag,BufferedWriter bw) throws IOException, JSONException {
		Collections.shuffle(LetterBag);
	    Character letter = LetterBag.get(0);
		JSONObject json = new JSONObject();
		json.put("letter", letter);
		json.put("signature","8b6547447108e11c0092c95e460d70f367bc137d5f89c626642e1e5f2ce");
		json.put("author", "b7b597e0d64accdb6d8271328c75ad301c29829619f4865d31cc0c550046a08f");
		json.put("head","e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855");
		json.put("period", 0);
		json.put("letter", letter);
		String j = "{ \"inject_letter\": { \"letter\":\"a\", \"period\":0, \"head\":\"e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855\", \"author\":\"b7b597e0d64accdb6d8271328c75ad301c29829619f4865d31cc0c550046a08f\", \"signature\":\"8b6547447108e11c0092c95e460d70f367bc137d5f89c626642e1e5f2ce\" }}";
		JSONObject json2 = new JSONObject();
		json2.put("inject_letter", json);
		
		byte [] a =intToBigEndian(j.length());

		//byte [] a =intToBigEndian(json2.toString().length());
		//byte [] a =intToBigEndian(("{ \"Inject_letter\" : "+  letter+ " }").length());
		for(int i = a.length-1 ;i>=0 ;i--) {
			bw.write((char)(a[i]));
		}
		//System.out.println("{ \"Inject_letter\" : " + letter + " }");
		System.out.println(j);
		bw.write(j.toString());
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

	
	public static void inject_word(Socket s, String word) throws IOException, JSONException {
		OutputStream os = socket.getOutputStream();
		OutputStreamWriter osw = new OutputStreamWriter(os);
		BufferedWriter bw = new BufferedWriter(osw);
		JSONObject json = new JSONObject();
		json.put("inject_word", word);
		byte [] a =intToBigEndian(json.toString().length());
		//byte [] a =intToBigEndian(("{ \"Inject_letter\" : "+  letter+ " }").length());
		for(int i = a.length-1 ;i>=0 ;i--) {
			bw.write((char)(a[i]));
		}
		//System.out.println("{ \"Inject_letter\" : " + letter + " }");
		bw.write(json.toString());
		//bw.write("{ \"Inject_letter\" : " +  letter +" }");
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
			System.out.println(LetterBag);
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
