package com.khizriev.server.storage;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import com.khizriev.common.storage.AuthenticationMessage;
import com.khizriev.common.storage.FileMessage;
import com.khizriev.common.storage.FileOperationsMessage;
import com.khizriev.common.storage.FileParametersListMessage;
import com.khizriev.server.storage.chanofauthenrification.CheckPassMiddle;
import com.khizriev.server.storage.chanofauthenrification.LoginExistCheckMiddle;
import com.khizriev.server.storage.chanofauthenrification.MiddleWare;
import  com.khizriev.server.storage.service.AuthenticationFactory;
import  com.khizriev.server.storage.service.AuthenticationService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Comparator;




/**
 * Класс-обработчик всех входящих десериализованных классов-сообщений от клиента
 */
public class MainHandler extends ChannelInboundHandlerAdapter {

	private String userCloudStorage;

	public String getUserCloudStorage() {
		return userCloudStorage;
	}

	public void setUserCloudStorage(String userCloudStorage) {
		this.userCloudStorage = userCloudStorage;
	}

	public AuthenticationService getSqlUsersDaoService() {
		return sqlUsersDaoService;
	}

	public void setSqlUsersDaoService(AuthenticationService sqlUsersDaoService) {
		this.sqlUsersDaoService = sqlUsersDaoService;
	}

	private AuthenticationService sqlUsersDaoService;

	public MainHandler() {
		sqlUsersDaoService = AuthenticationFactory.createAuthenticationService();
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		try {
			if (msg == null) {
				return;
			}
			if (msg instanceof AuthenticationMessage) {
				authMessageHandler(ctx, msg);
			}
			if (msg instanceof FileOperationsMessage) {
				fileOperationMessageHandler(ctx, msg);
			}

			if (msg instanceof FileParametersListMessage) {
				sendListOfFileParameters(ctx);
			}
			if (msg instanceof FileMessage) {
				FileMessage fileMessage = (FileMessage) msg;
				Files.write(Paths.get(userCloudStorage + fileMessage.getFilename()), fileMessage.getData(),
						StandardOpenOption.CREATE);
			}
		} finally {
			ReferenceCountUtil.release(msg);
		}
	}

	private void fileOperationMessageHandler(ChannelHandlerContext ctx, Object msg) throws IOException {
		FileOperationsMessage fileOperationsMessage = (FileOperationsMessage) msg;
		switch (fileOperationsMessage.getFileOperation()) {
		case COPY:
			copyToClientStorage(ctx, fileOperationsMessage);
			break;
		case MOVE:
			copyToClientStorage(ctx, fileOperationsMessage);
			deleteFileFromCloudStorage(ctx, fileOperationsMessage);
			break;
		case DELETE:
			deleteFileFromCloudStorage(ctx, fileOperationsMessage);
			break;
		default:
			break;
		}
	}

	private void authMessageHandler(ChannelHandlerContext ctx, Object msg) {
		AuthenticationMessage authMessage = (AuthenticationMessage) msg;
		switch (authMessage.getAuthCommandType()) {
		case AUTHORIZATION:
			MiddleWare authMiddle = new LoginExistCheckMiddle(sqlUsersDaoService);
			authMiddle.linkWith(new CheckPassMiddle(sqlUsersDaoService));

			if (authMiddle.check(authMessage.getLogin(), authMessage.getPassword())) {
				authMessage.setStatus(true);
				userCloudStorage = authMessage.getLogin() + "Storage/";
				ctx.writeAndFlush(authMessage);
			} else {
				ctx.writeAndFlush(authMessage);
			}
			break;
		case CHANGE_PASS:
			if (sqlUsersDaoService.changePass(authMessage.getLogin(), authMessage.getPassword(),
					authMessage.getNewPassword())) {
				authMessage.setStatus(true);
				ctx.writeAndFlush(authMessage);
			} else {
				ctx.writeAndFlush(authMessage);
			}
			break;
		case DELETE_USER:
			if (sqlUsersDaoService.authentication(authMessage.getLogin(), authMessage.getPassword())) {
				authMessage.setStatus(true);
				sqlUsersDaoService.deleteUserByName(authMessage.getLogin());
				userCloudStorage = authMessage.getLogin() + "Storage/";
				deleteUsersCloudStorage(userCloudStorage);
				ctx.writeAndFlush(authMessage);
			} else {
				ctx.writeAndFlush(authMessage);
			}
			break;
		case REGISTRATION:
			if (sqlUsersDaoService.selectUserByName(authMessage.getLogin()) == null) {
				authMessage.setStatus(true);
				sqlUsersDaoService.insertUser(authMessage.getLogin(), authMessage.getPassword());
				ctx.writeAndFlush(authMessage);
			} else {
				ctx.writeAndFlush(authMessage);
			}
			break;
		default:
			break;
		}
	}

	private void deleteUsersCloudStorage(String userCloudStorage) {
		Path userStoragePath = Paths.get(userCloudStorage);
		try {
			Files.walk(userStoragePath).sorted(Comparator.reverseOrder()).peek(System.out::println).map(Path::toFile)
					.forEach(File::delete);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void deleteFileFromCloudStorage(ChannelHandlerContext ctx, FileOperationsMessage fileOperationsMessage)
			throws IOException {
		Path path = Paths.get(userCloudStorage + fileOperationsMessage.getFileName());
		Files.delete(path);
		sendListOfFileParameters(ctx);
	}

	private void sendListOfFileParameters(ChannelHandlerContext ctx) {
		File directory = new File(userCloudStorage);

		if (!directory.exists()) {
			directory.mkdir();
		}
		FileParametersListMessage fileParametersList = new FileParametersListMessage(userCloudStorage);
		ctx.writeAndFlush(fileParametersList);
	}

	private void copyToClientStorage(ChannelHandlerContext ctx, FileOperationsMessage fileOperationsMessage)
			throws IOException {
		Path path = Paths.get(userCloudStorage + fileOperationsMessage.getFileName());
		FileMessage fileMessage = new FileMessage(path);
		ctx.writeAndFlush(fileMessage);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		cause.printStackTrace();
		ctx.close();
	}
}
