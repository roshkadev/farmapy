package mobi.roshka.farmapy.bean;

public class User {
	
	private String acataUserId;
	private String fbName;
	private String fbNick;
	private String fbPictureUrl;
	private String acataAccessToken;

	public User(){
		
	}

	public String getAcataUserId() {
		return acataUserId;
	}

	public void setAcataUserId(String acataUserId) {
		this.acataUserId = acataUserId;
	}


	public String getAcataAccessToken() {
		return acataAccessToken;
	}

	public void setAcataAccessToken(String acataAccessToken) {
		this.acataAccessToken = acataAccessToken;
	}

	public String getFbName() {
		return fbName;
	}

	public void setFbName(String fbName) {
		this.fbName = fbName;
	}

	public String getFbNick() {
		return fbNick;
	}

	public void setFbNick(String fbNick) {
		this.fbNick = fbNick;
	}

	public String getFbPictureUrl() {
		return fbPictureUrl;
	}

	public void setFbPictureUrl(String fbPictureUrl) {
		this.fbPictureUrl = fbPictureUrl;
	}

	@Override
	public String toString() {
		return "access_token: "+ this.acataAccessToken+" fbName: "+ fbName+" fbNick: "+fbNick+" picture: "+ fbPictureUrl;
	}
	
	
}
