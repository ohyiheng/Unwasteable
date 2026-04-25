package my.edu.utar.unwasteable.viewmodel;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import my.edu.utar.unwasteable.data.AppDatabase;
import my.edu.utar.unwasteable.data.Item;
import my.edu.utar.unwasteable.data.ItemDao;
import my.edu.utar.unwasteable.data.ItemLog;
import my.edu.utar.unwasteable.data.ItemLogDao;

public class ItemViewModel extends AndroidViewModel {
    public interface ItemLookupCallback {
        void onResult(Item item);
    }

    private final ItemDao itemDao;
    private final ItemLogDao itemLogDao;

    private final LiveData<List<Item>> allItems;
    private final LiveData<List<ItemLog>> recentLogs;

    private final ExecutorService executorService = Executors.newFixedThreadPool(2);
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    public ItemViewModel(Application application) {
        super(application);

        AppDatabase db = AppDatabase.getDatabase(application);
        itemDao = db.itemDao();
        itemLogDao = db.itemLogDao();

        allItems = itemDao.getItems();
        recentLogs = itemLogDao.getRecentLogs();
    }

    public LiveData<List<Item>> getAllItems() {
        return allItems;
    }

    public LiveData<List<ItemLog>> getRecentLogs() {
        return recentLogs;
    }

    public void insert(Item item) {
        executorService.execute(() -> itemDao.insert(item));
    }

    public void insertWithLog(Item item) {
        executorService.execute(() -> {
            itemDao.insert(item);
            insertLog(item.name, "Added", item.quantity);
        });
    }

    public void update(Item item) {
        executorService.execute(() -> itemDao.update(item));
    }

    public void updateWithLog(Item item, String actionType, double quantityChange) {
        executorService.execute(() -> {
            itemDao.update(item);
            insertLog(item.name, actionType, quantityChange);
        });
    }

    public void delete(Item item) {
        executorService.execute(() -> itemDao.delete(item));
    }

    public void deleteWithLog(Item item) {
        executorService.execute(() -> {
            insertLog(item.name, "Deleted", 0);
            itemDao.delete(item);
        });
    }

    public void findItemByName(String name, ItemLookupCallback callback) {
        executorService.execute(() -> {
            Item item = itemDao.getItemByName(name);
            mainHandler.post(() -> callback.onResult(item));
        });
    }

    public void insertOrUpdateExisting(Item newItem, Item existingItem) {
        if (existingItem == null) {
            insert(newItem);
            return;
        }

        existingItem.quantity = newItem.quantity;
        existingItem.expiryDate = newItem.expiryDate;
        existingItem.locationName = newItem.locationName;
        existingItem.categoryName = newItem.categoryName;

        update(existingItem);
    }

    public void insertOrUpdateExistingWithLog(Item newItem, Item existingItem) {
        executorService.execute(() -> {
            if (existingItem == null) {
                itemDao.insert(newItem);
                insertLog(newItem.name, "Added", newItem.quantity);
                return;
            }

            existingItem.quantity = newItem.quantity;
            existingItem.expiryDate = newItem.expiryDate;
            existingItem.locationName = newItem.locationName;
            existingItem.categoryName = newItem.categoryName;

            itemDao.update(existingItem);
            insertLog(existingItem.name, "Updated", newItem.quantity);
        });
    }

    public void recordManualLog(String itemName, String actionType, double quantityChange) {
        executorService.execute(() -> insertLog(itemName, actionType, quantityChange));
    }

    private void insertLog(String itemName, String actionType, double quantityChange) {
        ItemLog log = new ItemLog();
        log.itemName = itemName;
        log.actionType = actionType;
        log.quantityChange = quantityChange;
        log.timestamp = System.currentTimeMillis();

        itemLogDao.insert(log);
    }
}