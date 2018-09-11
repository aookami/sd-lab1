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
	boolean listening;
	boolean didIRequestR1 = false;
	boolean didIRequestR2 = false;
	
	boolean didIRequestR1Q = false;
	boolean didIRequestR2Q = false;
	
	boolean waitingR1Reply = false;
	boolean receivedR1Reply = false;

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
					listening = true;
					input = listenToMc(socket, group);
				} catch (Exception e) {
					System.out.println(String.valueOf(name) + "EXCEPTION");
					System.out.println(e.getMessage());
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

	synchronized void send(String input, int target, String dataId, String what) {
		try {
			Thread.sleep(150);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

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
		send(sc);

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
		send(sc);

	}

	void exitRoom() {
		String a = String.valueOf(name);
		String b = "EXIT";
		String sc = a + ":" + b;
		send(sc);
		
		listening = false;
		
	}
	
	void send(String msg) { 
		byte[] c = msg.getBytes();

		DatagramPacket p = new DatagramPacket(c, c.length, aHost, 4446);
		try {
			msckt.send(p);

		} catch (IOException e) {
			System.out.println(name + "IOException at " + name + " send requestr1 msg	");
		}

	}

	void requestR1() {
		
		if(r1queue.isEmpty()) {
			requestR1Queue();
			try {
				Thread.sleep(1500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		if(r1queue.isEmpty() || r1queue.get(0) == name) {
			requestR1();
		}
		
		didIRequestR1 = true;
		
		send(String.valueOf(name) + ":r1REQ");
		
	}
	
	void requestR1Queue(){
		send(name + ":" + "r1REQueue");
		didIRequestR1Q = true;
		
	} 
	
	void sendr1Queue() {
		StringBuilder queue = new StringBuilder();
		
		for(int x : r1queue) {
			queue.append(String.valueOf(x) + "\\.");
		}
		
	
		send(name + ":r1Queue:" + queue.toString());
		
	}
	void replyr1User() {
		if (!r1queue.isEmpty()) {
			String a = String.valueOf(name) + ":" + "r1USR" + ":" + r1queue.get(0);
			send(a);
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
			System.out.println(name + " received: " + received);
			
			if(didIRequestR1Q) {
				if(received.contains("r1QUEUE")) {
					r1queue = new LinkedList<>();
					for(String x : received.split(":")[2].split("\\.")) {
						r1queue.add(Integer.valueOf(x));
					}
				}
				didIRequestR1Q = false;

			}
			

			if (didIRequestR1) {	
				if (received.contains(String.valueOf("r1")) && received.contains(String.valueOf(name))
						&& received.contains("FAILED")) {
						System.out.println(String.valueOf(name) + " denied r1");
						try {
							Thread.sleep(5000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						requestR1();
						

				}
				else if(received.contains("r1") && received.contains(String.valueOf(name))
						&& received.contains("TRUE")) {
					System.out.println(name + " USING r1");
					try {
						Thread.sleep(5000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					System.out.println(String.valueOf(name ) + "USED r1");
					didIRequestR1 = false;

				}
			}

			if (received.split(":")[0].contains(String.valueOf(name))) {
				
				if(received.contains("EXIT")) {
					System.out.println(String.valueOf(name) + "EXITING");
					break;
				}
				
				
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
			


		}
		
		return("");
	}

	public void printList() {
		System.out.print(name + "LIST");
		for (ParticipantInfo x : peers) {
			System.out.print("-" + x.name);
		}

		System.out.println("LISTEND");
	}
}
