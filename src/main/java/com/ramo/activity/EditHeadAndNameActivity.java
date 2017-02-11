package com.ramo.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.ramo.file_transfer.R;
import com.ramo.utils.ThumbnailsUtil;
import com.ramo.view.CircleImageView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * Created by ramo on 2016/3/27.
 */
@EActivity(R.layout.activity_name_and_head_edit)
public class EditHeadAndNameActivity extends Activity implements View.OnClickListener {
    @ViewById
    Button edit_name_head_camera_btn;
    @ViewById
    Button edit_name_head_albums_btn;
    @ViewById
    CircleImageView edit_head;
    private boolean hasShootPic = false;
    private final int REQUEST_ALBUMS_CODE = 10;
    private final int REQUEST_CAMERA_CODE = 11;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @AfterViews
    public void init() {
        initListener();
    }

    private void initListener() {
        edit_name_head_camera_btn.setOnClickListener(this);
        edit_name_head_albums_btn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.edit_name_head_albums_btn:
                Intent intent = new Intent();
                intent.setType("image/*");
                if (Build.VERSION.SDK_INT < 19) {
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                } else {
                    intent.setAction(Intent.ACTION_PICK);
                }
                startActivityForResult(intent, REQUEST_ALBUMS_CODE);
                break;
            case R.id.edit_name_head_camera_btn:
                intent = new Intent("android.media.action.IMAGE_CAPTURE");
                startActivityForResult(intent, REQUEST_CAMERA_CODE);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CAMERA_CODE:
                if (data != null) {
                    Bundle extras = data.getExtras();
                    Bitmap bit = ThumbnailsUtil.decodeBitmap((Bitmap) extras.get("data"), 100, 100);
                    edit_head.setImageBitmap(bit);
                    bit=null;
                    hasShootPic = true;// 此变量是在提交数据时，验证是否有图片用
                } else {
                    hasShootPic = false;
                }
                break;

            case REQUEST_ALBUMS_CODE:
                if (null != data) {
                    Uri uri = data.getData();
                    InputStream inputStream = null;
                    try {
                        inputStream = getContentResolver().openInputStream(uri);
                        Bitmap bit = ThumbnailsUtil.decodeBitmapFromStream(inputStream, 100, 100);
                        edit_head.setImageBitmap(bit);
                        bit = null;
                        hasShootPic = true;
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                        hasShootPic = false;
                    }
                }
            default:
                break;
        }
    }
}
