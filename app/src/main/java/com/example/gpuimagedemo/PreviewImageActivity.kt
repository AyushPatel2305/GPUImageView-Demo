package com.example.gpuimagedemo

import android.app.Activity
import android.app.Dialog
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.gpuimagedemo.databinding.ActivityPreviewImageBinding
import com.example.gpuimagedemo.databinding.DialogAlertCustomBinding
import com.example.gpuimagedemo.utils.FilterList
import com.example.gpuimagedemo.utils.FilterType
import com.example.gpuimagedemo.utils.GPUImageFilterTools
import com.google.android.material.slider.Slider
import com.google.android.material.slider.Slider.OnSliderTouchListener
import jp.co.cyberagent.android.gpuimage.GPUImage
import jp.co.cyberagent.android.gpuimage.GPUImageView
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilterGroup
import kotlin.math.roundToInt

class PreviewImageActivity : AppCompatActivity(),
    GPUImageView.OnPictureSavedListener, FilterOptionsAdapter.OnFilterChangedListener {

    private var uri: Uri? = null
    private lateinit var mBinding: ActivityPreviewImageBinding
    private var dialog: Dialog? = null

    val filterList = arrayListOf<FilterValue>()
    var filterAdapter: FilterOptionsAdapter? = null

    val typesArr = arrayOf("Default", "Adjustable", "Blend", "Others")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityPreviewImageBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        uri = intent.getParcelableExtra("uri")

        setImage()
        mBinding.gpuImage.setScaleType(GPUImage.ScaleType.CENTER_INSIDE)

        val adapter = ArrayAdapter(this,
            androidx.databinding.library.baseAdapters.R.layout.support_simple_spinner_dropdown_item,
            typesArr)

        adapter.setDropDownViewResource(androidx.databinding.library.baseAdapters.R.layout.support_simple_spinner_dropdown_item)

        mBinding.spnFilters.adapter = adapter

        mBinding.spnFilters.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                when (p2) {
                    0 -> {
                        setFilterAdapter(GPUImageFilterTools.filters)
                    }
                    1 -> {
                        setFilterAdapter(GPUImageFilterTools.adjustableFilters)
                    }
                    2 -> {
                        setFilterAdapter(GPUImageFilterTools.blendFliters)
                    }
                    3 -> {
                        setFilterAdapter(GPUImageFilterTools.blackFilters)
                    }
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                setFilterAdapter(GPUImageFilterTools.filters)
            }
        }

        mBinding.rvFilters.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        setFilterAdapter(GPUImageFilterTools.filters)

        mBinding.btnReset.setOnClickListener {
            resetFilters()
        }
        mBinding.btnSave.setOnClickListener {
            saveImage()
        }
    }

    private fun resetFilters() {
        filterList.clear()
        filterAdapter?.resetIsSelected()
        mBinding.filterSlider.value = 0f
        mBinding.filterSlider.isVisible = false
        setFilter()
    }

    private fun setFilterAdapter(filters: FilterList) {
        resetFilters()
        filterAdapter = FilterOptionsAdapter(uri!!,
            filters,
            this)
        mBinding.rvFilters.adapter = filterAdapter
    }

    private fun setImage() {
        if (uri != null) {

            mBinding.gpuImage.setImage(uri)
            mBinding.gpuImage.filter = GPUImageFilter()
        }
    }

    fun setFilter() {
        val filtersList: ArrayList<GPUImageFilter> = arrayListOf()

        if (!filterList.isNullOrEmpty()) {
            for (i in filterList) {
                filtersList.add(i.filter)
            }
        }
        if (filtersList.isNotEmpty()) {
            val filterGrp = GPUImageFilterGroup(filtersList)
            mBinding.gpuImage.setImage(uri)
            mBinding.gpuImage.filter = filterGrp
        } else {
            mBinding.gpuImage.setImage(uri)
            mBinding.gpuImage.filter = GPUImageFilter()
        }
    }

    override fun onPictureSaved(uri: Uri?) {
        Toast.makeText(this@PreviewImageActivity, "Image Saved", Toast.LENGTH_SHORT).show()
    }

    fun getFilter(filterType: FilterType): Int {
        var filterObj: FilterValue? = null
        for (filter in filterList) {
            if (filterType == filter.type) {
                filterObj = filter
                break
            }
        }
        if (filterObj == null) {
            val newFilterObj =
                GPUImageFilterTools.createFilterForType(this@PreviewImageActivity, filterType)
            filterObj = FilterValue(filterType,
                newFilterObj,
                GPUImageFilterTools.getFilterProgressInPercent(filterType))
            filterList.add(filterObj)
        }
        Log.e("FILTER_LIST_SIZE", "${filterList.size}")
        Log.e("FILTER_LIST_POS", "${filterList.indexOf(filterObj)}")
        return filterList.indexOf(filterObj)
    }

    fun removeFilter(filterType: FilterType) {
        for (filter in filterList) {
            if (filterType == filter.type) {
                filterList.remove(filter)
                setFilter()
                mBinding.filterSlider.isVisible = false
                break
            }
        }
    }

    override fun onFilterChanged(filter: FilterType) {
        val filterObjPos = getFilter(filter)
        setFilter()
        val adjuster =
            GPUImageFilterTools.FilterAdjuster(filterList[filterObjPos].filter)
        mBinding.filterSlider.clearOnSliderTouchListeners()
        if (adjuster.canAdjust()) {
            mBinding.filterSlider.isVisible = true
            adjuster.adjust(filterList[filterObjPos].progressValue)
            mBinding.filterSlider.value =
                filterList[filterObjPos].progressValue.toFloat()
            mBinding.filterSlider.addOnSliderTouchListener(object :
                OnSliderTouchListener {
                override fun onStartTrackingTouch(slider: Slider) {}

                override fun onStopTrackingTouch(slider: Slider) {
                    if (adjuster.canAdjust()) {
                        adjuster.adjust(slider.value.roundToInt())

                        filterList[filterObjPos].progressValue = slider.value.toInt()
                        mBinding.gpuImage.requestRender()
                    }
                }

            })
        } else {
            mBinding.filterSlider.isVisible = false
        }
    }

    override fun OnFilterRemoved(filter: FilterType) {
        removeFilter(filter)
    }

    fun saveImage() {
        try {
            dialog = Dialog(this)
            val binding: DialogAlertCustomBinding = DataBindingUtil.inflate(layoutInflater,
                R.layout.dialog_alert_custom,
                null,
                false)
            dialog?.window?.setBackgroundDrawable(ContextCompat.getDrawable(this,
                android.R.color.transparent))
            dialog?.setContentView(binding.root)
            dialog?.setCancelable(false)
            dialog?.setCanceledOnTouchOutside(false)
            dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
            val window = dialog?.window
            window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT)
            window?.setGravity(Gravity.CENTER)
            binding.tvPositive.setOnClickListener {
                dialog?.dismiss()
                mBinding.gpuImage.saveToPictures("GPUImageSave", "${System.currentTimeMillis()}.jpg") {
                    Toast.makeText(this, "Image Saved", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
            binding.tvNegative.setOnClickListener {
                dialog?.dismiss()
            }
            dialog?.show()
            val windowManager = window?.attributes
            windowManager?.dimAmount = 0.2f
            window?.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}