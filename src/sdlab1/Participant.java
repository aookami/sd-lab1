package sdlab1;

import java.io.IOException;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class Participant {
	
	
	String name;
	
	private int privatekey;
	
	InetAddress aHost;
	
	public int publickey;
	
	MulticastSocket msckt;
	Participant(String name){
		privatekey = Integer.valueOf(name)*Integer.valueOf(name)%50000;
		publickey = Integer.valueOf(name)*2%50000;
		
		try {
			msckt = new MulticastSocket();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

}
