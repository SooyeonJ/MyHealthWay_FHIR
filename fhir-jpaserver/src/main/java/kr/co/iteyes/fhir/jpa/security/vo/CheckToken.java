package kr.co.iteyes.fhir.jpa.security.vo;


import java.util.Date;
import java.util.List;

public class CheckToken {
	private boolean active;

	private Date exp;

	private String user_name;

	private List<String> authorities;

	private String client_id;

	private List<String> scope;

	private List<String> grant_list;

	private String validityYN;


	public String getValidityYN() {
		return validityYN;
	}

	public void setValidityYN(String validityYN) {
		this.validityYN = validityYN;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public Date getExp() {
		return exp;
	}

	public void setExp(Date exp) {
		this.exp = exp;
	}

	public String getUser_name() {
		return user_name;
	}

	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}

	public List<String> getAuthorities() {
		return authorities;
	}

	public void setAuthorities(List<String> authorities) {
		this.authorities = authorities;
	}

	public String getClient_id() {
		return client_id;
	}

	public void setClient_id(String client_id) {
		this.client_id = client_id;
	}

	public List<String> getScope() {
		return scope;
	}

	public void setScope(List<String> scope) {
		this.scope = scope;
	}

	public List<String> getGrant_list() {
		return grant_list;
	}

	public void setGrant_list(List<String> grant_list) {
		this.grant_list = grant_list;
	}
}
