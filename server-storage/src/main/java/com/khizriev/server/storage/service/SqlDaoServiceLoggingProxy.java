package com.khizriev.server.storage.service;

import java.util.logging.Logger;

/**
 * AuthenticationServiceProxy
 */
public class SqlDaoServiceLoggingProxy implements AuthenticationService {

    private Logger logger = Logger.getLogger(SqlDaoServiceLoggingProxy.class.getName());

    private AuthenticationService sqlUsersDaoService;

    public SqlDaoServiceLoggingProxy() {
        this.sqlUsersDaoService = new SqlUsersDaoService("jdbc:sqlite:my_cloud_store_server.db", "org.sqlite.JDBC");
    }

    @Override
    public void insertUser(String name, String password) {
        logger.info(String.format("Insert new user with name '%s' and passord '%s'", name,password));
        sqlUsersDaoService.insertUser(name, password);

    }

    @Override
    public void deleteUserByName(String login) {
        logger.info(String.format("Delete user with name '%s'", login));
        sqlUsersDaoService.deleteUserByName(login);
    }

    @Override
    public User selectUserByName(String login) {
        logger.info(String.format("call for user with name '%s'", login));
        return sqlUsersDaoService.selectUserByName(login);
        
    }

    @Override
    public boolean authentication(String login, String password) {
        logger.info(String.format("authentication request from user '%s'", login));
        return sqlUsersDaoService.authentication(login, password);
        
    }

    @Override
    public boolean changePass(String login, String oldPass, String newPass) {
        logger.info(String.format("change password from '%s' to '%s' request from user '%s'", oldPass, newPass, login));
        sqlUsersDaoService.changePass(login, oldPass, newPass);
        return false;
    }

    @Override
    public boolean isLogin(String login) {
        logger.info(String.format(" check login '%s'", login));
        User user = selectUserByName(login);
		if(user == null) {
			return false;
		}
		return true;
    }

    @Override
    public boolean checkPassword(String login, String password) {
        logger.info(String.format(" check password from login '%s'", login));
        User user = selectUserByName(login);
		return user.getPassword().equals(password);
    }
    
}