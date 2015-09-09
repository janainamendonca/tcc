package br.furb.corpusmapping;

import android.app.Application;

/**
 * Created by Janaina on 06/09/2015.
 */
public class CorpusMappingApp extends Application {
    private static CorpusMappingApp instance;

    private long selectedPatientId;

    public static CorpusMappingApp getInstance() {
        // TODO não deveria ser necessário fazer isso
        if (instance == null) {
            instance = new CorpusMappingApp();
        }
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public long getSelectedPatientId() {
        return selectedPatientId;
    }

    public void setSelectedPatientId(long selectedPatientId) {
        this.selectedPatientId = selectedPatientId;
    }
}