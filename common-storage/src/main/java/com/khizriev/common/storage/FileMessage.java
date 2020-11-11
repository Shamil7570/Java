package com.khizriev.common.storage;

import lombok.Getter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;


/**
 * Тип сообщения для передачи файла по сети
 */
@Getter
public class FileMessage extends AbstractMessage {	
 
	private static final long serialVersionUID = -3810868633835335417L;

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    private String filename;
    private byte[] data;

    public FileMessage(Path path) throws IOException {
        filename = path.getFileName().toString();
        data = Files.readAllBytes(path);
    }
}
