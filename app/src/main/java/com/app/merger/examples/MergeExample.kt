package com.app.merger.examples

import com.app.utils.Utils
import com.googlecode.mp4parser.FileDataSourceImpl
import com.googlecode.mp4parser.authoring.Movie
import com.googlecode.mp4parser.authoring.Track
import com.googlecode.mp4parser.authoring.builder.DefaultMp4Builder
import com.googlecode.mp4parser.authoring.container.mp4.MovieCreator
import com.googlecode.mp4parser.authoring.tracks.AACTrackImpl
import com.googlecode.mp4parser.authoring.tracks.AppendTrack
import com.googlecode.mp4parser.authoring.tracks.CroppedTrack
import io.reactivex.Observable
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.io.RandomAccessFile
import java.util.*

/**
 * Created by Umair_Adil on 23/07/2016.
 */
class MergeExample(private val videoPaths: List<String>, private val audioPaths: List<String>) {


    fun merge(): Observable<String> {
        return mergeTracks()
    }

    private fun mergeTracks(): Observable<String> {

        try {
            // Create a media file name
            val filePath = Utils.outputPath + File.separator + "TMP4_APP_OUT_" + Utils.getTimeStamp() + ".mp4"

            val inMovies = arrayListOf<Movie>()
            var aacTrack: AACTrackImpl? = null


            val videoFileList = ArrayList<File>()
            val audioFileList = ArrayList<File>()

            for (i in videoPaths.indices) {
                val file = File(videoPaths[i])
                val movie = MovieCreator.build(file.absolutePath)!!
                inMovies.add(movie)
                videoFileList.add(File(videoPaths[i]))
            }

            for (i in audioPaths.indices) {
                val file = File(audioPaths[i])
                aacTrack = AACTrackImpl(FileDataSourceImpl(file))
                audioFileList.add(File(audioPaths[i]))
            }


            val videoTracks = LinkedList<Track>()
            val audioTracks = LinkedList<Track>()

            val aacTrackShort = CroppedTrack(aacTrack!!, 1, aacTrack.samples.size.toLong())
            audioTracks.add(aacTrackShort)
            videoTracks.add(inMovies.first().getTracks().first())

            //Result movie from putting the audio and video together from the two clips
            val result = Movie()

            //Append all audio and video
            if (videoTracks.size > 0)
                result.addTrack(AppendTrack(*videoTracks.toTypedArray()))

            if (audioTracks.size > 0)
            //result.addTrack(new AppendTrack(audioTracks.toArray(new Track[audioTracks.size()])));
                result.addTrack(aacTrackShort)


            val out = DefaultMp4Builder().build(result)
            val fc = RandomAccessFile(String.format(filePath), "rw").channel
            out.writeContainer(fc)
            fc.close()

            //TODO Refresh Gallery Here

            return Observable.just(filePath)

        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: NullPointerException) {
            e.printStackTrace()
        } catch (e: OutOfMemoryError) {
            e.printStackTrace()
        }

        return Observable.just("")
    }

    companion object {
        private val TAG = "MergeExample"
    }
}
