package my.edu.utar.unwasteable.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(
    entities = {
        Category.class,
        Location.class,
        Item.class,
        Product.class,
    },
    version = 1
)
public abstract class AppDatabase extends RoomDatabase {
    private static volatile AppDatabase Instance;

    public static AppDatabase getDatabase(final Context context) {
        if (Instance != null) return Instance;

        synchronized (AppDatabase.class) {
            if (Instance == null) {
                Instance = Room.databaseBuilder(
                    context.getApplicationContext(),
                    AppDatabase.class,
                    "unwasteable_app_db"
                ).build();
            }
            return Instance;
        }
    }

    public abstract CategoryDao categoryDao();
    public abstract LocationDao locationDao();
    public abstract ItemDao itemDao();
    public abstract ProductDao productDao();
}
