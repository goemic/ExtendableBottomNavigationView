package de.goemic.extendablebottomnavigationview.sample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import de.goemic.ExtendableBottomNavigationView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        val bottomNavigation = findViewById<ExtendableBottomNavigationView>(R.id.bottomNavigation)

        //
        // init items
        //

        // @MenuRes can be set from code, if not set from layout.xml
//        bottomNavigation.inflateMenu(R.menu.menu_bottom_navigation)

        // the item list can as well be generated / set manually
//        bottomNavigation.setNavigationItemList(generateItemList())


        //
        // Listener
        //
        bottomNavigation.setOnItemSelectedListener { item ->

            findViewById<TextView>(R.id.textView).text = item.title

            return@setOnItemSelectedListener true
        }

//        bottomNavigation.onItemSelectedListener =
//            ExtendableBottomNavigationView.OnItemSelectedListener { item ->
//                // ..
//                true
//            }


        bottomNavigation.setOnItemReelectedListener { item ->
            Toast.makeText(this, "reselected: ${item.title}", Toast.LENGTH_SHORT).show()
        }

//        bottomNavigation.onItemReselectedListener =
//            ExtendableBottomNavigationView.OnItemReselectedListener {
//                // ..
//            }

    }


    /**
     * This method shows how an item list can be generated manually. In this example with @IdRes.
     *
     * @return dummy item list
     */
    private fun generateItemList(): List<ExtendableBottomNavigationView.Item> {
        val itemList = arrayListOf<ExtendableBottomNavigationView.Item>()

        itemList.add(
            ExtendableBottomNavigationView.Item(
                this,
                R.id.navigation_1,
                "item1",
                R.drawable.ic_filter_1_black_24dp
            )
        )
        itemList.add(
            ExtendableBottomNavigationView.Item(
                this,
                R.id.navigation_2,
                "item2",
                R.drawable.ic_filter_2_black_24dp
            )
        )
        itemList.add(
            ExtendableBottomNavigationView.Item(
                this,
                R.id.navigation_3,
                "item3",
                R.drawable.ic_filter_3_black_24dp
            )
        )
        itemList.add(
            ExtendableBottomNavigationView.Item(
                this,
                R.id.navigation_4,
                "item4",
                R.drawable.ic_filter_4_black_24dp
            )
        )
        itemList.add(
            ExtendableBottomNavigationView.Item(
                this,
                R.id.navigation_5,
                "item5",
                R.drawable.ic_filter_5_black_24dp
            )
        )
        itemList.add(
            ExtendableBottomNavigationView.Item(
                this,
                R.id.navigation_6,
                "item6",
                R.drawable.ic_filter_6_black_24dp
            )
        )
        itemList.add(
            ExtendableBottomNavigationView.Item(
                this,
                R.id.navigation_7,
                "item7",
                R.drawable.ic_filter_7_black_24dp
            )
        )

        return itemList
    }
}