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

public class ItemViewModel extends AndroidViewModel {
    public interface ItemLookupCallback {
        void onResult(Item item);
    }

    private final ItemDao itemDao;
    private final LiveData<List<Item>> allItems;
    private final ExecutorService executorService = Executors.newFixedThreadPool(2);
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    public ItemViewModel(Application application) {
        super(application);
        AppDatabase db = AppDatabase.getDatabase(application);
        itemDao = db.itemDao();
        allItems = itemDao.getItems();
    }

    public LiveData<List<Item>> getAllItems() {
        return allItems;
    }

    public void insert(Item item) {
        executorService.execute(() -> itemDao.insert(item));
    }

    public void update(Item item) {
        executorService.execute(() -> itemDao.update(item));
    }

    public void delete(Item item) {
        executorService.execute(() -> itemDao.delete(item));
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
}