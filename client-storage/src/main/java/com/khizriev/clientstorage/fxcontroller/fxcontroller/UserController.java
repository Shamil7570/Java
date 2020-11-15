package com.khizriev.clientstorage.fxcontroller.fxcontroller;

import com.khizriev.clientstorage.Network;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import com.khizriev.clientstorage.Network;
import com.khizriev.common.storage.AuthenticationMessage;
import com.khizriev.common.storage.AuthenticationMessage.AuthCommandType;

import java.net.URL;
import java.util.ResourceBundle;

public class UserController implements Initializable {
	
	@FXML
	private VBox rootPane;
	
	@FXML
	private Label userLabel;
	
	@FXML
	private TextField loginField;
	
	@FXML
	private PasswordField passwordField;
	
	@FXML 
	private PasswordField confirmPasswordField;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
	}
	
	public void setUserLabelText(String string) {
		userLabel.setText(string);
		if(userLabel.getText().equals("Change Password")) {
			passwordField.setPromptText("Old Password");;
			confirmPasswordField.setPromptText("New Password");
		}
		
	}
	
	@FXML
	private void enter() {
		String password = passwordField.getText().equals(confirmPasswordField.getText()) ? this.passwordField.getText() : null;
		if(password != null) {
			if(userLabel.getText().equals("Create New User")) {
				Network.sendMsg(new AuthenticationMessage(loginField.getText(), password, AuthCommandType.REGISTRATION));
				Network.sendMsg(new AuthenticationMessage(loginField.getText(), password, AuthCommandType.REGISTRATION));
			}

			if(userLabel.getText().equals("Delete User")) {
				Network.sendMsg(new AuthenticationMessage(loginField.getText(), password, AuthCommandType.DELETE_USER));
			}
		} 
		if(confirmPasswordField.getPromptText().equals("New Password")) {
			Network.sendMsg(new AuthenticationMessage(loginField.getText(), passwordField.getText(),
					confirmPasswordField.getText(), AuthCommandType.CHANGE_PASS));
		}
		 Stage stage = (Stage) rootPane.getScene().getWindow();
		 stage.close();
	}

}
