package com.khizriev.server.storage.service;

/**
 * AuthenticationService
 */
public interface AuthenticationService {

    void insertUser(String name, String password);

	void deleteUserByName(String login);

	User selectUserByName(String login);

	 boolean authentication(String login, String password);
	
	 boolean changePass(String login, String oldPass, String newPass);

	 boolean isLogin(String login);

	 boolean checkPassword(String login, String password);
    
}