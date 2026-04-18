package my.edu.utar.unwasteable.viewmodel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import my.edu.utar.unwasteable.data.AppDatabase;
import my.edu.utar.unwasteable.data.Product;
import my.edu.utar.unwasteable.data.ProductDao;
import my.edu.utar.unwasteable.data.ProductWithItems;

public class ProductViewModel extends AndroidViewModel {
    private final ProductDao productDao;
    private final LiveData<List<Product>> allProducts;
    private final ExecutorService executorService = Executors.newFixedThreadPool(2);

    public ProductViewModel(Application application) {
        super(application);
        AppDatabase db = AppDatabase.getDatabase(application);
        productDao = db.productDao();
        allProducts = productDao.getProducts();
    }

    public LiveData<List<Product>> getAllProducts() {
        return allProducts;
    }

    public LiveData<List<ProductWithItems>> getProductsWithItems() {
        return productDao.getProductsWithItems();
    }

    public void insert(Product product) {
        executorService.execute(() -> productDao.insert(product));
    }

    public void update(Product product) {
        executorService.execute(() -> productDao.update(product));
    }

    public void delete(Product product) {
        executorService.execute(() -> productDao.delete(product));
    }
}
