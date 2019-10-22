package scrabblos;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;
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
	 
	    public static void main(String args[])
	    {
	        try
	        {
	            String host = "localhost";
	            int port = 12345;
	            InetAddress address = InetAddress.getByName(host);
	           // System.err.print(address);
	            socket = new Socket(address, port);
	 
	            //Send the message to the server
	            OutputStream os = socket.getOutputStream();
	            OutputStreamWriter osw = new OutputStreamWriter(os);
	            BufferedWriter bw = new BufferedWriter(osw);
	            
	            JSONObject json = new JSONObject();
	            int k = 2*json.toString().toCharArray().length;
	            json.put("register","ed25519");
	            
	            char [] strs = {'\000','\000','\000','\000','\000','\000','\000','\004'};
	 
	            String mes = "";
	            for(int i=0;i<strs.length;i++) {
	            	mes += strs[i];
	            }
	            mes +=json.toString();
	            bw.write(mes);
	            
	            
	            bw.flush();
	            System.out.println("Message sent to the server : "+json.toString());
	            
//	            JSONObject getlettres = new JSONObject();
//	            json.put("get_full_letterpool",null);
	 
	            //Get the return message from the server
	            InputStream is = socket.getInputStream();
	            InputStreamReader isr = new InputStreamReader(is);
	            BufferedReader br = new BufferedReader(isr);
	            String message = br.readLine();
	            System.out.println("Message received from the server : " +message);
	        }
	        catch (Exception exception)
	        {
	            exception.printStackTrace();
	        }
	        finally
	        {
	            //Closing the socket
	            try
	            {
	                socket.close();
	            }
	            catch(Exception e)
	            {
	                e.printStackTrace();
	            }
	        }
	    }

}
