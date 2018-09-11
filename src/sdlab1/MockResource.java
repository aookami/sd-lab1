package sdlab1;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;

public class MockResource {

	String name;

	String user;

	boolean isCritical;

	public MockResource(String id) {
		this.name = id;
	}

	Thread r = new Thread() {
		@Override
		public void run() {

			while (true) {

				MulticastSocket socket = null;
				InetAddress group;
				try {
					group = InetAddress.getByName("224.24.24.42");
					socket = new MulticastSocket(4446);
					socket.joinGroup(group);
				} catch (Exception e) {
					System.out.println("EEEEEEXp");
				}
				byte[] buf = new byte[256];
				DatagramPacket pkct = new DatagramPacket(buf, buf.length);
				try {
					socket.receive(pkct);

				} catch (IOException e) {
					System.out.println("IOException at listenToMc");

				}

				String received = new String(pkct.getData());
				System.out.println(name + " received " + received);
				if (!received.contains(name)) {
					continue;
				}

				if (received.contains(""))
					if (received.split(":")[1].contains(name + "REQ")) {
						if (isCritical) {
							// broadcast false+id do requisitor
							String a = name + ":" + received.split(":")[0] + "FALSE";
							byte[] c = a.getBytes();

							DatagramPacket p = null;
							try {
								p = new DatagramPacket(c, c.length, InetAddress.getByName("224.24.24.42"), 4446);
							} catch (UnknownHostException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
							try {
								group = InetAddress.getByName("224.24.24.42");
								socket = new MulticastSocket(4446);
								socket.joinGroup(group);
								socket.send(p);

							} catch (IOException e) {
								System.out.println(name + "IOException at " + name + " send request msg	" + name);
							}
						} else {
							isCritical = true;
							String a = name + ":" + received.split(":")[0] + "TRUE";
							byte[] c = a.getBytes();

							DatagramPacket p = null;
							try {
								p = new DatagramPacket(c, c.length, InetAddress.getByName("224.24.24.42"), 4446);
							} catch (UnknownHostException e1) {
								System.out.println(
										name + "UnknownHostException at " + name + " send request msg " + name);

							}
							try {
								group = InetAddress.getByName("224.24.24.42");
								socket = new MulticastSocket(4446);
								socket.joinGroup(group);
								socket.send(p);

							} catch (IOException e) {
								System.out.println(name + "IOException at " + name + " send request msg" + name);
							}
							Thread denyall = new Thread() {

								@Override
								public void run() {
									try {
										while (true) {
											MulticastSocket socketl = new MulticastSocket(4446);
											InetAddress groupl = InetAddress.getByName("224.24.24.42");
											socketl.joinGroup(groupl);
											byte[] buf = new byte[256];
											DatagramPacket pkct = new DatagramPacket(buf, buf.length);
											try {
												socketl.receive(pkct);
											} catch (IOException e) {
												System.out.println("IOException at listenToMc");

											}

											String received = new String(pkct.getData());
											if (received.contains(name + "REQ")) {
												System.out.println(name + " denier received: " + received);
												send(name + ":" + received.split(":")[0] + "FALSE", socketl, groupl);
											}
										}
									} catch (Exception e) {
										return;
									}
								}

								void send(String msg, MulticastSocket socketl, InetAddress aHost) {
									byte[] c = msg.getBytes();

									DatagramPacket p = new DatagramPacket(c, c.length, aHost, 4446);
									try {
										socketl.send(p);

									} catch (IOException e) {
										System.out.println(name + "IOException at " + name + " send request msg	");
									}

								}
							};

							denyall.start();

							try {
								Thread.sleep(5000);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							isCritical = false;
							denyall.interrupt();

						}
					}
			}
		}
	};

}
