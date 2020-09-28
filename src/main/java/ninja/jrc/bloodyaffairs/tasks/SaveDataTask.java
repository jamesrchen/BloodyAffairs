package ninja.jrc.bloodyaffairs.tasks;

import ninja.jrc.bloodyaffairs.managers.PersistentDataManager;

public class SaveDataTask implements Runnable {
    private final PersistentDataManager persistentDataManager;

    public SaveDataTask(PersistentDataManager persistentDataManager){
        this.persistentDataManager = persistentDataManager;
    }

    @Override
    public void run() {
        this.persistentDataManager.saveData();
    }
}