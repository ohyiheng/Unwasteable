package my.edu.utar.unwasteable.viewmodel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import my.edu.utar.unwasteable.data.AppDatabase;
import my.edu.utar.unwasteable.data.Location;
import my.edu.utar.unwasteable.data.LocationDao;

public class LocationViewModel extends AndroidViewModel {
    private final LocationDao locationDao;
    private final LiveData<List<Location>> allLocations;
    private final ExecutorService executorService = Executors.newFixedThreadPool(2);

    public LocationViewModel(Application application) {
        super(application);
        AppDatabase db = AppDatabase.getDatabase(application);
        locationDao = db.locationDao();
        allLocations = locationDao.getAllLocations();
    }

    public LiveData<List<Location>> getAllLocations() {
        return allLocations;
    }

    public LiveData<Location> getLocationById(int id) {
        return locationDao.getLocationById(id);
    }

    public void insert(Location location) {
        executorService.execute(() -> locationDao.insert(location));
    }

    public void update(Location location) {
        executorService.execute(() -> locationDao.update(location));
    }

    public void delete(Location location) {
        executorService.execute(() -> locationDao.delete(location));
    }
}
