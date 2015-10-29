package br.furb.corpusmapping.data.backup;

import br.furb.corpusmapping.util.EventBus;
import br.furb.corpusmapping.util.IOUtils;
import br.furb.corpusmapping.util.errors.ImportError;

public class DataImporterRunnable implements Runnable {
    private final EventBus eventBus;
    private final DataImporter dataImporter;

    public DataImporterRunnable(EventBus eventBus, DataImporter dataImporter) {
        this.eventBus = eventBus;
        this.dataImporter = dataImporter;
    }

    @Override public void run() {
        try {
            dataImporter.importData();
            eventBus.post(dataImporter);
        } catch (Exception e) {
            e.printStackTrace();
            final ImportError error = new ImportError("A importação dos dados falhou. " + e.getMessage(), e);
           // Crashlytics.logException(error);
            eventBus.post(error);
        } finally {
            IOUtils.closeQuietly(dataImporter);
        }
    }
}
