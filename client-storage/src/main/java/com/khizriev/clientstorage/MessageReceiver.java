package com.khizriev.clientstorage;

import com.khizriev.common.storage.AbstractMessage;
import com.khizriev.common.storage.AuthenticationMessage;
import com.khizriev.common.storage.FileMessage;
import com.khizriev.common.storage.FileParametersListMessage;

import java.io.IOException;
import java.util.concurrent.Exchanger;


/**
 * Класс создает поток, который получает от сервера и обменивается  сообщениями с другими потоками по средствам
 * {@link Exchanger}
 */
public class MessageReceiver implements Runnable {
	
	private Exchanger<AuthenticationMessage> authExchanger;
	private Exchanger<AbstractMessage> absExchanger;
	
	public MessageReceiver(Exchanger<AuthenticationMessage> authExchanger, Exchanger<AbstractMessage> absExchanger) {
		this.authExchanger = authExchanger;
		this.absExchanger = absExchanger;
		Thread thread = new Thread(this, "ClientMessageExchanger");
		thread.setDaemon(true);
		thread.start();
	}

	@Override
	public void run() {
		while(true) {
			try {
				AbstractMessage abstractMessage = Network.readObject();
				if(abstractMessage instanceof AuthenticationMessage) {
					AuthenticationMessage authenticationMessage = (AuthenticationMessage) abstractMessage;
					authExchanger.exchange(authenticationMessage);
				}
				if(abstractMessage instanceof FileParametersListMessage ||
						abstractMessage instanceof FileMessage) {
					absExchanger.exchange(abstractMessage);
				}
					
			} catch (ClassNotFoundException | IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}
