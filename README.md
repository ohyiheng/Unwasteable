# Unwasteable

Unwasteable is an Android pantry management application designed to help users track food items, quantities, expiry dates, storage locations, and categories using a local Room database.

## Current Data Model

### `items`

| Field | Type | Description |
|---|---|---|
| id | INT | Primary key |
| name | TEXT | Pantry item name |
| quantity | DECIMAL | Current amount remaining |
| expiry_date | DATE | Optional item expiry date |
| location_name | TEXT | Optional storage place, such as fridge, freezer, or pantry shelf |
| category_name | TEXT | Optional food group, such as dairy, frozen food, canned food, or vegetables |

## Current Main Features

- Add pantry items with quantity, expiry date, location, and category.
- Detect duplicate item names and allow users to update the existing item or add another record.
- View all pantry items in a card-based Pantry page.
- Display item status as Fresh, Soon, Expired, Unknown, or Low Stock.
- Search pantry items by name, quantity, expiry date, location, or category.
- Filter pantry items by All, Fresh, Soon, Expired, Unknown, or Low Stock.
- Update item quantity quickly using Use and Add actions.
- Edit existing item details.
- Delete items with confirmation.
- View a Home dashboard summary for total pantry items, expiring soon items, expired items, and items without expiry dates.
- Navigate from Home dashboard cards directly to the matching Pantry filter.
- Retrieve basic food information from the Open Food Facts API through the Food Info Lookup section.
- Use a custom launcher icon for the application.

## External Endpoint Integration

The application connects to the Open Food Facts API in the Add page. Users can enter an item name and tap Check Food Info. The app sends an HTTP GET request to the Open Food Facts endpoint and displays the returned product name, brand, category, and source.

Endpoint used:

```text
https://world.openfoodfacts.org/cgi/search.pl
```

The app includes the Android INTERNET permission in `AndroidManifest.xml` to allow the external API request.

## Custom Launcher Icon

The application uses a custom launcher icon stored as:

```text
app/src/main/res/drawable/unwasteable_icon.jpeg
```

The launcher icon is configured through:

```text
app/src/main/res/mipmap-anydpi-v26/ic_launcher.xml
app/src/main/res/mipmap-anydpi-v26/ic_launcher_round.xml
```

## Technology Stack

- Java
- Android XML layouts
- Room database
- Material Components
- Android Navigation Component
- Bottom Navigation
- Open Food Facts API

## Project Structure

```text
app/src/main/java/my/edu/utar/unwasteable/
├── MainActivity.java
├── data/
│   ├── AppDatabase.java
│   ├── DateConverters.java
│   ├── Item.java
│   └── ItemDao.java
├── ui/
│   ├── AddFragment.java
│   ├── HomeFragment.java
│   ├── ItemAdapter.java
│   └── ItemListFragment.java
└── viewmodel/
    └── ItemViewModel.java
```
