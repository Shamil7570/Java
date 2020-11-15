package com.khizriev.clientstorage.fxcontroller.fxcontroller;

import com.khizriev.clientstorage.MessageReceiver;
import com.khizriev.clientstorage.Network;
import com.khizriev.common.storage.*;
import com.khizriev.common.storage.FileOperationsMessage.FileOperation;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;


import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ResourceBundle;


public class StoragePanelFxController implements Initializable {
	
	private static final String  CLIENT_STORAGE = "client_storage/";

	@FXML
	private VBox rootPane;
	@FXML
	private TableView<FileParameters> localTable;
	@FXML
	private TableView<FileParameters> cloudTable;
	
	private FileChooser fileChooser = new FileChooser();

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		initializeTables(localTable);
		initializeTables(cloudTable);
		refreshLocalFileTable();
		createReceiveMessageThread();
		requestCloudFileList();
	}

	private void refreshLocalFileTable() {
		refreshTableEntries(localTable, new FileParametersListMessage(CLIENT_STORAGE));
	}

	private void refreshTableEntries(TableView<FileParameters> table, FileParametersListMessage fileParametersList) {
		if (Platform.isFxApplicationThread()) {
			table.getItems().clear();
			fileParametersList.getFileParameterList().forEach(e -> table.getItems().add(e));
		} else {
			Platform.runLater(() -> {
				table.getItems().clear();
				fileParametersList.getFileParameterList().forEach(e -> table.getItems().add(e));
			});
		}
	}

	@SuppressWarnings("unchecked")
	private void initializeTables(TableView<FileParameters> table) {
		TableColumn<FileParameters, String> localNameColumn = new TableColumn<>("Name");
		localNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
		localNameColumn.setPrefWidth(270);
		TableColumn<FileParameters, Integer> localSizeColumn = new TableColumn<>("Size");
		localSizeColumn.setCellValueFactory(new PropertyValueFactory<>("size"));
		localSizeColumn.setPrefWidth(85);
		TableColumn<FileParameters, String> localDateColumn = new TableColumn<>("Date");
		localDateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
		localDateColumn.setPrefWidth(140);
		table.getColumns().addAll(localNameColumn, localSizeColumn, localDateColumn);

	}

	/**
	 * Метод создает поток, который обменивается и обрабатывает сообщения
	 *  типа {@link AbstractMessage} из {@link MessageReceiver}
	 */
	private void createReceiveMessageThread() {
		Thread t = new Thread(() -> {
			try {
				while (true) {
					AbstractMessage msg = Network.getAbsMesExchanger().exchange(null);
					if (msg instanceof FileParametersListMessage) {
						FileParametersListMessage fileParametersList = (FileParametersListMessage) msg;
						refreshTableEntries(cloudTable, fileParametersList);
					}
					if(msg instanceof FileMessage) {
						FileMessage fileMessage = (FileMessage) msg;
						Files.write(Paths.get(CLIENT_STORAGE + fileMessage.getFilename()), fileMessage.getData(), StandardOpenOption.CREATE);
						refreshLocalFileTable();												
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} finally {
				Network.stop();
			}
		}, "StorageMsgReceiver");
		t.setDaemon(true);
		t.start();
	}

	
	private void requestCloudFileList() {
		Network.sendMsg(new FileParametersListMessage());
	}

	@FXML
	private Path copyFileToCloud() {
		FileParameters focusedFileLine = localTable.getSelectionModel().getSelectedItem();
		if(focusedFileLine == null) return null;
		Path path = Paths.get(CLIENT_STORAGE + focusedFileLine.getName());
		if (Files.exists(path)) {
			try {
				FileMessage fileMessage = new FileMessage(path);
				Network.sendMsg(fileMessage);
				requestCloudFileList();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return path;
	}
	
	@FXML
	private void copyFileFromCloud() {
		FileParameters focusedFileLine = cloudTable.getSelectionModel().getSelectedItem();
		if(focusedFileLine == null) return;
		FileOperationsMessage fileOperationsMessage = new FileOperationsMessage(FileOperation.COPY, focusedFileLine.getName());
		Network.sendMsg(fileOperationsMessage);
	}
	
	@FXML
	private void moveFileToCloud() {
		try {
			Path path = copyFileToCloud();
			if(path == null) return;
			Files.delete(path);
			refreshLocalFileTable();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@FXML
	private void moveFileFromClod() {
		FileParameters focusedFileLine = cloudTable.getSelectionModel().getSelectedItem();
		if(focusedFileLine == null) return;
		FileOperationsMessage fileOperationsMessage = new FileOperationsMessage(FileOperation.MOVE, focusedFileLine.getName());
		Network.sendMsg(fileOperationsMessage);
	}
	
	@FXML
	private void deleteLocalFile() {
		FileParameters focusedFileLine = localTable.getSelectionModel().getSelectedItem();
		if(focusedFileLine == null) return;
		Path path = Paths.get(CLIENT_STORAGE + focusedFileLine.getName());
		if (Files.exists(path)) {
			try {
				Files.delete(path);
				refreshLocalFileTable();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	@FXML
	private void deleteFileFromCloud() {
		FileParameters focusedFileLine = cloudTable.getSelectionModel().getSelectedItem();
		if(focusedFileLine == null) return;
		FileOperationsMessage fileOperationsMessage = new FileOperationsMessage(FileOperation.DELETE, focusedFileLine.getName());
		Network.sendMsg(fileOperationsMessage);
	}
	
	@FXML
	private void uploadFileFormOs() {	
		Window window = this.rootPane.getScene().getWindow();
		File file =  fileChooser.showOpenDialog(window);
		try {
			byte[] data = Files.readAllBytes(file.toPath());
			Files.write(Paths.get(CLIENT_STORAGE + file.getName()), data, StandardOpenOption.CREATE);
			refreshLocalFileTable();	
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@FXML
	private void downloadFileToOs() {
		Window window = this.rootPane.getScene().getWindow();
		FileParameters focusedFileLine = localTable.getSelectionModel().getSelectedItem();
		Path locStoragePath = Paths.get(CLIENT_STORAGE + focusedFileLine.getName());
		try {
			fileChooser.setInitialFileName(focusedFileLine.getName());
			File file = fileChooser.showSaveDialog(window);
			Path osPath = file.toPath();
			Files.copy(locStoragePath, osPath);
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}
	
	@FXML
	private void exitToSingUp() {
		Stage primaryStage = (Stage) this.rootPane.getScene().getWindow();
		Network.start();
		primaryStage.close();		
	}
	
	@FXML
	private void help() {
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
	    alert.setTitle("Help");
	    alert.setHeaderText("Инструкция");
	    alert.setContentText("Приложение состоит из двух основных панелей: \n"
	    		+ "local storage - локальное хранилище находящееся на ПК пользователя; \n"
	    		+ "cloud storage - облачное хранилище располагающегося на сервере. \n"
	    		+ "Кнопки copy, move, delete над каждой панели служат для соответствующих"
	    		+ "файловых операций по отношению к выбранному файлу текущего хранилища. \n"
	    		+ "С помощью меню File можно обмениваться файлами между локальным хранилищем и ОС: \n"
	    		+ "upload - загружает файл из ОС в локальное хранилище \n"
	    		+ "download  - скачивает выбранный в локальном хранилище файл \n");
	    alert.showAndWait();
	}
	
	@FXML
	private void about() {
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
	    alert.setTitle("About");
	    alert.setHeaderText("Information");
	    alert.setContentText("MyCloud Store v1.0");
	    alert.showAndWait();
	}

	public void setRootPane(VBox rootPane) {
		this.rootPane = rootPane;
	}
}
