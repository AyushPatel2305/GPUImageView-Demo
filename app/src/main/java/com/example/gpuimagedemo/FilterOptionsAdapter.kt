package com.example.gpuimagedemo

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.gpuimagedemo.databinding.ItemFilterOptionsBinding
import com.example.gpuimagedemo.utils.FilterList
import com.example.gpuimagedemo.utils.FilterType
import com.example.gpuimagedemo.utils.GPUImageFilterTools

class FilterOptionsAdapter(
    val image: Uri,
    val filters: FilterList,
    private val listener: OnFilterChangedListener,
) :
    RecyclerView.Adapter<FilterOptionsAdapter.ViewHolder>() {


    private val filterNames = filters.getNames().toTypedArray()

    val filterList = arrayListOf<FilterType>()

    class ViewHolder(val binding: ItemFilterOptionsBinding, val imageUri: Uri) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemFilterOptionsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding, image)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (filterList.contains(filters.getFilter(filterNames[position]))) {
            holder.binding.container.isSelected = true
            holder.binding.ivDelete.isVisible = true
        } else {
            holder.binding.container.isSelected = false
            holder.binding.ivDelete.isVisible = false
        }
        holder.binding.tvName.text = filterNames[position]
        holder.binding.container.setOnClickListener {
            val filterType = filters.getFilter(filterNames[position])
            if (!filterList.contains(filterType)) {
                filterList.add(filterType)
                notifyItemChanged(position)
            }
            listener.onFilterChanged(filterType)
        }
        holder.binding.ivDelete.setOnClickListener {
            val filterType = filters.getFilter(filterNames[position])
            filterList.remove(filterType)
            listener.OnFilterRemoved(filterType)
            notifyItemChanged(position)
        }
    }

    override fun getItemCount(): Int {
        return filterNames.size
    }

    fun resetIsSelected() {
        filterList.clear()
        notifyDataSetChanged()
    }

    interface OnFilterChangedListener {
        fun onFilterChanged(filter: FilterType)
        fun OnFilterRemoved(filter: FilterType)
    }

}