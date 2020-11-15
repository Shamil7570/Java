package com.khizriev.common.storage;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;


/**
 * Класс для передачи с клиента на сервер необходимой операции
 * с файлом в облачном хранилище
 */
@Getter
@Setter

public class FileOperationsMessage extends AbstractMessage  {

	public static final long serialVersionUID = 1344774173888738704L;


    public enum FileOperation {
        COPY, DELETE, MOVE

    }

    

	public FileOperation getFileOperation() {
		return fileOperation;
	}

	

	public String getFileName() {
		return fileName;
	}

	

	private FileOperation fileOperation;
	private String fileName;
}
