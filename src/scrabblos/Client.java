package scrabblos;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Signature;

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
	public static void register(BufferedWriter bw) throws IOException, JSONException {
		JSONObject json = new JSONObject();
		json.put("register", "b7b597e0d64accdb6d8271328c75ad301c29829619f4865d31cc0c550046a08f");

		byte [] a =intToBigEndian(json.toString().length());
		for(int i = a.length-1 ;i>=0 ;i--) {
			bw.write((char)(a[i]));
		}
		bw.write(json.toString());
		bw.flush();

	}
	public static void continous_listen(BufferedWriter bw) throws JSONException, IOException {
		byte [] a =intToBigEndian("{ \"listen\" : null }".length());
		for(int i = a.length-1 ;i>=0 ;i--) {
			bw.write((char)(a[i]));
		}
		System.out.println("{ \"listen\" : null }");
		bw.write("{ \"listen\" : null }");
		bw.flush();
	}
	
	public static void stop_listen(BufferedWriter bw) throws JSONException, IOException {
		byte [] a =intToBigEndian("{ \"stop_listen\" : null }".length());
		for(int i = a.length-1 ;i>=0 ;i--) {
			bw.write((char)(a[i]));
		}
		System.out.println("{ \"stop_listen\" : null }");
		bw.write("{ \"stop_listen\" : null }");
		bw.flush();
	}
	
	public static void inject_Letter(BufferedWriter bw) {
		
	}
	

	public static void main(String args[]) {
		try {
			String host = "localhost";
			int port = 12345;
			InetAddress address = InetAddress.getByName(host);
			// System.err.print(address);
			socket = new Socket(address, port);

			// Send the message to the server
			OutputStream os = socket.getOutputStream();
			OutputStreamWriter osw = new OutputStreamWriter(os);
			BufferedWriter bw = new BufferedWriter(osw);
			
			register(bw);
			
			//continous_listen(bw);
			//stop_listen(bw);
			
			
			//socket.close();
			
			InputStream is = socket.getInputStream();
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);
			/*while(true) {
			int message = br.read();
			System.out.println("Message received from the server : " + message );
			}
			*/
			int c;
			StringBuilder response= new StringBuilder();

			while (br.ready()) {
				c = br.read();
			    // Since c is an integer, cast it to a char.
			    // If c isn't -1, it will be in the correct range of char.
				if(c>=34 && c<=127) {
			    response.append((char)c );
			    System.out.println("char : " + (char)c);
				}
				 System.out.println("Message received from the server : " + c  );
			}
			String result = response.toString();
			System.out.println("Message : " + result );
			//System.out.println("leave" + message);
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
