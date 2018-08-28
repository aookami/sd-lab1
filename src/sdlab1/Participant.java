package sdlab1;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class Participant {

	int name;

	private String privatekey;

	InetAddress aHost;

	public String publickey;

	MulticastSocket msckt;
	Thread listener = new Thread(){
		@Override
		public void run(){
			MulticastSocket socket;
			InetAddress group;

			try {
				group = InetAddress.getByName("224.24.24.42");
				socket = new MulticastSocket(4446);
				socket.joinGroup(group);
				listenToMc(socket, group);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	};
	
	Participant(int name) {
		this.name = name;
		privatekey = String.valueOf(Integer.valueOf(name) * Integer.valueOf(name) % 50000);
		publickey = String.valueOf(Integer.valueOf(name) * 2 % 50000);

		try {
			msckt = new MulticastSocket(4446);
			aHost = InetAddress.getByName("224.24.24.42");
			msckt.joinGroup(aHost);
		} catch (IOException e) {
			System.out.println("IOException at " + name + " init");
		}
		listener.start();
	}

	void enterRoom() {

		String a = String.valueOf(name);
		String b = "ENTER";

		String sc = a + ":" + b + ":" + publickey;
		byte[] c = sc.getBytes();
		try {
		
			DatagramPacket p = new DatagramPacket(c, c.length, aHost, 4446);

			msckt.send(p);

		} catch (Exception e) {
			System.out.println(e.getMessage() + "AAAAAAAa");
		}

	}

	void exitRoom() {
		String a = String.valueOf(name);
		String b = "EXIT";
		String sc = a + ":" + b;
		byte[] c = sc.getBytes();
		
		DatagramPacket p = new DatagramPacket(c, c.length, aHost, 4446);
		try {
			msckt.send(p);
		} catch (IOException e) {
			System.out.println("IOException at " + name + " send enter msg	");
		}
	}

	void listenToMc(MulticastSocket socket, InetAddress group) {
		while (true) {
			byte[] buf = new byte[256];
			DatagramPacket pkct = new DatagramPacket(buf, buf.length);
			try {
				socket.receive(pkct);
				
			} catch (IOException e) {
				System.out.println("IOException at listenToMc");

			}

			String received = new String(pkct.getData());
			
			if(received.split(":")[0].equals(String.valueOf(name))){
				continue;
			}
			System.out.println(String.valueOf(name)  + " received: " + received);

		}
	}
}
