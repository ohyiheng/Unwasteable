package my.edu.utar.unwasteable.data;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.List;

public class ProductWithItems {
    @Embedded public Product product;
    @Relation(
        parentColumn = "id",
        entityColumn = "product_id"
    )
    public List<Item> items;
}
