package com.app.merger.examples

import com.app.utils.Utils
import com.app.utils.mp3agic.ID3v2
import com.app.utils.mp3agic.ID3v24Tag
import com.app.utils.mp3agic.Mp3File
import com.googlecode.mp4parser.FileDataSourceImpl
import com.googlecode.mp4parser.authoring.Track
import com.googlecode.mp4parser.authoring.builder.DefaultMp4Builder
import com.googlecode.mp4parser.authoring.container.mp4.MovieCreator
import com.googlecode.mp4parser.authoring.tracks.AACTrackImpl
import com.googlecode.mp4parser.authoring.tracks.MP3TrackImpl
import io.reactivex.Observable
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException






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
            //val ct = CroppedTrack(getAudioTrack(), 10, 500)
            val track = getAudioTrack()
            movie.addTrack(track)

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

        val track = FileDataSourceImpl(audioPath)
        var audio: Track? = null

        if (audioPath.contains(".mp3")) {
            readTags()
            //val audioPath = removeTags()
            audio = MP3TrackImpl(track)
        } else if (audioPath.contains(".aac")) {
            audio = AACTrackImpl(track)
        }
        return audio!!
    }

    private fun readTags(){
        val mp3file = Mp3File(File(audioPath))

        if (mp3file.hasId3v2Tag()) {
            val id3v2Tag = mp3file.getId3v2Tag()
            println("Track: " + id3v2Tag.getTrack())
            println("Artist: " + id3v2Tag.getArtist())
            println("Title: " + id3v2Tag.getTitle())
            println("Album: " + id3v2Tag.getAlbum())
            println("Year: " + id3v2Tag.getYear())
            println("Genre: " + id3v2Tag.getGenre() + " (" + id3v2Tag.getGenreDescription() + ")")
            println("Comment: " + id3v2Tag.getComment())
            println("Composer: " + id3v2Tag.getComposer())
            println("Publisher: " + id3v2Tag.getPublisher())
            println("Original artist: " + id3v2Tag.getOriginalArtist())
            println("Album artist: " + id3v2Tag.getAlbumArtist())
            println("Copyright: " + id3v2Tag.getCopyright())
            println("URL: " + id3v2Tag.getUrl())
            println("Encoder: " + id3v2Tag.getEncoder())
            val albumImageData = id3v2Tag.getAlbumImage()
            if (albumImageData != null) {
                println("Have album image data, length: " + albumImageData!!.size + " bytes")
                println("Album image mime type: " + id3v2Tag.getAlbumImageMimeType())
            }
        }
    }

    private fun removeTags(): String {
        val filePath = Utils.outputPath + File.separator + "cleaned.mp3"

        val mp3file = Mp3File(File(audioPath))

        val id3v2Tag: ID3v2
        if (mp3file.hasId3v2Tag()) {
            id3v2Tag = mp3file.id3v2Tag
        } else {
            // mp3 does not have an ID3v2 tag, let's create one..
            id3v2Tag = ID3v24Tag()
            mp3file.id3v2Tag = id3v2Tag
        }
        id3v2Tag.track = "5"
        id3v2Tag.artist = "An Artist"
        id3v2Tag.title = "The Title"
        id3v2Tag.album = "The Album"
        id3v2Tag.year = "2001"
        id3v2Tag.genre = 12
        id3v2Tag.comment = "Some comment"
        id3v2Tag.composer = "The Composer"
        id3v2Tag.publisher = "A Publisher"
        id3v2Tag.originalArtist = "Another Artist"
        id3v2Tag.albumArtist = "An Artist"
        id3v2Tag.copyright = "Copyright"
        id3v2Tag.url = "http://foobar"
        id3v2Tag.encoder = "The Encoder"

        mp3file.save(filePath)

        return filePath
    }
}
