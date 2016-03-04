package de.project.ice.inventory

import com.badlogic.gdx.utils.Array
import de.project.ice.IceGame

class Inventory(private val game: IceGame) {
    val items = Array<Item>()

    fun addItem(name: String) {
        removeItem(name)
        val item = Items.get(name)
        item.inventory = this
        items.add(item)
    }

    fun removeItem(name: String) {
        items.removeValue(Items.get(name), true)
    }

    class Item @JvmOverloads constructor(
            val name: String = "INVALID",
            val icon: String = "invalid_item",
            val description: String = "Invalid Item",
            val listener: ItemListener? = null) {

        internal var inventory: Inventory? = null

        protected fun Inventory(): Inventory? {
            return inventory
        }
    }

    interface ItemListener {
        fun itemClicked()
    }
}