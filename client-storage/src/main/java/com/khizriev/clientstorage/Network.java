package com.khizriev.clientstorage;

import com.khizriev.common.storage.AbstractMessage;
import io.netty.handler.codec.serialization.ObjectDecoderInputStream;
import io.netty.handler.codec.serialization.ObjectEncoderOutputStream;
import lombok.Getter;
import com.khizriev.common.storage.AuthenticationMessage;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.Exchanger;

/**
 * Класс обслуживающий сеть со стороны клиента
 */
public class Network {
	private static Socket socket;
	private static ObjectEncoderOutputStream objectEncoderOutputStream;
	private static ObjectDecoderInputStream objectDecoderInputStream;

	public static void setAbsMesExchanger(Exchanger<AbstractMessage> absMesExchanger) {
		Network.absMesExchanger = absMesExchanger;
	}

	public static Socket getSocket() {
		return socket;
	}

	public static void setSocket(Socket socket) {
		Network.socket = socket;
	}

	public static ObjectEncoderOutputStream getObjectEncoderOutputStream() {
		return objectEncoderOutputStream;
	}

	public static void setObjectEncoderOutputStream(ObjectEncoderOutputStream objectEncoderOutputStream) {
		Network.objectEncoderOutputStream = objectEncoderOutputStream;
	}

	public static ObjectDecoderInputStream getObjectDecoderInputStream() {
		return objectDecoderInputStream;
	}

	public static void setObjectDecoderInputStream(ObjectDecoderInputStream objectDecoderInputStream) {
		Network.objectDecoderInputStream = objectDecoderInputStream;
	}

	public static Exchanger<AbstractMessage> getAbsMesExchanger() {
		return absMesExchanger;
	}

	public static Exchanger<AuthenticationMessage> getAuthMesExchanger() {
		return authMesExchanger;
	}

	public static void setAuthMesExchanger(Exchanger<AuthenticationMessage> authMesExchanger) {
		Network.authMesExchanger = authMesExchanger;
	}

	public static int getMaxObjSize() {
		return MAX_OBJ_SIZE;
	}

	@Getter
	private static Exchanger<AbstractMessage> absMesExchanger;
	@Getter
	private static Exchanger<AuthenticationMessage> authMesExchanger;

	private static final int MAX_OBJ_SIZE = 100 * 1024 * 1024;

	public static void start() {
		try {
			socket = new Socket("localhost", 8189);
			objectEncoderOutputStream = new ObjectEncoderOutputStream(socket.getOutputStream());
			objectDecoderInputStream = new ObjectDecoderInputStream(socket.getInputStream(), MAX_OBJ_SIZE);
			absMesExchanger = new Exchanger<>();
			authMesExchanger = new Exchanger<>();
			new MessageReceiver(authMesExchanger, absMesExchanger);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void stop() {
		try {
			objectEncoderOutputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			objectDecoderInputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static boolean sendMsg(AbstractMessage msg) {
		try {
			objectEncoderOutputStream.writeObject(msg);
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	public static AbstractMessage readObject() throws ClassNotFoundException, IOException {
		Object object = objectDecoderInputStream.readObject();
		return (AbstractMessage) object;
	}

}
