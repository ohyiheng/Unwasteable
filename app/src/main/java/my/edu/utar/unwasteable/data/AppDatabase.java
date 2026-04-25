package my.edu.utar.unwasteable.data;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(
        entities = {
                Item.class,
                ItemLog.class
        },
        version = 5
)
@TypeConverters({DateConverters.class})
public abstract class AppDatabase extends RoomDatabase {
    private static volatile AppDatabase Instance;

    private static final Migration MIGRATION_4_5 = new Migration(4, 5) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL(
                    "CREATE TABLE IF NOT EXISTS item_logs (" +
                            "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                            "item_name TEXT, " +
                            "action_type TEXT, " +
                            "quantity_change REAL NOT NULL, " +
                            "timestamp INTEGER NOT NULL" +
                            ")"
            );
        }
    };

    public static AppDatabase getDatabase(final Context context) {
        if (Instance != null) return Instance;

        synchronized (AppDatabase.class) {
            if (Instance == null) {
                Instance = Room.databaseBuilder(
                                context.getApplicationContext(),
                                AppDatabase.class,
                                "unwasteable_app_db"
                        )
                        .addMigrations(MIGRATION_4_5)
                        .fallbackToDestructiveMigration(true)
                        .build();
            }
            return Instance;
        }
    }

    public abstract ItemDao itemDao();

    public abstract ItemLogDao itemLogDao();
}