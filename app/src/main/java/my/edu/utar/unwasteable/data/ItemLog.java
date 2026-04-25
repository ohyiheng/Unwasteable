package my.edu.utar.unwasteable.data;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "item_logs")
public class ItemLog {
    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "item_name")
    public String itemName;

    @ColumnInfo(name = "action_type")
    public String actionType;

    @ColumnInfo(name = "quantity_change")
    public double quantityChange;

    public long timestamp;
}