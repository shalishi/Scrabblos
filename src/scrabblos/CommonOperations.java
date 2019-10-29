package scrabblos;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;

public class CommonOperations {
	public static void continous_listen(Socket socket, BufferedWriter bw) throws JSONException, IOException {

		OutputStream os = socket.getOutputStream();
		OutputStreamWriter osw = new OutputStreamWriter(os);

		byte[] a = Utils.intToBigEndian("{\"listen\":null}".length());
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

		byte[] a = Utils.intToBigEndian("{ \"stop_listen\" : null }".length());
		for (int i = a.length - 1; i >= 0; i--) {
			bw.write((char) (a[i]));
		}
		System.out.println("{ \"stop_listen\" : null }");
		bw.write("{ \"stop_listen\" : null }");
		bw.flush();
	}
	
	public static DiffLetterPool get_full_letterpool(Socket socket, BufferedWriter bw)
			throws JSONException, IOException {

		byte[] a = Utils.intToBigEndian("{ \"get_full_letterpool\": null}".length());
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
		long since = j2.getInt("period");
		
		System.out.println(letterpool);
		
		JSONArray letters = letterpool.getJSONArray("letters");
		long current_period = letterpool.getInt("current_period");
		long next_period = letterpool.getInt("next_period");
		System.out.println(letters.toString());
		ArrayList<Letter> lettersA = new ArrayList<Letter>();
		for (int i = 0; i < letters.length(); i++) {
			lettersA.add(gson.fromJson(letters.getJSONObject(i).toString(), Letter.class));
		}
		LetterPool lp = new LetterPool(current_period, next_period, lettersA);
		DiffLetterPool df = new DiffLetterPool(since, lp);
		return  df;
	}
	
	public static int IsNextTurn(Socket socket) throws IOException, JSONException {
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
				JSONObject j = new JSONObject(res);
				int i = j.getInt("next_turn");
				return i;
	}

	public static DiffLetterPool get_letterpool_since(Socket socket, BufferedWriter bw, int period)
			throws JSONException, IOException {

		// ENVOIE DE LA REQUETE
		byte[] a = Utils.intToBigEndian(("{ \"get_letterpool_since\":" + period + "}").length());
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
				long since = j2.getInt("period");
				
				System.out.println(letterpool);
				
				JSONArray letters = letterpool.getJSONArray("letters");
				long current_period = letterpool.getInt("current_period");
				long next_period = letterpool.getInt("next_period");
				System.out.println(letters.toString());
				ArrayList<Letter> lettersA = new ArrayList<Letter>();
				for (int i = 0; i < letters.length(); i++) {
					lettersA.add(gson.fromJson(letters.getJSONObject(i).toString(), Letter.class));
				}
				LetterPool lp = new LetterPool(current_period, next_period, lettersA);
				DiffLetterPool df = new DiffLetterPool(since, lp);
				return  df;
			}
			
	
	public static DiffWordPool get_full_wordpool(Socket socket, BufferedWriter bw) throws JSONException, IOException {

		byte[] a = Utils.intToBigEndian("{ \"get_full_wordpool\": null}".length());
		for (int i = a.length - 1; i >= 0; i--) {
			bw.write((char) (a[i]));
		}
		System.out.println("{ \"listen\" : null }");
		bw.write("{ \"get_full_wordpool\": null}");
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
				
				JSONObject j2 = (JSONObject) j.get("diff_wordpool");
				System.out.println(j2);
				
				JSONObject wordpool = (JSONObject) j2.get("wordpool");
				long since = j2.getInt("period");
				
				System.out.println(wordpool);
				
				JSONArray words = wordpool.getJSONArray("words");
				long current_period = wordpool.getInt("current_period");
				long next_period = wordpool.getInt("next_period");
				ArrayList<Word> wordsA = new ArrayList<Word>();
				
				for(int i = 0; i<words.length();i++) {
					JSONObject word = words.getJSONObject(i);
					String head = word.getString("head");
					String politicien = word.getString("politicien");
					String signature = word.getString("signature");
					JSONArray lettersInword = word.getJSONArray("word");					
					ArrayList<Letter> lettersA = new ArrayList<Letter>();
					for (int k = 0; k < lettersInword.length(); k++) {
						lettersA.add(gson.fromJson(lettersInword.getJSONObject(k).toString(), Letter.class));
					}
					Word w = new Word(lettersA, head, politicien, signature);
					wordsA.add(w);
				}
				WordPool wp = new WordPool(current_period, next_period, wordsA);
				DiffWordPool df = new DiffWordPool(since, wp);
				return  df;
			

	}

	public static DiffWordPool get_wordpool_since(Socket socket, BufferedWriter bw, int period)
			throws JSONException, IOException {

		byte[] a = Utils.intToBigEndian(("{ \"get_wordpool_since\":" + period + "null}").length());
		for (int i = a.length - 1; i >= 0; i--) {
			bw.write((char) (a[i]));
		}
		System.out.println("{ \"listen\" : null }");
		bw.write("{ \"get_wordpool_since\":" + period + "}");
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
						
						JSONObject j2 = (JSONObject) j.get("diff_wordpool");
						System.out.println(j2);
						
						JSONObject wordpool = (JSONObject) j2.get("wordpool");
						long since = j2.getInt("period");
						
						System.out.println(wordpool);
						
						JSONArray words = wordpool.getJSONArray("words");
						long current_period = wordpool.getInt("current_period");
						long next_period = wordpool.getInt("next_period");
						ArrayList<Word> wordsA = new ArrayList<Word>();
						
						for(int i = 0; i<words.length();i++) {
							JSONObject word = words.getJSONObject(i);
							String head = word.getString("head");
							String politicien = word.getString("politicien");
							String signature = word.getString("signature");
							JSONArray lettersInword = word.getJSONArray("word");					
							ArrayList<Letter> lettersA = new ArrayList<Letter>();
							for (int k = 0; k < lettersInword.length(); k++) {
								lettersA.add(gson.fromJson(lettersInword.getJSONObject(k).toString(), Letter.class));
							}
							Word w = new Word(lettersA, head, politicien, signature);
							wordsA.add(w);
						}
						WordPool wp = new WordPool(current_period, next_period, wordsA);
						DiffWordPool df = new DiffWordPool(since, wp);
						return  df;
					

	}

	

}
