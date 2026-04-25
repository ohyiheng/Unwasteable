package my.edu.utar.unwasteable.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ItemLogDao {
    @Insert
    void insert(ItemLog itemLog);

    @Query("SELECT * FROM item_logs ORDER BY timestamp DESC LIMIT 5")
    LiveData<List<ItemLog>> getRecentLogs();

    @Query("DELETE FROM item_logs")
    void clearAll();
}