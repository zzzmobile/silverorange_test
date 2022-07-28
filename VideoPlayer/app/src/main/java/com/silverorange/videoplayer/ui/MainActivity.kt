package com.silverorange.videoplayer.ui

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkRequest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.PlaybackException
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.google.android.material.snackbar.Snackbar
import com.silverorange.videoplayer.R
import com.silverorange.videoplayer.di.Injection
import com.silverorange.videoplayer.model.Video
import com.silverorange.videoplayer.viewmodel.MainViewModel
import io.noties.markwon.Markwon
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.layout_control.*

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: MainViewModel   // viewmodel for mainactivity
    private var videoList = mutableListOf<Video>()  // video object list

    private var videoPlayer: ExoPlayer? = null  // videoplayer by ExoPlayer
    private var currentVideoIndex = -1      // current video index, -1 in defeault value

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // check network connection
        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val builder: NetworkRequest.Builder = NetworkRequest.Builder()
        cm.registerNetworkCallback(
            builder.build(),
            object : ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network) {
                }

                override fun onLost(network: Network) {
                    Snackbar.make(contentView, "Your network is disconnected now.", Snackbar.LENGTH_SHORT).show()
                }

                override fun onUnavailable() {
                    Snackbar.make(contentView, "Your network is unavailable now.", Snackbar.LENGTH_SHORT).show()
                }
            }
        )

        viewModel = ViewModelProvider(this, Injection.provideViewModelFactory()).get(MainViewModel::class.java)

        // observes
        viewModel.videos.observe(this) { list ->
            // sort list by published date
            videoList.addAll(list.sortedBy { it.publishedAt })

            // set video and index
            if (videoList.size > 0) {
                currentVideoIndex = 0
                setVideoData()
            }
        }
        viewModel.error.observe(this) {
            // show fetch error
            Snackbar.make(contentView, "Failed to fetch videos.", Snackbar.LENGTH_SHORT).show()
        }

        // fetch video data from server
        viewModel.getVideos()

        initVideoPlayer()
        setupUI()
    }

    private fun setVideoData() {
        if (videoList.size > 0 && currentVideoIndex < videoList.size) {
            val video = videoList[currentVideoIndex]

            video.title?.let {
                setMarkwonTextView(textTitle, it)
            }
            video.author?.name?.let {
                setMarkwonTextView(textAuthor, it)
            }
            video.description?.let {
                setMarkwonTextView(textDescription, it)
            }

            // prepare video
            if (video.fullURL != null) {
                val mediaSource = buildMediaSource(video.fullURL!!)
                videoPlayer!!.setMediaSource(mediaSource)
                videoPlayer!!.prepare()
                videoPlayer!!.pause()
            }

            updateButtonStatus()
        }
    }

    private fun initVideoPlayer() {
        videoPlayer = ExoPlayer.Builder(this).build()
        playerView.player = videoPlayer

        videoPlayer!!.playWhenReady = false

        videoPlayer!!.addListener(object : Player.Listener {
            override fun onPlayerError(error: PlaybackException) {
                Snackbar.make(contentView, error.localizedMessage, Snackbar.LENGTH_SHORT).show()
            }
        })

    }

    private fun releasePlayer() {
        if (videoPlayer == null)
            return

        videoPlayer!!.release()
        videoPlayer = null
    }

    override fun onStart() {
        super.onStart()
        initVideoPlayer()
    }

    override fun onResume() {
        super.onResume()
        if (videoPlayer == null) {
            initVideoPlayer()
        }
    }

    override fun onStop() {
        super.onStop()
        releasePlayer()
    }

    private fun buildMediaSource(videoUri: String): MediaSource {
        val dataSourceFactory: DataSource.Factory = DefaultHttpDataSource.Factory()
        return ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(
            MediaItem.fromUri(videoUri)
        )
    }

    private fun setupUI() {
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.hide()

        buttonPlay.setOnClickListener {
            videoPlayer!!.play()
            buttonPlay.visibility = View.INVISIBLE
            buttonPause.visibility = View.VISIBLE
        }
        buttonPause.setOnClickListener {
            videoPlayer!!.pause()
            buttonPlay.visibility = View.VISIBLE
            buttonPause.visibility = View.INVISIBLE
        }
        buttonPrev.setOnClickListener {
            currentVideoIndex -= 1
            setVideoData()
        }
        buttonNext.setOnClickListener {
            currentVideoIndex += 1
            setVideoData()
        }

        buttonPrev.isEnabled = false
        buttonNext.isEnabled = false
    }

    private fun updateButtonStatus() {
        buttonPlay.visibility = View.VISIBLE
        buttonPause.visibility = View.INVISIBLE

        val count = videoList.size
        if (count == 0 || count == 1) {
            buttonPrev.isEnabled = false
            buttonNext.isEnabled = false
        } else if (currentVideoIndex <= 0) {
            buttonPrev.isEnabled = false
            buttonNext.isEnabled = true
        } else if (currentVideoIndex >= videoList.size - 1) {
            buttonPrev.isEnabled = true
            buttonNext.isEnabled = false
        }
    }

    // set markwon for textview
    private fun setMarkwonTextView(textView: TextView, str: String) {
        val markwon = Markwon.create(this)
        val markdown = markwon.toMarkdown(str)
        markwon.setParsedMarkdown(textView, markdown)
    }
}