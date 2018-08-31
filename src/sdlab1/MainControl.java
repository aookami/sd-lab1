package sdlab1;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;

public class MainControl {

	static Thread t0 = new Thread("MC") {
		@Override
		public void run() {
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

	static Thread t1 = new Thread("Vash") {
		@Override
		public void run() {
			Participant t1 = new Participant(113);
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			t1.enterRoom();
			t1.exitRoom();

		}
	};

	public static void main(String[] args) { 

			t0.start();

			t1.start();

	}

	static void listenToMc(MulticastSocket socket, InetAddress group) {
		while (true) {
			byte[] buf = new byte[256];
			DatagramPacket pkct = new DatagramPacket(buf, buf.length);
			try {
				socket.receive(pkct);
			} catch (IOException e) {
				System.out.println("IOException at listenToMc");

			}

			String received = new String(pkct.getData());
			System.out.println("Main received: " + received);

		}
	}

}
