package br.furb.corpusmapping.util;

import android.os.Handler;
import android.os.Looper;

import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

public class EventBus extends Bus {
    private final Handler handler = new Handler(Looper.getMainLooper());
    private static EventBus instance = null;

    private EventBus() {
        super(ThreadEnforcer.ANY);
    }

    @Override
    public void post(final Object event) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            super.post(event);
        } else {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    EventBus.super.post(event);
                }
            });
        }
    }

    public static synchronized EventBus getInstance(){
        if(instance == null){
            instance = new EventBus();
        }
        return instance;
    }
}
