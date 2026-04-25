package my.edu.utar.unwasteable.data;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.time.LocalDate;

@Entity(tableName = "items")
public class Item {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String name;

    @ColumnInfo(name = "location_name")
    public String locationName;

    @ColumnInfo(name = "category_name")
    public String categoryName;

    public double quantity;

    @ColumnInfo(name = "expiry_date")
    public LocalDate expiryDate;
}