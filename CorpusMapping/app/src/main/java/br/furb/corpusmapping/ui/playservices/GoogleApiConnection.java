package br.furb.corpusmapping.ui.playservices;

import com.google.android.gms.common.api.GoogleApiClient;
import com.squareup.otto.Produce;

import java.util.HashMap;
import java.util.Map;

import br.furb.corpusmapping.util.EventBus;

public class GoogleApiConnection {
    private final EventBus eventBus;
    private final Map<String, GoogleApiClient> googleApiClients = new HashMap<>();
    private static GoogleApiConnection instance;

    private GoogleApiConnection(EventBus eventBus) {
        this.eventBus = eventBus;
      //  eventBus.register(this);
    }

    public static GoogleApiConnection getInstance(){
        if(instance == null){
            instance =new GoogleApiConnection(EventBus.getInstance());
        }
        return instance;
    }

    @Produce
    public GoogleApiConnection produceGoogleApiConnection() {
        return googleApiClients.size() > 0 ? this : null;
    }

    public void put(String clientUniqueId, GoogleApiClient client) {
        if (client != null && (client.isConnected() || client.isConnecting())) {
            googleApiClients.put(clientUniqueId, client);
            eventBus.post(this);
        }
    }

    public void remove(String clientUniqueId) {
        googleApiClients.remove(clientUniqueId);
    }

    public GoogleApiClient get(String clientUniqueId) {
        return googleApiClients.get(clientUniqueId);
    }

    public boolean contains(String clientUniqueId) {
        return googleApiClients.containsKey(clientUniqueId);
    }
}
