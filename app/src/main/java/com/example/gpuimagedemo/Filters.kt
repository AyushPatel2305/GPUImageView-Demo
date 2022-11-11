package com.example.gpuimagedemo

import com.example.gpuimagedemo.utils.FilterType
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageSketchFilter

class Filters(val name: String, val filter: GPUImageFilter)
class FilterValue(val type: FilterType, val filter: GPUImageFilter, var progressValue: Int)
