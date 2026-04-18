# Unwasteable

## Entity-relationship model

1. **`products`**

| Field        | Type    | Description                                          |
|--------------|---------|------------------------------------------------------|
| id           | INT     | Primary Key                                          |
| name         | VARCHAR | Generic name (e.g., "Whole Milk")                    |
| brand        | VARCHAR | 	Manufacturer name                                   |
| category_id  | FK	     | Link to Categories table (Dairy, Canned Goods, etc.) |
| default_unit | ENUM	   | grams, liters, ounces, units                         |

2. **`items`**

| Field       | Type    | Description                                         |
|-------------|---------|-----------------------------------------------------|
| id          | INT     | Primary Key                                         |
| product_id  | INT     | Link to the master Product                          |
| location_id | INT     | Link to Locations (Main Fridge, Basement Freezer)   |
| quantity    | DECIMAL | Current amount remaining                             |
| expiry_date | DATE    | Critical for notification logic                     |
| opened_date | DATE    | To track shelf life after breaking the seal         |
| status      | ENUM    | Unopened, Opened, Expired, Depleted                 |

3. **`locations`**

| Field | Type | Description                             |
|-------| ---- |-----------------------------------------|
| id    | INT | Primary Key                             |
| name  | VARCHAR | "Main Fridge", "Basement Freezer", etc. |

4. **`categories`**

| Field | Type | Description                                         |
|-------| ---- |-----------------------------------------------------|
| id    | INT | Primary Key                                         |
| name  | VARCHAR | "Dairy", "Canned Goods", "Frozen Foods", etc. |