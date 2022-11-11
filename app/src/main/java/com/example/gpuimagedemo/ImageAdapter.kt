package com.example.gpuimagedemo

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.gpuimagedemo.databinding.ItemImagePreviewBinding
import jp.co.cyberagent.android.gpuimage.filter.*

class ImageAdapter(val image: Uri, private val listener: OnFilterChangedListener) :
    RecyclerView.Adapter<ImageAdapter.ViewHolder>() {

    private val filterList = arrayListOf(
        Filters("Normal", GPUImageFilter()),
        Filters("Monochrome", GPUImageMonochromeFilter()),
        Filters("Sketch", GPUImageSketchFilter()),
        Filters("Toon", GPUImageToonFilter()),
        Filters("Solarize", GPUImageSolarizeFilter()),
        Filters("Swirl", GPUImageSwirlFilter()),
        Filters("Laplacian", GPUImageLaplacianFilter()),
        Filters("False Color", GPUImageFalseColorFilter()),
        Filters("KUWAHARA", GPUImageKuwaharaFilter()),
    )

//    private var selectedPosition = 0

    class ViewHolder(val binding: ItemImagePreviewBinding, val imageUri: Uri) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(filter: Filters) {
            binding.gpuImageView.setImage(imageUri)
            binding.gpuImageView.filter = filter.filter
            binding.tvFilterName.text = filter.name
        }

    }

    override fun onViewAttachedToWindow(holder: ViewHolder) {

        holder.binding.gpuImageView.gpuImage.deleteImage()
        holder.binding.gpuImageView.setImage(holder.imageUri)
        holder.binding.gpuImageView.filter = filterList[holder.adapterPosition].filter

        super.onViewAttachedToWindow(holder)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemImagePreviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding, image)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(filterList[position])
        holder.binding.root.setOnClickListener {
//            selectedPosition = holder.adapterPosition
            listener.onFilterChanged(filterList[position].filter)
        }
    }

    override fun getItemCount(): Int {
        return filterList.size
    }

    interface OnFilterChangedListener {
        fun onFilterChanged(filter: GPUImageFilter)
    }

}