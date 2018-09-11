package sdlab1;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Participant {

	final int name;

	final private String privatekey;

	InetAddress aHost;

	public String publickey;

	boolean didIRequestR1 = false;
	boolean didIRequestR2 = false;

	List<ParticipantInfo> peers = new ArrayList<>();

	List<Integer> r1queue = new LinkedList<>();
	List<Integer> r2queue = new LinkedList<>();

	int r1user = -1;
	int r2user = -1;

	MulticastSocket msckt;

	Participant(int name) {
		class listener implements Runnable {
			@Override
			public void run() {
				MulticastSocket socket;
				InetAddress group;
				String input = "";
				try {
					group = InetAddress.getByName("224.24.24.42");
					socket = new MulticastSocket(4446);
					socket.joinGroup(group);
					input = listenToMc(socket, group);
				} catch (Exception e) {
					System.out.println("EEEEEEXp");
				}

				// Input handling code - inputs are always who:what:objectofwhat

			}
		}
		;

		this.name = name;
		privatekey = String.valueOf(Integer.valueOf(name) * Integer.valueOf(name) % 50000);
		publickey = String.valueOf(Integer.valueOf(name) * 2 % 50000);

		try {
			msckt = new MulticastSocket(4446);
			aHost = InetAddress.getByName("224.24.24.42");
			msckt.joinGroup(aHost);
		} catch (IOException e) {
			System.out.println(name + "IOException at " + name + " init");
		}
		Thread t = new Thread(new listener());
		t.start();
	}

	void send(String input, int target, String dataId, String what) {

		String myname = String.valueOf(name);

		// get target public key
		String targetPk = null;
		for (ParticipantInfo x : peers) {
			if (x.name == target) {
				targetPk = x.getPublicKey();
			}
		}
		if (targetPk == null) {
			return;
		}

		String sc = myname + ":" + what + ":" + String.valueOf(target) + ":" + dataId + ":" + targetPk + ":";

		byte[] c = sc.getBytes();
		try {

			DatagramPacket p = new DatagramPacket(c, c.length, aHost, 4446);

			msckt.send(p);

		} catch (Exception e) {
			System.out.println(name + "Exception at sending multicast datapcket");

			System.out.println(name + e.getMessage() + "AAAAAAAa");
		}

	}

	void addToPeerList(int id, String publickey) {
		peers.add(new ParticipantInfo(id, publickey));
	}

	void removeFromPeerList(int id) {
		for (ParticipantInfo x : peers) {
			if (x.name == id) {
				peers.remove(x);
			}
		}
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
			System.out.println(name + "Exception at sending multicast datapcket");

			System.out.println(name + e.getMessage() + "AAAAAAAa");
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
			System.out.println(name + "IOException at " + name + " send enter msg	");
		}
	}

	void requestR1() {
		didIRequestR1 = true;
		String a = String.valueOf(name) + ":" + "r1REQ";
		byte[] c = a.getBytes();

		DatagramPacket p = new DatagramPacket(c, c.length, aHost, 4446);
		try {
			msckt.send(p);

		} catch (IOException e) {
			System.out.println(name + "IOException at " + name + " send requestr1 msg	");
		}

	}

	void replyr1User() {
		if (!r1queue.isEmpty()) {
			String a = String.valueOf(name) + ":" + "r1USR" + ":" + r1queue.get(0);
			byte[] c = a.getBytes();

			DatagramPacket p = new DatagramPacket(c, c.length, aHost, 4446);
			try {
				msckt.send(p);

			} catch (IOException e) {
				System.out.println(name + "IOException at " + name + " send enter msg	");
			}
		}
	}

	synchronized String listenToMc(MulticastSocket socket, InetAddress group) {
		while (true) {
			byte[] buf = new byte[256];
			DatagramPacket pkct = new DatagramPacket(buf, buf.length);
			try {
				socket.receive(pkct);

			} catch (IOException e) {
				System.out.println(name + "IOException at listenToMc");

			}

			String received = new String(pkct.getData());

			if (didIRequestR1) {
				if (received.contains(String.valueOf("r1")) && received.contains(String.valueOf(name))
						&& received.contains("FAILED")) {
						System.out.println(String.valueOf(name) + " denied r1");
				}
				else if(received.contains(String.valueOf("r1")) && received.contains(String.valueOf(name))
						&& received.contains("TRUE")) {
					try {
						Thread.sleep(5000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					System.out.println(String.valueOf(name ) + "USED r1");
				}
			}

			if (received.split(":")[0].contains(String.valueOf(name))) {
				continue;
			} else {
				if (received.split(":")[1].contains("r1REQ")) {
					r1queue.add(Integer.valueOf(received.split(":")[0]));
				}
				if (received.split(":")[1].contains("r2REQ")) {
					r2queue.add(Integer.valueOf(received.split(":")[0]));
				}

				if (received.split(":")[1].equals("ENTER")) {
					boolean doIHave = false;
					for (ParticipantInfo x : peers) {
						if (x.name == Integer.valueOf(received.split(":")[0]))
							doIHave = true;
					}
					if (doIHave == false) {
						System.out.println(name + " ADDING " + received.split(":")[0]);
						peers.add(new ParticipantInfo(Integer.valueOf(received.split(":")[0]), received.split(":")[2]));

						// now we got to send back our name and publickey
						printList();
						enterRoom();

					}

				} else if (received.split(":")[1].contains("EXIT")) {
					ParticipantInfo exiter = null;
					for (ParticipantInfo x : peers) {

						if (x.name == Integer.valueOf(received.split(":")[0])) {
							exiter = x;
						}
					}

					System.out.println(String.valueOf(name) + " - " + String.valueOf(exiter.name) + " EXITING"
							+ String.valueOf(Integer.valueOf(received.split(":")[0])));
					peers.remove(exiter);
					System.out.println(name + " REMOVED " + String.valueOf(exiter.name));
					printList();
				}

			}
			try {
				Thread.sleep(150);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			System.out.println(name + " received: " + received);

		}
	}

	public void printList() {
		System.out.print(name + "LIST");
		for (ParticipantInfo x : peers) {
			System.out.print("-" + x.name);
		}

		System.out.println("LISTEND");
	}
}
