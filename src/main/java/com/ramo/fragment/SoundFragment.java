package com.ramo.fragment;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ramo.file_transfer.R;
import com.ramo.utils.ImageManageUtil;
import com.ramo.utils.L;
import com.ramo.utils.T;
import com.ramo.utils.ThumbnailsUtil;
import com.xququ.SAE_SDK.XQuquerService;
import com.xququ.SAE_SDK.XQuquerService.XQuquerListener;

import net.frakbot.jumpingbeans.JumpingBeans;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.io.FileNotFoundException;
import java.io.InputStream;


/**
 * Created by ramo on 2016/7/17.
 */
@EFragment(R.layout.fragment_sound)
public class SoundFragment extends Fragment implements OnClickListener, XQuquerListener {

    final String APPAccesskey = "";
    final String APPSecretkey = "";

    private XQuquerService xququerService;
    @ViewById(R.id.sound_message)
    EditText txtMessage;
    @ViewById(R.id.sound_send)
    ImageButton butSend;
    @ViewById
    LinearLayout sound_anim_ll;
    @ViewById
    LinearLayout sound_bottom_choose_file;
    @ViewById(R.id.sound_jump_tv)
    TextView sound_jump_tv;
    @ViewById
    ImageButton uploadFile_btn;
    @ViewById
    TextView sound_cancel_btn;
    @ViewById
    TextView sound_send_text;
    @ViewById
    TextView sound_send_img;
    @ViewById
    TextView sound_send_camera;
    @ViewById
    TextView sound_send_url;
    @ViewById
    ImageView sound_send_iv;

    private String dataToken;
    private String dataContent;

    private final int REQUEST_ALBUMS_CODE = 10;
    private final int REQUEST_CAMERA_CODE = 11;
    private JumpingBeans jumpingBeans;

