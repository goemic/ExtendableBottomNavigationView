package de.goemic

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.content.res.Configuration
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.Log
import android.view.*
import android.view.View.OnClickListener
import android.widget.*
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.children
import androidx.core.view.doOnLayout
import androidx.core.view.doOnNextLayout
import androidx.core.view.forEach
import de.goemic.extendablebottomnavigationview.R

@Suppress("unused")
class ExtendableBottomNavigationView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr) {

    private val maxItemsOnSmartphone = 5

    private val itemList: MutableList<Item> = mutableListOf()
    private var isTablet = false
    private var moreItemsContainerWidth = -1

    var onItemSelectedListener: OnItemSelectedListener? = null
    var onItemReselectedListener: OnItemReselectedListener? = null

    var selectedItemId: Int = -1
        private set

    // container views
    private val mainItemsContainer: LinearLayout
    private val moreItemsContainer: LinearLayout

    // animation related
    private var isAnimating = false
    private var startHideAnimation = false
    private var startShowAnimation = false

    //
    // constructor / init
    //

    init {
        val rootView = inflate(context, R.layout.extendable_bottom_navigation, this)
        mainItemsContainer = rootView.findViewById(R.id.container_defaultItems)
        moreItemsContainer = rootView.findViewById(R.id.container_moreItems)
        isTablet = context.resources.getBoolean(R.bool.isTabletOrLandscape)

        updateMoreItemsContainerBounds()

        // start reading styled attributes
        val typedArray =
            context.obtainStyledAttributes(attrs, R.styleable.ExtendableBottomNavigationView)

        val menuRes = typedArray.getResourceId(
            R.styleable.ExtendableBottomNavigationView_menu,
            -1
        )

        typedArray.recycle()

        doOnNextLayout {
            if (menuRes != -1) {
                // this should be called AFTER more items container is properly setup to avoid race conditions
                inflateMenu(menuRes)
            }
        }
    }


    //
    // private helper methods
    //

    private fun updateMoreItemsContainerBounds() {

        mainItemsContainer.doOnLayout {

            // calculate width of moreItemsContainer (offscreen)
            moreItemsContainerWidth = (mainItemsContainer.width
                .toFloat() / mainItemsContainer.childCount
                .toFloat()).toInt() * moreItemsContainer.childCount

            // we can't update layout params more than once on the same UI thread, therefore post it..
            moreItemsContainer.post {
                val layoutParams = moreItemsContainer.layoutParams as LayoutParams
                layoutParams.width = moreItemsContainerWidth
                layoutParams.height = LayoutParams.MATCH_PARENT
                moreItemsContainer.layoutParams = layoutParams
            }

            moreItemsContainer.translationX = moreItemsContainerWidth.toFloat()
            moreItemsContainer.visibility = VISIBLE
        }
    }


    private fun updateItemViewSelectionState(itemView: View, selectedItemId: Int) {
        val isSelectedItem = selectedItemId == itemView.id

        val viewHolder = itemView.tag as ViewHolder
        viewHolder.imageView.isSelected = isSelectedItem
        viewHolder.textView.isSelected = isSelectedItem

        itemView.tag = viewHolder
    }

    private fun inflateChildView(
        inflater: LayoutInflater,
        item: Item,
        container: LinearLayout
    ) {
        val childView = inflater.inflate(
            R.layout.extendable_bottom_navigation_item,
            container,
            false
        )

        val viewHolder = ViewHolder(
            textView = childView.findViewById(R.id.tv_text),
            imageView = childView.findViewById(R.id.iv_icon),
        )

        viewHolder.textView.text = item.title
        viewHolder.imageView.setImageDrawable(item.icon)

        childView.id = item.id
        childView.tag = viewHolder
        childView.setOnClickListener(onItemClickListener)

        container.addView(childView)
    }

    //
    // public methods
    //

    fun setOnItemSelectedListener(listener: (item: Item) -> Boolean) {
        onItemSelectedListener =
            OnItemSelectedListener { item -> listener.invoke(item) }
    }

    fun setOnItemReelectedListener(listener: (item: Item) -> Unit) {
        this.onItemReselectedListener =
            OnItemReselectedListener { item -> listener.invoke(item) }
    }

    @Suppress("MemberVisibilityCanBePrivate")
    fun inflateMenu(menuRes: Int) {
        val popupMenu = PopupMenu(context, null)
        val menu = popupMenu.menu
        MenuInflater(context).inflate(menuRes, menu)

        val list = mutableListOf<Item>()
        menu.children.forEach {
            list.add(
                Item(
                    it.itemId,
                    it.title.toString(),
                    it.icon,
                )
            )
        }

        setNavigationItemList(list)
    }


