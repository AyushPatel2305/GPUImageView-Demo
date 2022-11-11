package com.example.gpuimagedemo

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.result.contract.ActivityResultContracts
import com.bumptech.glide.Glide
import com.example.gpuimagedemo.databinding.ActivityMainBinding
import com.example.gpuimagedemo.utils.FilePathUtil
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityMainBinding
    private var uri: Uri? = null

    private val array = intArrayOf(
        R.drawable.img1,
        R.drawable.img2,
        R.drawable.img3,
        R.drawable.img4,
        R.drawable.img5,
        R.drawable.img6,
        R.drawable.img7,
        R.drawable.img8,
        R.drawable.img9,
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        mBinding.llCamera.setOnClickListener {
            openCamera()
        }

        mBinding.llGallery.setOnClickListener {
            galleryResult.launch("image/*")
        }

    }

    private fun changeBackground() {
        Glide.with(this)
            .load(array.random())
            .into(mBinding.ivHomeBg)

    }

    override fun onResume() {
        super.onResume()
        changeBackground()
    }

    fun openCamera() {
        val contentResolver = contentResolver
        val cv = ContentValues()
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        cv.put(MediaStore.Images.Media.TITLE, timeStamp)
        uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, cv)
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
        grantWritePermission(this, intent, uri!!)
        cameraResult.launch(intent)
    }

    private val cameraResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val path = uri
                val name = uri?.let { FilePathUtil.getFileName(this, it) }
                val fullPath = uri?.let { FilePathUtil.getPath(this, it) }
                startActivity(
                    Intent(this, PreviewImageActivity::class.java).putExtra(
                        "path",
                        fullPath
                    ).putExtra("uri", uri)
                )
            }
        }

    private val galleryResult =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            val path = uri
            val name = uri?.let { FilePathUtil.getFileName(this, it) }
            val fullPath = uri?.let { FilePathUtil.getPath(this, it) }
            startActivity(
                Intent(this, PreviewImageActivity::class.java).putExtra(
                    "path",
                    fullPath
                ).putExtra("uri", uri)
            )
        }


    private fun grantWritePermission(context: Context, intent: Intent, uri: Uri) {
        val resInfoList =
            context.packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
        for (resolveInfo in resInfoList) {
            val packageName = resolveInfo.activityInfo.packageName
            context.grantUriPermission(
                packageName,
                uri,
                Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
        }
    }

}