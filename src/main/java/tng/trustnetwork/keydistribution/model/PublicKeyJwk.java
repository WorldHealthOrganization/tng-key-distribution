package tng.trustnetwork.keydistribution.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

public class PublicKeyJwk {
	@JsonIgnoreProperties(ignoreUnknown = true)
	private String kty;
	@JsonIgnoreProperties(ignoreUnknown = true)
	private String crv;
	@JsonIgnoreProperties(ignoreUnknown = true)
	private String x;
	@JsonIgnoreProperties(ignoreUnknown = true)
	private String y;
	@JsonIgnoreProperties(ignoreUnknown = true)
	private List<String> x5c;
	
	
	public String getKty() {
		return kty;
	}
	public void setKty(String kty) {
		this.kty = kty;
	}
	public String getCrv() {
		return crv;
	}
	public void setCrv(String crv) {
		this.crv = crv;
	}
	public String getX() {
		return x;
	}
	public void setX(String x) {
		this.x = x;
	}
	public String getY() {
		return y;
	}
	public void setY(String y) {
		this.y = y;
	}
	public List<String> getX5c() {
		return x5c;
	}
	public void setX5c(List<String> x5c) {
		this.x5c = x5c;
	}
	
	
}
