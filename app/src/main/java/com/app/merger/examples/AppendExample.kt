package com.app.merger.examples

import com.app.utils.Utils
import com.googlecode.mp4parser.authoring.Movie
import com.googlecode.mp4parser.authoring.Track
import com.googlecode.mp4parser.authoring.builder.DefaultMp4Builder
import com.googlecode.mp4parser.authoring.container.mp4.MovieCreator
import com.googlecode.mp4parser.authoring.tracks.AppendTrack
import com.googlecode.mp4parser.authoring.tracks.CroppedTrack
import io.reactivex.Observable
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.io.RandomAccessFile
import java.util.*

class AppendExample(private val filePaths: List<String>) {

    fun append(): Observable<String> {
        return doAppend()
    }

    private fun doAppend(): Observable<String> {

        try {

            val fileList = ArrayList<File>()
            val movieList = ArrayList<Movie>()
            for (i in filePaths.indices) {
                fileList.add(File(filePaths[i]))
                movieList.add(MovieCreator.build(File(filePaths[i]).absolutePath))
            }

            // Create a media file name
            val filePath = Utils.outputPath + File.separator + "TMP4_APP_OUT_" + Utils.getTimeStamp() + ".mp4"

            val videoTracks = LinkedList<Track>()
            val audioTracks = LinkedList<Track>()
            val audioDuration = longArrayOf(0)
            val videoDuration = longArrayOf(0)
            for (m in movieList) {
                for (t in m.tracks) {
                    if (t.handler == "soun") {
                        for (a in t.sampleDurations) audioDuration[0] += a
                        audioTracks.add(t)
                    } else if (t.handler == "vide") {
                        for (v in t.sampleDurations) videoDuration[0] += v
                        videoTracks.add(t)
                    }
                }

                adjustDurations(videoTracks, audioTracks, videoDuration, audioDuration)
            }

            //Result movie from putting the audio and video together from the two clips
            val result = Movie()

            //Append all audio and video
            if (videoTracks.size > 0)
                result.addTrack(AppendTrack(*videoTracks.toTypedArray()))

            if (audioTracks.size > 0)
                result.addTrack(AppendTrack(*audioTracks.toTypedArray()))


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
        }

        return Observable.just("")
    }

    private fun adjustDurations(videoTracks: LinkedList<Track>, audioTracks: LinkedList<Track>, videoDuration: LongArray, audioDuration: LongArray) {
        var diff = audioDuration[0] - videoDuration[0]

        //nothing to do
        if (diff == 0L) {
            return
        }

        //audio is longer
        var tracks = audioTracks

        //video is longer
        if (diff < 0) {
            tracks = videoTracks
            diff *= -1
        }

        var track = tracks.last
        val sampleDurations = track.sampleDurations
        var counter: Long = 0
        for (i in sampleDurations.size - 1 downTo -1 + 1) {
            if (sampleDurations[i] > diff) {
                break
            }
            diff -= sampleDurations[i]
            audioDuration[0] -= sampleDurations[i]
            counter++
        }

        if (counter == 0L) {
            return
        }

        track = CroppedTrack(track, 0, track.samples.size - counter)

        //update the original reference
        tracks.removeLast()
        tracks.addLast(track)
    }

    companion object {

        private val TAG = "AppendExample"
    }
}
