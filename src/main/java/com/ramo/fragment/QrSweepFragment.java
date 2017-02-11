package com.ramo.fragment;

import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.google.zxing.WriterException;
import com.ramo.activity.ConnectStateActivity_;
import com.ramo.file_transfer.R;
import com.ramo.utils.L;
import com.ramo.utils.T;
import com.ramo.view.ArcMenu;
import com.ramo.wifi.WiFiUtils;
import com.zxing.camera.CameraManager;
import com.zxing.decoding.CaptureActivityHandler;
import com.zxing.decoding.InactivityTimer;
import com.zxing.encoding.EncodingHandler;
import com.zxing.view.ViewfinderView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.io.IOException;
import java.util.Vector;


/**
 * Created by ramo on 2016/3/10.
 */
@EFragment(R.layout.sweep_layout)
public class QrSweepFragment extends Fragment implements Callback {


    private CaptureActivityHandler handler;
    @ViewById(R.id.viewfinder_view)
    ViewfinderView viewfinderView;
    private boolean hasSurface;
    private Vector<BarcodeFormat> decodeFormats;
    private String characterSet;
    private InactivityTimer inactivityTimer;
    private MediaPlayer mediaPlayer;
    private boolean playBeep;
    private static final float BEEP_VOLUME = 0.10f;
    private boolean vibrate;

    private boolean isScanning = true;
    @ViewById
    ImageView sweep_qr_code;
    @ViewById
    RelativeLayout sweep_qr_rl;
    @ViewById
    ArcMenu mArcMenu;

    WiFiUtils wiFiUtils;

    @AfterViews
    public void init() {
        CameraManager.init(getActivity().getApplication());
        hasSurface = false;
        inactivityTimer = new InactivityTimer(getActivity());
        wiFiUtils = new WiFiUtils(getContext());

        initListener();
    }

    private void initListener() {

        mArcMenu.setOnMenuItemClickListener(new ArcMenu.OnMenuItemClickListener() {
            @Override
            public void onClick(View view, int pos) {
                if (pos == 2 && isScanning) {
                    pauseSweepCamera();
                    inactivityTimer.shutdown();
                    wiFiUtils.closeWiFi();
                    createQrCode();

                } else {

                    resumeSweepCamera();
                    sweep_qr_rl.setVisibility(View.GONE);
                }
                isScanning = !isScanning;

            }
        });
    }

    private void createQrCode() {
        try {
            sweep_qr_rl.setVisibility(View.VISIBLE);
            String qrText = "ssid=" + WiFiUtils.SSID;
          //  String qrText = "http://15500p32q9.51mypc.cn/LightNing/successShareAndroid?accessPath=38237a26-aa64-4bcd-bf7c-b63597db9697&iconPath=http://15500p32q9.51mypc.cn/LightNing/img/38237a26-aa64-4bcd-bf7c-b63597db9697.png&onlystatus=f9604c79-a88b-4b86-b506-86d7dee";
            if (!qrText.equals("")) {
                Bitmap qrCodeBitmap = EncodingHandler.createQRCode(qrText, 350);
                sweep_qr_code.setImageBitmap(qrCodeBitmap);
                wiFiUtils.setWifiApEnabled(true);
            }
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        resumeSweepCamera();
    }

    private void resumeSweepCamera() {
        SurfaceHolder surfaceHolder = ((SurfaceView) (getActivity().findViewById(R.id.preview_view))).getHolder();
        if (hasSurface) {
            initCamera(surfaceHolder);
        } else {
            surfaceHolder.addCallback(this);
            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }
        decodeFormats = null;
        characterSet = null;

        playBeep = true;
        AudioManager audioService = (AudioManager) getContext().getSystemService(getActivity().AUDIO_SERVICE);
        if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
            playBeep = false;
        }
        initBeepSound();
        vibrate = true;
    }

    @Override
    public void onPause() {
        super.onPause();
        pauseSweepCamera();
    }

    private void pauseSweepCamera() {
        if (handler != null) {
            handler.quitSynchronously();
            handler = null;
        }
        CameraManager.get().closeDriver();
    }

    @Override
    public void onDestroy() {
        inactivityTimer.shutdown();
        super.onDestroy();
    }

    /**
     * 处理扫描结果
     */
    public void handleDecode(Result result, Bitmap barcode) {

        inactivityTimer.onActivity();
        playBeepSoundAndVibrate();
        String resultString = result.getText();
        L.e("result:"+resultString);
        if (resultString.equals("")) {
            T.showShort(getContext(), "扫描失败!");
        } else {
            judgeScanType(resultString);
        }
    }

    private void judgeScanType(String resultString) {
        if (resultString.indexOf("ssid") > 0) {
            wiFiUtils.openWiFi();
            L.e("connectHot");
            connectHot(resultString);
        } else {
            openUrl(resultString);
        }
    }

    private void openUrl(String resultString) {
        Uri content_url = Uri.parse(resultString);
        Intent intent = new Intent(Intent.ACTION_VIEW,content_url);
        startActivity(intent);
    }

    private void connectHot(String resultString) {
        String[] split = resultString.split("=");
        if ("ssid".equals(split[0])) {
            Intent intent = new Intent(getContext(), ConnectStateActivity_.class);
            intent.putExtra("ssid", split[1]);
            startActivity(intent);
        }
    }

    private void initCamera(SurfaceHolder surfaceHolder) {
        try {
            CameraManager.get().openDriver(surfaceHolder);
        } catch (IOException ioe) {
            return;
        } catch (RuntimeException e) {
            return;
        }
        if (handler == null) {
            handler = new CaptureActivityHandler(this, decodeFormats, characterSet);
        }
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {

    }

    public void surfaceCreated(SurfaceHolder holder) {
        if (!hasSurface) {
            hasSurface = true;
            initCamera(holder);
        }

    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        hasSurface = false;

    }

    public ViewfinderView getViewfinderView() {
        return viewfinderView;
    }

    public Handler getHandler() {
        return handler;
    }

    public void drawViewfinder() {
        viewfinderView.drawViewfinder();

    }

    private void initBeepSound() {
        if (playBeep && mediaPlayer == null) {
            getActivity().setVolumeControlStream(AudioManager.STREAM_MUSIC);
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setOnCompletionListener(beepListener);

            AssetFileDescriptor file = getResources().openRawResourceFd(
                    R.raw.beep);
            try {
                mediaPlayer.setDataSource(file.getFileDescriptor(),
                        file.getStartOffset(), file.getLength());
                file.close();
                mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
                mediaPlayer.prepare();
            } catch (IOException e) {
                mediaPlayer = null;
            }
        }
    }

    private static final long VIBRATE_DURATION = 200L;

    private void playBeepSoundAndVibrate() {
        if (playBeep && mediaPlayer != null) {
            mediaPlayer.start();
        }
        if (vibrate) {
            Vibrator vibrator = (Vibrator) getContext().getSystemService(getActivity().VIBRATOR_SERVICE);
            vibrator.vibrate(VIBRATE_DURATION);
        }
    }

    private final OnCompletionListener beepListener = new OnCompletionListener() {
        public void onCompletion(MediaPlayer mediaPlayer) {
            mediaPlayer.seekTo(0);
        }
    };

}