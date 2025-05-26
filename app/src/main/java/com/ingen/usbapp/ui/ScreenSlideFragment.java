package com.ingen.usbapp.ui;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.ingen.usbapp.R;
import com.ingen.usbapp.playlist.MediaObject;
import com.ingen.usbapp.ui.common.BaseFragment;
import com.ingen.usbapp.utils.Logger;
import com.bumptech.glide.Glide;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.PlaybackException;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.RenderersFactory;
import com.google.android.exoplayer2.TracksInfo;
import com.google.android.exoplayer2.audio.AudioAttributes;
import com.google.android.exoplayer2.ui.StyledPlayerView;
import com.google.android.exoplayer2.util.Util;

import java.util.ArrayList;

public class ScreenSlideFragment extends BaseFragment {

    private final int MESSAGE_PLAY_NEXT_MEDIA = 90;

    private StyledPlayerView playerView;
    private ImageView imageView;

    private ExoPlayer player;
    private ArrayList<MediaObject> mPlaylist;

    private int mMediaIndex;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message message) {
            super.handleMessage(message);

            switch (message.what) {
                case MESSAGE_PLAY_NEXT_MEDIA:
                    loadNextMedia();
                    break;
            }
        }
    };

    public static ScreenSlideFragment getInstance(ArrayList<MediaObject> playlist) {
        ScreenSlideFragment fragment = new ScreenSlideFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("Playlist", playlist);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected int getLayout() {
        return R.layout.fragment_screen_slide;
    }

    @Override
    protected void initView(View view) {
        playerView = view.findViewById(R.id.player_view);
        imageView = view.findViewById(R.id.imageView);
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        mPlaylist = getArguments().getParcelableArrayList("Playlist");


    }

    @Override
    public void onStart() {
        super.onStart();
        mMediaIndex = 0;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mPlaylist != null && mMediaIndex < mPlaylist.size()) {
            playMedia(mPlaylist.get(mMediaIndex));
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (Util.SDK_INT > 23) {
            if (playerView != null) {
                playerView.onPause();
            }
            releasePlayer();
        }

        mHandler.removeCallbacksAndMessages(null);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        releaseClientSideAdsLoader();
        mHandler.removeCallbacksAndMessages(null);
        mHandler = null;
    }

    private void loadNextMedia() {
        try {
            mMediaIndex += 1;
            if (mMediaIndex >= mPlaylist.size()) {
                mMediaIndex = 0;
            }

            if (mPlaylist != null && mMediaIndex < mPlaylist.size()) {
                playMedia(mPlaylist.get(mMediaIndex));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void playMedia(MediaObject mediaObject) {
        if (mediaObject == null) {
            return;
        }

        Logger.d("playMedia " + mediaObject.getFile().getName());
        if (mediaObject.isImageFile()) {
            playerView.setVisibility(View.INVISIBLE);
            imageView.setVisibility(View.VISIBLE);

            Glide.with(getContext())
                    .load(mediaObject.getUri())
                    .into(imageView)
            ;

            int delay = mediaObject.getDurationOfImage();
            Logger.d("delay " + delay);
            mHandler.sendEmptyMessageDelayed(MESSAGE_PLAY_NEXT_MEDIA, delay * 1000);

        } else if (mediaObject.isVideoFile()) {
            playerView.setVisibility(View.VISIBLE);
            imageView.setVisibility(View.INVISIBLE);

            playVideo(mediaObject);
        }

        hideNavBar();
    }

    private boolean playVideo(MediaObject mediaObject) {
        if (player == null) {
            RenderersFactory renderersFactory = buildRenderersFactory(getContext(), false, true);

            player = new ExoPlayer.Builder(getContext())
                    .setRenderersFactory(renderersFactory)
                    //.setMediaSourceFactory(createMediaSourceFactory())
                    .build();
            player.addListener(new PlayerEventListener());
            player.setAudioAttributes(AudioAttributes.DEFAULT, true);
            player.setPlayWhenReady(true);

            playerView.setPlayer(player);
            playerView.setControllerAutoShow(false);
        }

        player.setMediaItem(new MediaItem.Builder().setUri(mediaObject.getUri()).build());
        player.prepare();

        return true;
    }

    private void releasePlayer() {
        if (player != null) {
            player.release();
            player = null;
        }
        if (playerView != null) {
            playerView.setPlayer(/* player= */ null);
            playerView.getAdViewGroup().removeAllViews();
        }
    }

    private void releaseClientSideAdsLoader() {
        playerView.getAdViewGroup().removeAllViews();
    }

    public static RenderersFactory buildRenderersFactory(Context context, boolean preferExtensionRenderer, boolean useExtensionRenderers) {
        @DefaultRenderersFactory.ExtensionRendererMode
        int extensionRendererMode =
                useExtensionRenderers
                        ? (preferExtensionRenderer
                        ? DefaultRenderersFactory.EXTENSION_RENDERER_MODE_PREFER
                        : DefaultRenderersFactory.EXTENSION_RENDERER_MODE_ON)
                        : DefaultRenderersFactory.EXTENSION_RENDERER_MODE_OFF;

        return new DefaultRenderersFactory(context.getApplicationContext())
                .setExtensionRendererMode(extensionRendererMode);
    }

    private class PlayerEventListener implements Player.Listener {

        @Override
        public void onPlaybackStateChanged(@Player.State int playbackState) {
            Logger.d("onPlaybackStateChanged " + playbackState);
            if (playbackState == Player.STATE_ENDED) {
                loadNextMedia();
            }
        }

        @Override
        public void onPlayerError(PlaybackException error) {
            Logger.d("onPlayerError " + error.getMessage());
            loadNextMedia();
        }

        @Override
        @SuppressWarnings("ReferenceEquality")
        public void onTracksInfoChanged(TracksInfo tracksInfo) {
            Logger.d("onTracksInfoChanged " + tracksInfo.toString());
        }
    }
}
