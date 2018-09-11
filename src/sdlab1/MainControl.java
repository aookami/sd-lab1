package sdlab1;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class MainControl {
	static Thread input = new Thread("input") {
		@Override
		public void run() {
			while (true) {

				Scanner reader = new Scanner(System.in); // Reading from System.in
				System.out.println("Enter a number: ");
				String n = null;
				try {
					n = reader.nextLine(); // Scans the next token of the input as an int.					
				}catch (Exception e) {
					continue;
				}
				// once finished
				reader.close();
				System.out.println(n);
			}

		}
	};

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
			Participant t1 = new Participant(300);
			t1.enterRoom();
			
			
			try {
				Thread.sleep(1500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			t1.requestR1();
			
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			t1.exitRoom();

		}
	};

	static Thread t2 = new Thread("Giro") {
		@Override
		public void run() {
			Participant t2 = new Participant(500);

			t2.enterRoom();
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			t2.requestR1();
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//t2.exitRoom();

		}
	};

	static Thread t3 = new Thread("Tobi") {
		@Override
		public void run() {
			Participant t3 = new Participant(730);
			try {
				Thread.sleep(150);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			t3.enterRoom();
			
			try {
				Thread.sleep(15000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			t3.requestR1();
			
			

		}
	};
	public static void main(String[] args) {
		
		MockResource r1 = new MockResource("r1");
		
		r1.r.start();
		
		
		input.start();
		t0.start();
		try {
			Thread.sleep(150);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		t1.start();
		try {
			Thread.sleep(150);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		t2.start();
		try {
			Thread.sleep(150);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		t3.start();
		
		

	}

	synchronized static void  listenToMc(MulticastSocket socket, InetAddress group) {
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
