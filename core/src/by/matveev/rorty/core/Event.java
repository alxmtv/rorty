package by.matveev.rorty.core;

import com.badlogic.gdx.utils.Pool;

public abstract class Event implements Pool.Poolable {

    public String sender;
    public String receiver;

    public Event() {
    }

    public Event setSender(String sender) {
        this.sender = sender;
        return this;
    }

    public Event setReceiver(String receiver) {
        this.receiver = receiver;
        return this;
    }

    @Override
    public void reset() {
        this.sender = null;
        this.receiver = null;
    }

    public abstract boolean validate();
}
