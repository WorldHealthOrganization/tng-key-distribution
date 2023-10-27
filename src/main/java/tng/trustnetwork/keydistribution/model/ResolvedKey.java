package tng.trustnetwork.keydistribution.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


public class ResolvedKey {
	
	@JsonIgnoreProperties(ignoreUnknown = true)
	private String type;
	@JsonIgnoreProperties(ignoreUnknown = true)
	private String id;
	@JsonIgnoreProperties(ignoreUnknown = true)
	private String controller;
	@JsonIgnoreProperties(ignoreUnknown = true)
	private PublicKeyJwk publicKeyJwk;
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getController() {
		return controller;
	}
	public void setController(String controller) {
		this.controller = controller;
	}
	public PublicKeyJwk getPublicKeyJwk() {
		return publicKeyJwk;
	}
	public void setPublicKeyJwk(PublicKeyJwk publicKeyJwk) {
		this.publicKeyJwk = publicKeyJwk;
	}
	

}
