package com.example.buynow

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.VideoView
import androidx.recyclerview.widget.RecyclerView
import com.example.buynow.databinding.ItemVideoBinding
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem

class VideoAdapter(private val videoUris: List<String>) : RecyclerView.Adapter<VideoAdapter.VideoViewHolder>() {

    inner class VideoViewHolder(val binding: ItemVideoBinding) : RecyclerView.ViewHolder(binding.root) {
        private var player: ExoPlayer? = null

        fun bind(videoUri: String) {
            player?.release()
            player = ExoPlayer.Builder(binding.root.context).build().apply {
                setMediaItem(MediaItem.fromUri(videoUri))
                prepare()
                playWhenReady = true
            }

            binding.videoView.player = player
        }

        fun releasePlayer() {
            player?.release()
            player = null
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        val binding = ItemVideoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VideoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        holder.bind(videoUris[position])
    }

    override fun getItemCount(): Int = videoUris.size

    override fun onViewRecycled(holder: VideoViewHolder) {
        holder.releasePlayer()
        super.onViewRecycled(holder)
       class VideoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            private val videoView: VideoView = itemView.findViewById(R.id.videoView)

            fun bind(videoUri: String) {
                videoView.setVideoURI(Uri.parse(videoUri))
                videoView.start()
            }

            fun pauseVideo() {
                if (videoView.isPlaying) {
                    videoView.pause()
                }
            }
        }
    }
}
