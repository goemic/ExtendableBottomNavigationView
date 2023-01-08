![JitPack](https://img.shields.io/jitpack/v/github/goemic/ExtendableBottomNavigationView) ![GitHub repo size](https://img.shields.io/github/repo-size/goemic/ExtendableBottomNavigationView)

# ExtendableBottomNavigationView
A very small and lightweight implementation of a BottomNavigationView for android which provides the possibility to use more than 5 items on a smartphone. 
If more then 5 items are available, an "more button" is added automatically. Clicking the "more button" scrolls the other items onto the screen. (video)
For up to 5 items it behaves like a normal BottomNavigationView

[ex_bottom_navigation.webm](https://user-images.githubusercontent.com/16276869/179368385-4e862ebb-38c3-4ebe-b33b-bad4a36f621e.webm)

# How to use

## Add this to your project's (root) build.gradle:

```
allprojects {
    repositories {
        maven { url "https://jitpack.io" }
    }
}
```

..and this to your app/module level build.gradle:

```
dependencies {
    implementation 'com.github.goemic:ExtendableBottomNavigationView:{latest version}'
}
```

## Setup ExtendableBottomNavigationView
You can specify a menu resource file where you define your bottom navigation items:
Create a menu.xml in your res/menu/ folder:

```
<?xml version="1.0" encoding="utf-8"?>
<menu xmlns:android="http://schemas.android.com/apk/res/android">
    <item
        android:id="@+id/item_1"
        android:title="Item 1"
        android:icon="@drawable/ic_filter_1_black_24dp"/>
    <item
        android:id="@+id/item_2"
        android:title="@string/navigation_item_2_title"
        android:icon="@drawable/ic_filter_2_black_24dp" />
    <item
        android:id="@+id/item_3"
        android:title="@string/navigation_item_3_title"
        android:icon="@drawable/ic_filter_3_black_24dp" />
    <item
        android:id="@+id/item_4"
        android:title="@string/navigation_item_4_title"
        android:icon="@drawable/ic_filter_4_black_24dp" />
    <item
        android:id="@+id/item_5"
        android:title="@string/navigation_item_5_title"
        android:icon="@drawable/ic_filter_5_black_24dp" />
    <item
        android:id="@+id/item_6"
        android:title="@string/navigation_item_6_title"
        android:icon="@drawable/ic_filter_6_black_24dp" />
</menu>
```

Then add ExtendableBottomNavigationView to your Fragment's/Activity's layout file with the created menu:
```
    <de.goemic.ExtendableBottomNavigationView
        android:id="@+id/bottomNavigation"
        android:layout_width="match_parent"
        android:layout_height="60dp"
        android:layout_alignParentBottom="true"
        app:menu="@menu/menu_bottom_navigation"/>        
```

..alternatively @MenuRes can be set from code, if not set from layout.xml
```
    val bottomNavigation = findViewById<ExtendableBottomNavigationView>(R.id.bottomNavigation)
    bottomNavigation.inflateMenu(R.menu.menu_bottom_navigation)
```

You can also add init/add/remove/replace/update the items dynamically:
```
    bottomNavigation.setNavigationItemList(..)
```

## Add listener
ExtendableBottomNavigationView has two types of listener: OnItemSelectedListener and OnItemReelectedListener.

For ease of use you can either set them via property access or setter method.

### OnItemSelectedListener
```
    bottomNavigation.onItemSelectedListener =
        ExtendableBottomNavigationView.OnItemSelectedListener { item ->
            // do your stuff here

            return@setOnItemSelectedListener
        }
```
..or..
```
    bottomNavigation.setOnItemSelectedListener { item ->
        // do your stuff here
        
        return@setOnItemSelectedListener true
    }
```


### OnItemReselectedListener
```
    bottomNavigation.onItemReselectedListener =
        ExtendableBottomNavigationView.OnItemReselectedListener {
            // do your stuff here
        }
```
..or..
```
    bottomNavigation.setOnItemReelectedListener {
        // do your stuff here
    }
```

# Roadmap
- Add CI with UI-tests
- Add alternative "more menu" option with a PopupMenu
