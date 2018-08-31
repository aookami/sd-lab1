package sdlab1;

public class ParticipantInfo {

	int name;
	
	String publicKey;
	
	ParticipantInfo(int name, String key){
		this.name = name;
		this.publicKey = key;  
		
		
	} 

	public int getName() {
		return name;
	}

	public void setName(int name) {
		this.name = name;
	}

	public String getPublicKey() {
		return publicKey;
	}

	public void setPublicKey(String publicKey) {
		this.publicKey = publicKey;
	}
	
}
