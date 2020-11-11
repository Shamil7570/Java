package com.khizriev.common.storage;

import lombok.Getter;
import lombok.Setter;


/**
 * Тип сообщения для обмена с сервером информации об аутентификации пользователей
 */
@Getter
@Setter
public class AuthenticationMessage extends AbstractMessage {	
	
	private static final long serialVersionUID = 1122033184461537593L;
	
	public enum AuthCommandType {
		REGISTRATION, CHANGE_PASS, AUTHORIZATION, DELETE_USER
	}	
	
	private boolean status;		

	private String login;

	public static long getSerialVersionUID() {
		return serialVersionUID;
	}

	public boolean isStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getNewPassword() {
		return newPassword;
	}

	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}

	public AuthCommandType getAuthCommandType() {
		return authCommandType;
	}

	public void setAuthCommandType(AuthCommandType authCommandType) {
		this.authCommandType = authCommandType;
	}

	private String password;
	private String newPassword;	
	private AuthCommandType authCommandType;

	public AuthenticationMessage(){}
	
	public AuthenticationMessage(String login, String password, String newPassword, AuthCommandType authCommandType) {
		this.login = login;
		this.password = password;
		this.newPassword = newPassword;
		this.authCommandType = authCommandType;
	}

	public AuthenticationMessage(String login, String password, AuthCommandType authCommandType) {
		this.login = login;
		this.password = password;
		this.authCommandType = authCommandType;
	}

	public static class AuthenticationMessageBuilder {
		
		private AuthenticationMessage authenticationMessage;

		public AuthenticationMessageBuilder() {
			this.authenticationMessage = new AuthenticationMessage();
		}

		public AuthenticationMessageBuilder withLogin(String login) {
			this.authenticationMessage.login = login;
			return this;
		}

		public AuthenticationMessageBuilder withPassword(String password) {
			this.authenticationMessage.password = password;
			return this;
		}

		public AuthenticationMessageBuilder withNewPassword(String newPassword) {
			this.authenticationMessage.newPassword = newPassword;
			return this;
		}

		public AuthenticationMessageBuilder withAuthType(AuthCommandType authCommandType) {
			this.authenticationMessage.authCommandType = authCommandType;
			return this;
		}

		public AuthenticationMessage build() {
			return this.authenticationMessage;
		}
	}
	
	
	
	
}
