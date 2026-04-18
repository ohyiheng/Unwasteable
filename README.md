# Unwasteable

## Entity-relationship model

1. **`items`**

| Field       | Type    | Description                                         |
|-------------|---------|-----------------------------------------------------|
| id          | INT     | Primary Key                                         |
| location_id | INT     | Link to Locations (Main Fridge, Basement Freezer)   |
| category_id | INT     | Link to Categories (Fruits, Vegetables, Meat)       |
| quantity    | DECIMAL | Current amount remaining                             |
| expiry_date | DATE    | Critical for notification logic                     |

2. **`locations`**

| Field | Type | Description                             |
|-------| ---- |-----------------------------------------|
| id    | INT | Primary Key                             |
| name  | VARCHAR | "Main Fridge", "Basement Freezer", etc. |

3. **`categories`**

| Field | Type | Description                                         |
|-------| ---- |-----------------------------------------------------|
| id    | INT | Primary Key                                         |
| name  | VARCHAR | "Dairy", "Canned Goods", "Frozen Foods", etc. |