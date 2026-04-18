package my.edu.utar.unwasteable.viewmodel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import my.edu.utar.unwasteable.data.AppDatabase;
import my.edu.utar.unwasteable.data.Category;
import my.edu.utar.unwasteable.data.CategoryDao;

public class CategoryViewModel extends AndroidViewModel {
    private final CategoryDao categoryDao;
    private final LiveData<List<Category>> allCategories;
    private final ExecutorService executorService = Executors.newFixedThreadPool(2);

    public CategoryViewModel(Application application) {
        super(application);
        AppDatabase db = AppDatabase.getDatabase(application);
        categoryDao = db.categoryDao();
        allCategories = categoryDao.getAllCategories();
    }

    public LiveData<List<Category>> getAllCategories() {
        return allCategories;
    }

    public LiveData<Category> getCategoryById(int id) {
        return categoryDao.getCategoryById(id);
    }

    public void insert(Category category) {
        executorService.execute(() -> categoryDao.insert(category));
    }

    public void update(Category category) {
        executorService.execute(() -> categoryDao.update(category));
    }

    public void delete(Category category) {
        executorService.execute(() -> categoryDao.delete(category));
    }
}
