package by.matveev.rorty.core;

import by.matveev.rorty.entities.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Pools;
import com.badlogic.gdx.utils.Queue;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class EventQueue {

    private static final EventQueue instance = new EventQueue();
    private final Map<String, Queue<Event>> events = new LinkedHashMap<>();

    private EventQueue() {
    }

    public static void add(Event event) {
        final String receiver = event.receiver;
        if (receiver == null) {
            Gdx.app.log(EventQueue.class.getSimpleName(),
                    "Could not add event, receiver is null");
            return;
        }

        if (!event.validate()) {
            Gdx.app.log(EventQueue.class.getSimpleName(),
                    "Could not add event, data is not valid");
            return;
        }
        Queue<Event> queue = instance.events.get(receiver);
        if (queue == null) {
            queue = new Queue<>();
            instance.events.put(receiver, queue);
        }
        queue.addLast(event);
    }

    public static void dispatch(List<Entity> entities) {
        for (Entity e : entities) {
            final Queue<Event> events = instance.events.get(e.name);
            if (events != null) {
                while (events.size != 0) {
                    final Event event = events.removeFirst();
                    e.onEvent(event);
                    Pools.free(event);
                }
            }
        }
    }


    public void reset() {
        events.clear();
    }
}