    private Handler animHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            sound_anim_ll.setVisibility(View.GONE);
        }
    };

    @AfterViews
    public void init() {

        initAnim();
        butSend.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                send();
            }
        });
        uploadFile_btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                openBottomMenu();
            }
        });
        sound_cancel_btn.setOnClickListener(this);
        sound_send_text.setOnClickListener(this);
        sound_send_img.setOnClickListener(this);
        sound_send_camera.setOnClickListener(this);
        sound_send_url.setOnClickListener(this);

        initXQUQUerService();
    }

    private void initAnim() {
        sound_anim_ll.setVisibility(View.VISIBLE);
        sound_anim_ll.setBackgroundResource(R.drawable.sound_anim);
        AnimationDrawable background = (AnimationDrawable) sound_anim_ll.getBackground();
        background.start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2500);
                    animHandler.sendEmptyMessage(0);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    private void initXQUQUerService() {
        xququerService = XQuquerService.getInstance();
        xququerService.setAccesskeyAndSecretKey(APPAccesskey, APPSecretkey);

        dataToken = "0ee220b0e01c0914";
        dataContent = "Hello World !";
    }

    @Override
    public void onStart() {
        super.onStart();
        xququerService.start(this);
        L.e("onStart");
    }

    @Override
    public void onStop() {
        super.onStop();
        xququerService.stop();
        L.e("onStop");
    }

    @Override
    public void onClick(View sender) {
        switch (sender.getId()) {

            case R.id.sound_cancel_btn:
                break;
            case R.id.sound_send_url:
                txtMessage.setText("http://");
                break;
            case R.id.sound_send_text:
                HideIVAndShowEV();
                break;
            case R.id.sound_send_img:
                openSystemPicDir();
                break;
            case R.id.sound_send_camera:
                takeCamera();
                break;
        }
        closeBottomMenu();
    }

    private void takeCamera() {
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        startActivityForResult(intent, REQUEST_CAMERA_CODE);
    }

    private void openBottomMenu() {
        ObjectAnimator.ofFloat(sound_bottom_choose_file, "translationY", sound_bottom_choose_file.getTranslationY(), sound_bottom_choose_file.getTranslationY() - sound_bottom_choose_file.getHeight()).setDuration(300).start();
    }

    private void closeBottomMenu() {
        ObjectAnimator.ofFloat(sound_bottom_choose_file, "translationY", sound_bottom_choose_file.getTranslationY(), sound_bottom_choose_file.getTranslationY() + sound_bottom_choose_file.getHeight()).setDuration(300).start();
    }

    private void openSystemPicDir() {
        Intent intent = new Intent();
        intent.setType("image/*");
        if (Build.VERSION.SDK_INT < 19) {
            intent.setAction(Intent.ACTION_GET_CONTENT);
        } else {
            intent.setAction(Intent.ACTION_PICK);
        }
        startActivityForResult(intent, REQUEST_ALBUMS_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {

            case REQUEST_CAMERA_CODE:
                if (data != null) {
                    Bundle extras = data.getExtras();
                    Bitmap bit = ThumbnailsUtil.decodeBitmap((Bitmap) extras.get("data"), 250, 250);

                    HideEVAndShowIV();
                    sound_send_iv.setImageBitmap(bit);
                    bit = null;
                }
                break;
            case REQUEST_ALBUMS_CODE:
                if (null != data) {
                    Uri uri = data.getData();
                    InputStream inputStream = null;
                    try {
                        inputStream = getContext().getContentResolver().openInputStream(uri);
                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

                        HideEVAndShowIV();
                        sound_send_iv.setImageBitmap(bitmap);

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            default:
                break;
        }
        closeBottomMenu();
    }


    private void send() {
        String dataContent = txtMessage.getText().toString();
        new sendTask().execute(dataContent);
    }

    @Override
    public void onSend() {
        L.e("onSend");
        T.showShort(getContext(), "发送成功");
    }

    @Override
    public void onRecv(String dataToken) {

        if (!dataToken.equals(this.dataToken)) {
            this.dataToken = dataToken;
            this.dataContent = null;
            new recvTask().execute();
        }

    }

    class sendTask extends AsyncTask<String, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            butSend.setImageResource(R.drawable.loading);
        }

        @Override
        protected Void doInBackground(String... params) {
            Looper.prepare();
            String data = params[0];
            if (data.length() <= 0) {
                Drawable drawable = sound_send_iv.getDrawable();
                if (drawable != null) {
                    String s = ImageManageUtil.drawableToStr(drawable);
                    dataContent = "img_" + s;
                    L.e("send img:" + dataContent);
                } else {
                    T.showShort(getContext(), "不能为空");
                    return null;
                }
            }
            if (!data.equals(dataContent)) {
                dataToken = null;
                dataContent = data;
            }
            if (dataToken == null) {
                dataToken = xququerService.uploadData(data);
                L.e("uploadData:" + dataToken);
            }
            if (dataToken != null) xququerService.sendDataToken(dataToken);
            Looper.loop();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            butSend.setImageResource(R.drawable.sound_send_selector);
        }
    }

    class recvTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            return xququerService.downloadData(dataToken);
        }

        @Override
        protected void onPostExecute(String content) {
            T.showShort(getContext(), "接收" + content);
            super.onPostExecute(content);
            dataContent = content;
            if (dataContent.indexOf('_') != -1) {
                String[] split = dataContent.split("_");
                switch (split[0]) {
                    case "img":
                        HideEVAndShowIV();
                        Bitmap bitmap=null;
                        if(split[1]!=null)
                         bitmap = ImageManageUtil.StrToBitmap(split[1]);
                        if (bitmap != null)
                            sound_send_iv.setImageBitmap(bitmap);
                        break;
                }
            } else {
                HideIVAndShowEV();
                txtMessage.setText(content);
            }


        }
    }

    private void HideIVAndShowEV() {
        sound_send_iv.setVisibility(View.GONE);
        txtMessage.setVisibility(View.VISIBLE);
    }

    private void HideEVAndShowIV() {
        txtMessage.setText("");
        txtMessage.setVisibility(View.GONE);
        sound_send_iv.setVisibility(View.VISIBLE);
    }

    @Override
    public void onResume() {
        super.onResume();
        jumpingBeans = JumpingBeans.with(sound_jump_tv)
                .appendJumpingDots()
                .build();
      /*  final TextView textView2 = (TextView) findViewById(R.id.jumping_text_2);
        jumpingBeans2 = JumpingBeans.with(textView2)
                .makeTextJump(0, textView2.getText().toString().indexOf(' '))
                .setIsWave(false)
                .setLoopDuration(1000)
                .build();*/
    }

    @Override
    public void onPause() {
        super.onPause();
        jumpingBeans.stopJumping();
    }
}
