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
import java.net.ServerSocket;
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

public class Politicien {

	private static ServerSocket socket;
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

	public static void main(String args[]) {
		try {
			String host = "localhost";
			int port = 12345;
			InetAddress address = InetAddress.getByName(host);
			// System.err.print(address);
			socket = new ServerSocket(port);
			 
			//InputStream is = socket.getInputStream();
			//InputStreamReader isr = new InputStreamReader(socket.getInputStream());
			//BufferedReader br = new BufferedReader(isr);
			//String message = br.readLine();
			//System.out.println("Message received from client : " + message );
			
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
		System.out.println(bb);
		return bb.array();
		
	}

	
}

