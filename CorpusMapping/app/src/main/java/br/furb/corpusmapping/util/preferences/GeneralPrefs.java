package br.furb.corpusmapping.util.preferences;

import android.content.Context;


public class GeneralPrefs extends Prefs {
    private static final String PREFIX = "general_";

    private int lastVersionCode;
    private boolean isAutoUpdateCurrencies;
    private long autoUpdateCurrenciesTimestamp;
    private String lastFileExportPath;
    private int intervalLength;
    private boolean analyticsOptOut;
    private String mainCurrencyCode;

    public GeneralPrefs(Context context) {
        super(context);
        refresh();
    }

    @Override
    protected String getPrefix() {
        return PREFIX;
    }

    public void refresh() {
        lastVersionCode = getInteger("lastVersionCode", 0);
        lastFileExportPath = getString("lastFileExportPath", null);
    }

    public void clear() {
        clear("lastVersionCode", "lastFileExportPath");
        refresh();
        notifyChanged();
    }

    private void notifyChanged() {
    }

    public int getLastVersionCode() {
        return lastVersionCode;
    }

    public void setLastVersionCode(int lastVersionCode) {
        this.lastVersionCode = lastVersionCode;
        setInteger("lastVersionCode", lastVersionCode);
    }


    public String getLastFileExportPath() {
        return lastFileExportPath;
    }

    public void setLastFileExportPath(String lastFileExportPath) {
        this.lastFileExportPath = lastFileExportPath;
        setString("lastFileExportPath", lastFileExportPath);
        notifyChanged();
    }


}