    @Suppress("MemberVisibilityCanBePrivate")
    fun selectItem(id: Int) {
        val view = findViewById<View>(id)
        if (view == null) {
            Log.e(
                ExtendableBottomNavigationView::class.java.simpleName,
                "id is not present in ExtendableBottomNavigation"
            )
            return
        }

        itemList.singleOrNull { it.id == id }?.let {

            // reselection
            if (this.selectedItemId == id) {
                onItemReselectedListener?.onNavigationItemReselected(it)
                return
            }

            // selection
            if (onItemSelectedListener?.onNavigationItemSelected(it) == true) {
                this.selectedItemId = id

                // de-/select items in MAIN container
                mainItemsContainer.forEach { itemView ->
                    updateItemViewSelectionState(itemView, id)
                }

                // de-/select items in MORE container
                moreItemsContainer.forEach { itemView ->
                    updateItemViewSelectionState(itemView, id)
                }
            }
        }
    }

    fun getNavigationItemList(): List<Item> {
        return itemList
    }

    @Suppress("MemberVisibilityCanBePrivate")
    fun setNavigationItemList(itemList: List<Item>) {
        this.itemList.clear()
        this.itemList.addAll(itemList)

        val orientation = context.resources.configuration.orientation
        val inflater = LayoutInflater.from(context)

        moreItemsContainer.removeAllViews()
        mainItemsContainer.removeAllViews()

        if (orientation == Configuration.ORIENTATION_LANDSCAPE || isTablet || itemList.size <= maxItemsOnSmartphone) {
            this.itemList.forEach { item ->
                inflateChildView(inflater, item, mainItemsContainer)
            }
        } else {
            // add more icon to at max_item position
            this.itemList.add(
                maxItemsOnSmartphone - 1,
                Item(
                    context,
                    R.id.navigation_more,
                    context.getString(R.string.title_more),
                    R.drawable.navigation_more_selectable
                )
            )

            this.itemList.forEachIndexed { index, item ->
                if (index < maxItemsOnSmartphone) {
                    inflateChildView(inflater, item, mainItemsContainer)
                } else {
                    //to set measured width..
                    inflateChildView(inflater, item, moreItemsContainer)
                }
            }
        }

        updateMoreItemsContainerBounds()
    }

    private fun startMoreAnimation() {
        val view = findViewById<View>(R.id.navigation_more)
        if (!isAnimating) {
            view.isActivated = !view.isActivated
            val translateByX =
                if (view.isActivated)
                    -moreItemsContainerWidth
                else
                    moreItemsContainerWidth

            (view.tag as ViewHolder).let {
                it.imageView.isActivated = view.isActivated
                it.textView.text =
                    if (view.isActivated) {
                        context.getString(R.string.title_less)
                    } else {
                        context.getString(R.string.title_more)
                    }
            }

            mainItemsContainer.animate()
                .translationXBy(translateByX.toFloat())
                .start()

            moreItemsContainer.animate()
                .translationXBy(translateByX.toFloat())
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationStart(animation: Animator) {
                        isAnimating = true
                    }

                    override fun onAnimationEnd(animation: Animator) {
                        isAnimating = false
                        if (startShowAnimation) {
                            startMoreAnimation()
                        } else if (startHideAnimation) {
                            startMoreAnimation()
                        }
                        startShowAnimation = false
                        startHideAnimation = false
                    }
                })
                .start()
        } else {
            if (view.isActivated) {
                startHideAnimation = true
            } else {
                startShowAnimation = true
            }
        }
    }

    //
    // Listener
    //

    private val onItemClickListener = OnClickListener { view: View? ->
        if (view == null || view.tag == null) {
            return@OnClickListener
        }

        if (view.id == R.id.navigation_more) {
            startMoreAnimation()
        } else {
            selectItem(view.id)
        }
    }


    //
    // inner interface and classes
    //

    private inner class ViewHolder(
        val textView: TextView,
        val imageView: ImageView,
    )


    fun interface OnItemSelectedListener {
        /**
         * Called when an item in the navigation menu is selected.
         *
         * @param item Item: The selected item
         * @return true to display the item as the selected item and false if the item should not be selected. Consider setting non-selectable items as disabled preemptively to make them appear non-interactive.
         */
        fun onNavigationItemSelected(item: Item) : Boolean
    }

    fun interface OnItemReselectedListener {
        /**
         * Called when the currently selected item in the navigation menu is selected again.
         *
         * @param item Item: The selected item
         */
        fun onNavigationItemReselected(item: Item)
    }


    class Item constructor(
        val id: Int,
        val title: String?,
        val icon: Drawable?
    ) {

        constructor(
            context: Context,
            id: Int,
            @StringRes titleRes: Int,
            @DrawableRes iconRes: Int
        ) : this(
            context = context,
            id = id,
            title = context.getString(titleRes),
            iconRes = iconRes,
        )

        constructor(context: Context, id: Int, title: String, @DrawableRes iconRes: Int) : this(
            id = id,
            title = title,
            icon = AppCompatResources.getDrawable(context, iconRes)
        )
    }
}