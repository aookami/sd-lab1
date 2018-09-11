package sdlab1;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Scanner;

public class Control {
	


	static InetAddress aHost;

	public String publickey;
	
	
	public static void main(String[] args) {
		while (true) {

			System.out.println("Enter your username: ");
			Scanner scanner = new Scanner(System.in);
			String username = scanner.nextLine();
			System.out.println("Your username is " + username);

			MulticastSocket socket = null;
			InetAddress group;
			try {
				group = InetAddress.getByName("224.24.24.42");
				socket = new MulticastSocket(4446);
				socket.joinGroup(group);

			} catch (Exception e) {
				System.out.println("EEEEEEXp");
			}
			
			byte[] c = username.getBytes();
			try {

				DatagramPacket p = new DatagramPacket(c, c.length, aHost, 4446);

				socket.send(p);

			} catch (Exception e) {
				System.out.println("Exception at sending multicast datapcket");

				System.out.println(e.getMessage() + "AAAAAAAa");
			}

		}
	}
}