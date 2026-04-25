# Unwasteable

Unwasteable is an Android pantry management application designed to help users track food items, quantities, expiry dates, storage locations, and categories in a simple local database.

## Current Data Model

### `items`

| Field | Type | Description |
|---|---|---|
| id | INT | Primary key |
| name | TEXT | Pantry item name |
| quantity | DECIMAL | Current amount remaining |
| expiry_date | DATE | Item expiry date |
| location_name | TEXT | Optional storage place, such as fridge, freezer, or pantry shelf |
| category_name | TEXT | Optional food group, such as dairy, frozen food, canned food, or vegetables |

## Current Main Features

- Add pantry items with quantity, expiry date, location, and category.
- View all pantry items in a card-based Pantry page.
- Display item status as Fresh, Soon, Expired, or Unknown.
- Search pantry items by name, quantity, expiry date, location, or category.
- Edit existing item details.
- Delete items with confirmation.
- View a dashboard summary on the Home page.
