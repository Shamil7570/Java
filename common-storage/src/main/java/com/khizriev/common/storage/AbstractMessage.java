package com.khizriev.common.storage;

import java.io.Serializable;


/**
 * Корневой абстрактный класс, служащий обобщением всех классов,
 * которые передаются через механизм сериалзации между сервером и клиентом
 */
public abstract class AbstractMessage implements Serializable {

    private static final long serialVersionUID = 1L;

}
