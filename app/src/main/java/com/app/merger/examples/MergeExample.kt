package com.app.merger.examples

import com.app.utils.Utils
import com.googlecode.mp4parser.FileDataSourceImpl
import com.googlecode.mp4parser.authoring.Track
import com.googlecode.mp4parser.authoring.builder.DefaultMp4Builder
import com.googlecode.mp4parser.authoring.container.mp4.MovieCreator
import com.googlecode.mp4parser.authoring.tracks.AACTrackImpl
import com.googlecode.mp4parser.authoring.tracks.CroppedTrack
import com.googlecode.mp4parser.authoring.tracks.MP3TrackImpl
import io.reactivex.Observable
import java.io.*
import java.util.*
import kotlin.experimental.xor


/**
 * Created by Umair_Adil on 23/07/2016.
 */
class MergeExample(private val videoPath: String, private val audioPath: String) {


    fun merge(): Observable<String> {
        return mergeTracks()
    }

    private fun mergeTracks(): Observable<String> {

        try {
            // Create a media file name
            val filePath = Utils.outputPath + File.separator + "Merged_" + Utils.getTimeStamp() + ".mp4"

            val movie = MovieCreator.build(videoPath)
            val ct = CroppedTrack(getAudioTrack(), 10, 500)
            movie.addTrack(ct)

            val mp4file = DefaultMp4Builder().build(movie)
            val fc = FileOutputStream(File(filePath)).getChannel()
            mp4file.writeContainer(fc)
            fc.close()

            return Observable.just(filePath)

        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: NullPointerException) {
            e.printStackTrace()
        } catch (t: OutOfMemoryError) {
            t.printStackTrace()
        }

        return Observable.just("")
    }

    companion object {
        private val TAG = "MergeExample"
    }

    private fun getAudioTrack(): Track {

        removeHeaders()

        val track = FileDataSourceImpl(audioPath)
        var audio: Track? = null

        if (audioPath.contains(".mp3")) {
            audio = MP3TrackImpl(track)
        } else if (audioPath.contains(".aac")) {
            audio = AACTrackImpl(track)
        }
        return audio!!
    }

    fun removeHeaders(){
        val raf = RandomAccessFile(File(audioPath), "rw")
        val buf = ByteArray(65536)
        var pos: Long = 0
        var len: Int
        val random = Random(34)
        val lent = raf.read(buf)

        while ((lent) != -1) {
            for (i in 0 until lent) {
                buf[i] = buf[i] xor random.nextInt().toByte()
            }
            raf.seek(pos)
            raf.write(buf)
            pos = raf.getFilePointer()
        }
        raf.close()
    }
}
