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

			JSONObject json = new JSONObject();
			int k = 2 * json.toString().toCharArray().length;
			json.put("register", "b7b597e0d64accdb6d8271328c75ad301c29829619f4865d31cc0c550046a08f");
			byte [] a =intToBigEndian(json.toString().length());
			String prex = "";
			String mes = "";
			
			for(int i = a.length-1 ;i>=0 ;i--) {
				//mes +=Integer.toString(a[i],8);
				prex += convertToOctat(a[i]);
			}
//			
			
//			prex = "\000\000\000\000\000\000\000\117";
//			System.out.println("prex:  " + prex);
			mes =prex + json.toString();
	     	mes += json.toString();
//			mes = prex + json.toString();
			bw.write(mes);

			bw.flush();
			System.out.println("Message sent to the server : " + mes);


			// Get the return message from the server
			System.out.println("port : " + socket.getReceiveBufferSize());

			InputStream is = socket.getInputStream();
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);
			String message = "";
			

			System.out.println("here : ");
			message = br.readLine();
			System.out.println("Message received from the server : ");
			
			System.out.println("leave" + message);
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
	
	public static String convertToOctat(byte b) {
	    int i = b;
	    String str = Integer.toOctalString(i);
	    while(str.length()<3) {
	    	str = "0"+str;
	    }
	    str = "\\" + str;
	    return str;
	    //return Integer.toHexString(i);
	  }
	
	private static byte[] intToBigEndian(int numero) {
		ByteBuffer bb = ByteBuffer.allocate(8);
		bb.rewind();
		bb.order(ByteOrder.LITTLE_ENDIAN);
		bb.putInt((int) numero);
		System.out.println(bb);
		return bb.array();
		
	}
//	private static String convertToOctat(byte[] bb) {
//		String str = "";
//		for(int i =bb.length-1;i>=0;i--) {
//			str ="\"+;
//		}
//		
//		
//		return str;
//	}
	
}
