package space.active.testeroid.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import space.active.testeroid.R
import space.active.testeroid.TAG


class RecyclerViewAdapter(
    // construct and initialize interface for listen clicks
    private val itemClickListener: ItemClickListener,
    private val checkBoxVisibility: Boolean
): RecyclerView.Adapter<RecyclerViewAdapter.ListHolder>()  {

    // Create interfaces for listen clicks start
    interface ItemClickListener {
        fun onItemClick(values: AdapterItems)
        fun onItemLongClick(values: AdapterItems)
        fun onCheckBoxClick(value: AdapterItems)
    }
    // Create interfaces for listen clicks end

    var listItems = emptyList<AdapterItems>()

    class AdapterItems(
        val itemName: String,
        val itemId: Long,
        var position: Int = 0,
        var selected: Boolean = false)

    class ListHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Binding views with values
        val tvTitle: TextView = itemView.findViewById(R.id.tv_title)
        val imCheck: ImageView = itemView.findViewById(R.id.img_check)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.edit_test_list_item, parent, false)
        return ListHolder(view)
    }

    override fun onBindViewHolder(holder: ListHolder, position: Int) {
        holder.imCheck.isVisible = checkBoxVisibility

        listItems[position].position = holder.adapterPosition
        holder.tvTitle.text = listItems[position].itemName

        if (checkBoxVisibility) {
            holder.imCheck.checkTintColor(listItems[position].selected)
        } else {
            holder.imCheck.checkVisible(listItems[position].selected)
        }

        holder.imCheck.setOnClickListener {
            itemClickListener.onCheckBoxClick(listItems[position])
        }

        holder.itemView.setOnClickListener {
            itemClickListener.onItemClick(listItems[position])
        }

        holder.itemView.setOnLongClickListener {
            selectItemInViewModel(listItems[position])
            return@setOnLongClickListener true
        }
        // Bind click listener end
    }

    private fun ImageView.checkVisible (value: Boolean) {
        this.isVisible = value
    }

    private fun ImageView.checkTintColor (value: Boolean) {
        if (value) {
            this.imageTintList = context?.getColorStateList(R.color.yellow)
        } else {
            this.imageTintList = context?.getColorStateList(R.color.gray_70)
        }
    }

    override fun getItemCount(): Int {
        return listItems.size
    }

    // Update RV and store list into listTests
    fun setList(list: List<AdapterItems>){
        Log.e(TAG, "Start setList list.size: ${list.size}")
        listItems = list
        notifyDataSetChanged()
    }

    fun setSelected(listSelectedItemId: List<Long>){
        listItems.forEach { item->
            if (listSelectedItemId.any { selected -> item.itemId == selected}) {
                if (!item.selected) {
                    item.selected = true
                    notifyItemChanged(item.position)
                }
            } else if (item.selected) {
                item.selected = false
                notifyItemChanged(item.position)
            }
        }
    }

    fun clearSelected(){
        Log.e(TAG, "Start clearSelected")
        try {
            if (listItems.isNotEmpty()) {
                listItems.map { it.selected = false }
                notifyDataSetChanged()
            }
        } catch ( e: Exception) {
            Log.e(TAG, "Error ${e.message}")
        }
    }

    // It works when we need to send item in View and next ViewModel
    private fun selectItemInViewModel(item: AdapterItems){
        itemClickListener.onItemLongClick(item)
    }

    //It works when we not need process selected items in ViewModel. Not save selected if rotate screen
    fun selectItemInAdapter(item: AdapterItems){
        item.selected = !item.selected
        notifyItemChanged(item.position)
    }
}