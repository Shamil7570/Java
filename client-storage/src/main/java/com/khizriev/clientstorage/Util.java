package com.khizriev.clientstorage;

import javafx.application.Platform;


public class Util {

	public static void fxThreadProcess(Runnable runnable) {
		if (Platform.isFxApplicationThread()) {
			runnable.run();
		} else {
			Platform.runLater(runnable::run);
		}
	
	}

}
