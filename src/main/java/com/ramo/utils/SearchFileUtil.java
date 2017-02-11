package com.ramo.utils;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import com.ramo.file_transfer.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ramo on 2016/4/19.
 */
public class SearchFileUtil {


    private File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath());
    private String key; //关键字
    private Context context;
    private List<com.ramo.bean.File> result;

    public SearchFileUtil(Context context) {
        this.context = context;
        result = new ArrayList<com.ramo.bean.File>();
    }

    /* public List<com.ramo.bean.File> browserFile(String key) {
         this.key=key;
         result.clear();
         search(file);
         return result;
     }*/
    public List<com.ramo.bean.File> browserFile(String key) {
        this.key = key;
        result.clear();

        Uri[] uris = {SystemFilesGetter.IMAGE_URI, SystemFilesGetter.VIDEO_URI, SystemFilesGetter.AUDIO_URI};
        String[][] projection = {{MediaStore.Images.Media.TITLE, MediaStore.Images.Media.DATA}, {MediaStore.Video.Media.TITLE, MediaStore.Video.Media.DATA}, {MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.DATA}};
        for (int k = 0; k < uris.length; k++) {
            Cursor cursor = context.getContentResolver().query(uris[k],
                    projection[k], null, null, null);
            cursor.moveToFirst();

            for (int i = 0; i < cursor.getCount(); i++) {
                String title = cursor.getString(cursor.getColumnIndex(projection[k][0]));
                if (title.indexOf(key) > -1) {
                    String uri = cursor.getString(cursor.getColumnIndex(projection[k][1]));
                    com.ramo.bean.File bean = new com.ramo.bean.File();
                    bean.setFileUrl(uri);
                    bean.setFileName(title);
                    switch (k) {
                        case 0:
                            bean.setFileImg(ImageManageUtil.drawable2Byte(ImageManageUtil.RToDrawable(R.drawable.file_img_small)));
                            break;
                        case 1:
                            bean.setFileImg(ImageManageUtil.drawable2Byte(ImageManageUtil.RToDrawable(R.drawable.file_music_small)));
                            break;
                        case 2:
                            bean.setFileImg(ImageManageUtil.drawable2Byte(ImageManageUtil.RToDrawable(R.drawable.file_video_small)));
                            break;
                    }
                    result.add(bean);
                }
                cursor.moveToNext();
            }
            cursor.close();
        }

        return result;
    }

    private void search(File file) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null) {
                for (File f : files) {
                    if (f.getName().indexOf(key) > -1) {
                        com.ramo.bean.File bean = new com.ramo.bean.File();
                        bean.setFileUrl(f.getPath());
                        bean.setFileName(f.getName());
                        bean.setFileImg(ImageManageUtil.drawable2Byte(ImageManageUtil.RToDrawable(R.drawable.document_48)));
                        result.add(bean);
                    }
                    this.search(f);

                }
            }
        }

    }

}
