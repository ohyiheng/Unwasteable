package my.edu.utar.unwasteable.data;

import androidx.room.ColumnInfo;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.room.Relation;

import java.util.List;

@Entity(
    tableName = "products",
    foreignKeys = @ForeignKey(
        entity = Category.class,
        parentColumns = "id",
        childColumns = "category_id",
        onDelete = ForeignKey.SET_NULL
    ),
    indices = {@Index("category_id")}
)
public class Product {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String name;
    public String brand;

    @ColumnInfo(name = "category_id")
    public int categoryId;

    @ColumnInfo(name = "default_unit")
    public String defaultUnit;
}

