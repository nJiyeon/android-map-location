package campus.tech.kakao.map.adapter.search

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import campus.tech.kakao.map.R
import campus.tech.kakao.map.model.Item
import campus.tech.kakao.map.viewmodel.OnSearchItemClickListener

class SearchAdapter(
    private val onSearchItemClickListener: OnSearchItemClickListener
) : ListAdapter<Item, SearchAdapter.SearchViewHolder>(
    object : DiffUtil.ItemCallback<Item>() {
        override fun areItemsTheSame(oldItem: Item, newItem: Item) =
            oldItem.place == newItem.place

        override fun areContentsTheSame(oldItem: Item, newItem: Item) =
            oldItem == newItem
    }
) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)
        return SearchViewHolder(view)
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        val item = getItem(position)
        holder.bindViewHolder(item, onSearchItemClickListener)
    }

    class SearchViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val itemList: RelativeLayout = view.findViewById(R.id.item_list)
        private val place: TextView = view.findViewById(R.id.place_name)
        private val address: TextView = view.findViewById(R.id.address)
        private val category: TextView = view.findViewById(R.id.category)

        fun bindViewHolder(item: Item, onSearchItemClickListener: OnSearchItemClickListener) {
            place.text = item.place
            address.text = item.address
            category.text = item.category

            itemList.setOnClickListener {
                onSearchItemClickListener.onSearchItemClick(item)
            }
        }
    }
}
