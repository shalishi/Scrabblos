package scrabblos;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Signature;
import java.util.ArrayList;

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
	private static String generatePublicKey() {
		try {
			Signature sgr = new EdDSAEngine(MessageDigest.getInstance("SHA-512"));
			
			System.out.println(sgr);
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		EdDSAParameterSpec spec = EdDSANamedCurveTable.getByName("ed25519-sha-512");

		return "";
	}
	
	public static void register(Socket socket) throws IOException, JSONException {
		
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
	
	
	public static void get_full_letterpool(Socket socket,BufferedWriter bw) throws JSONException, IOException {
		
		byte [] a =intToBigEndian("{ \"get_full_letterpool\": null}".length());
		for(int i = a.length-1 ;i>=0 ;i--) {
			bw.write((char)(a[i]));
		}
		System.out.println("{ \"listen\" : null }");
		bw.write("{ \"get_full_letterpool\": null}");
		bw.flush();
	}
	
	public static ArrayList<Letter> get_letterpool_since(Socket socket,BufferedWriter bw,int period) throws JSONException, IOException {
		
		//ENVOIE DE LA REQUETE
		byte [] a =intToBigEndian(("{ \"get_letterpool_since\":" + period + "}").length());
		for(int i = a.length-1 ;i>=0 ;i--) {
			bw.write((char)(a[i]));
		}
		System.out.println("{ \"listen\" : null }");
		bw.write("{ \"get_letterpool_since\":" + period + "}");
		bw.flush();
		
		
		//RECUPERATION DE LA REPONSE SOUS FORME DE STRING
		int c;
		InputStream is = socket.getInputStream();
		InputStreamReader isr = new InputStreamReader(is);
		BufferedReader br = new BufferedReader(isr);
		String res = "";
		Boolean debutRep = false;
		while (br.ready()) {
			c = br.read();
			
			if(debutRep) {
				if(c>=34 && c<=127) {
					res = res + (char)c;
					System.out.println("char : " + (char)c);
				}
			}
			if((char)c =='^') {
				debutRep = true;
			}
			 //System.out.println("Message received from the server : " + (char)c  );
		}
		//TRANSFORMATION DE LA REPONSE SOUS FORME DE LISTE DE LETTER
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
	
	public static void make_word(DiffLetterPool dffl) {
		//dans cette class nous devons ecrire l'algo de creation d'un mot
	}
	
	public static void inject_word(Socket s, String word) throws IOException, JSONException {
		OutputStream os = socket.getOutputStream();
		OutputStreamWriter osw = new OutputStreamWriter(os);
		BufferedWriter bw = new BufferedWriter(osw);
		JSONObject json = new JSONObject();
		json.put("inject_word", word);
		byte [] a =intToBigEndian(json.toString().length());
		System.out.println(a);
		//byte [] a =intToBigEndian(("{ \"Inject_letter\" : "+  letter+ " }").length());
		for(int i = a.length-1 ;i>=0 ;i--) {
			System.out.println("what" + (char)(a[i]));
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
			//generatePublicKey();
			register(socket);
			System.out.println("OUI JE RENTRE");
			get_letterpool_since(socket,bw,0);
			//System.out.println(LetterBag);
			//inject_Letter(socket,LetterBag,bw);
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

