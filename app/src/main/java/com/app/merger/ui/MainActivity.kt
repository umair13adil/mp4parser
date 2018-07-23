package com.app.merger.ui

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import com.app.merger.R
import com.app.merger.examples.AppendExample
import com.app.merger.examples.MergeExample
import com.app.utils.Utils
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File

class MainActivity : Activity() {

    val TAG = "MainActivity"

    //Audio & Video Files
    lateinit var audio: File
    lateinit var audio2: File
    lateinit var audio3: File
    lateinit var video: File
    lateinit var video2: File

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.app.merger.R.layout.activity_main)

        //Ask for permissions
        if (ContextCompat.checkSelfPermission(applicationContext, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this@MainActivity, arrayOf<String>(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE), 2222)
        } else if (ContextCompat.checkSelfPermission(applicationContext, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this@MainActivity, arrayOf<String>(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE), 2222)
        } else {
            setUpResources()
        }

        btn_append.setOnClickListener {

            val videoPaths = arrayListOf<String>(video.path, video2.path)
            val appendExample = AppendExample(videoPaths)

            //This will append video files together in a sequence they were added. Output will be a single video file.
            appendExample.append()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeBy(
                            onNext = {
                                if (TextUtils.isEmpty(it)) {
                                    showToast(getString(R.string.message_error))
                                } else {
                                    Log.i(TAG, "Output Path: $it")
                                    showToast(getString(R.string.message_appended) + " " + it)
                                }
                            },
                            onError = {
                                it.printStackTrace()

                            },
                            onComplete = {}
                    )
        }

        btn_merge.setOnClickListener {

            val mergeExample = MergeExample(video.path, audio3.path)

            //This will merge audio and video files together in a single video file.
            mergeExample.merge()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeBy(
                            onNext = {
                                if (TextUtils.isEmpty(it)) {
                                    showToast(getString(R.string.message_error))
                                } else {
                                    Log.i(TAG, "Output Path: $it")
                                    showToast(getString(R.string.message_appended) + " " + it)
                                }
                            },
                            onError = {
                                it.printStackTrace()
                            },
                            onComplete = {}
                    )
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 2222) {
            setUpResources()
        }
    }

    fun setUpResources() {
        //Copy Audio, Video from resources to Storage Directory
        audio = Utils.copyFileToExternalStorage(R.raw.audio, "example.aac", applicationContext)
        audio2 = Utils.copyFileToExternalStorage(R.raw.audio2, "sample.aac", applicationContext)
        audio3 = Utils.copyFileToExternalStorage(R.raw.audio3, "audio3.mp3", applicationContext)
        video = Utils.copyFileToExternalStorage(R.raw.video, "video.mp4", applicationContext)
        video2 = Utils.copyFileToExternalStorage(R.raw.video2, "video2.mp4", applicationContext)
    }
}
