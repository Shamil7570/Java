package com.khizriev.server.storage.chanofauthenrification;

/**
 * MiddleWare
 */
public abstract class MiddleWare {

    private MiddleWare next;

    public MiddleWare linkWith(MiddleWare next) {
        this.next = next;
        return next;
    }

    public abstract boolean check(String login, String password);

    protected boolean checkNext(String login, String password) {
        if(this.next == null) {
            return true;
        }
        return next.check(login, password);
    }
}