# Android Mp4 Parser Audio and Video Merger
Android App to Append Mp4 Videos or Merge Audio files with video files on your Android device. Code is written in kotlin and supports RxJava2.

It has two basic features:

* Append multiple Mp4 Videos into single video file
* Mp4 Video Audio Overlay (Mux/Merge)

**Note:** _Only Mp4 and AAC format files are supported._

**Append Usage:**

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


**Merge Usage**

            val mergeExample = MergeExample(video.path, audio2.path)

            
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

*Library Used:*
**Mp4Parser**
