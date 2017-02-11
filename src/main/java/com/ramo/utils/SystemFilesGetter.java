package com.ramo.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;

import com.ramo.bean.AppInfo;
import com.ramo.bean.File;
import com.ramo.bean.Multimedia;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ramo on 2016/3/24.
 */
public class SystemFilesGetter {

    private Context context;
    private List<PackageInfo> installedPackages;
    public final static Uri AUDIO_URI = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
    public final static Uri IMAGE_URI = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
    public final static Uri VIDEO_URI = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;

    public SystemFilesGetter(Context context) {
        this.context = context;
        installedPackages = context.getPackageManager().getInstalledPackages(0);
    }


    public List getNonSystemAppInfo() {
        List<AppInfo> nonSystemAppList = new ArrayList<AppInfo>();
        AppInfo appInfo;
        for (PackageInfo packageInfo : installedPackages) {

            if (isNonSystemApp(packageInfo)) {
                appInfo = package2App(packageInfo);
                nonSystemAppList.add(appInfo);
            }
        }

        return nonSystemAppList;
    }

    private boolean isNonSystemApp(PackageInfo packageInfo) {
        return (packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0;
    }

    @NonNull
    private AppInfo package2App(PackageInfo packageInfo) {

        AppInfo appInfo = new AppInfo();
        appInfo.setFileName(packageInfo.applicationInfo.loadLabel(context.getPackageManager()).toString());
        appInfo.setPackageName(packageInfo.packageName);
        Drawable drawable = packageInfo.applicationInfo.loadIcon(context.getPackageManager());
        appInfo.setFileImg(ImageManageUtil.drawable2Byte(drawable));
        return appInfo;
    }

    public List<File> getAllImg() {

        List<File> imgList = new ArrayList<File>();
        ContentResolver contentResolver = context.getContentResolver();

        String[] projection = new String[]{MediaStore.Images.Media.TITLE, MediaStore.Images.Media.DATA,
                MediaStore.Images.Thumbnails._ID, MediaStore.Images.Media.SIZE};

        Cursor cursor = contentResolver.query(IMAGE_URI,
                projection, null, null, MediaStore.Images.Media.DEFAULT_SORT_ORDER);

        cursor.moveToFirst();

        for (int i = 0; i < cursor.getCount(); i++) {
            File info = new File();
            String title = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.TITLE));
            String uri = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            int thumbnails_id = cursor.getInt(cursor.getColumnIndex(MediaStore.Images.Thumbnails._ID));
            long size = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media.SIZE));
            info.setFileName(title);
            info.setFileRealName(title + getTypeNameFromUrl(uri));
            info.setFileUrl(uri);
            info.setFileSize(size);

            //获取略缩图的另一个方法 清晰度提高 但是效率会低
            //info.setThumbnails(BitmapUtils.toByte(ThumbnailsUtil.decodeSampledBitmapFromFd(uri, 200, 200)));
            Bitmap bitmap = MediaStore.Images.Thumbnails.getThumbnail(context.getContentResolver(), thumbnails_id, MediaStore.Images.Thumbnails.MICRO_KIND, null);
            if (bitmap != null)
                info.setFileImg(ImageManageUtil.bitmap2Byte(bitmap));

            imgList.add(info);

            cursor.moveToNext();
        }
        cursor.close();
        return imgList;
    }

    @NonNull
    private String getTypeNameFromUrl(String uri) {
        return uri.substring(uri.lastIndexOf('.'));
    }

    public List<Multimedia> getAllVideo() {
        List<Multimedia> multList = new ArrayList<Multimedia>();
        ContentResolver contentResolver = context.getContentResolver();

        String[] projection = new String[]{MediaStore.Video.Media.TITLE, MediaStore.Video.Media.DATA,
                MediaStore.Video.Media.SIZE, MediaStore.Video.Media.DURATION, MediaStore.Audio.Media._ID};

        Cursor cursor = contentResolver.query(VIDEO_URI,
                projection, null, null, MediaStore.Video.Media.DEFAULT_SORT_ORDER);

        if (cursor.getCount() <= 0) {
            cursor = contentResolver.query(MediaStore.Video.Media.INTERNAL_CONTENT_URI,
                    projection, null, null, MediaStore.Video.Media.DEFAULT_SORT_ORDER);
        }
        cursor.moveToFirst();

        for (int i = 0; i < cursor.getCount(); i++) {
            Multimedia info = paseDataToMultimediaInfo(cursor, projection);

            //获取当前Video对应的Id，然后根据该ID获取其Thumb
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID));
            String selection = MediaStore.Video.Thumbnails.VIDEO_ID + "=?";
            String[] selectionArgs = new String[]{id + ""};

            Cursor thumbCursor = contentResolver.query(MediaStore.Video.Thumbnails.EXTERNAL_CONTENT_URI, new String[]{MediaStore.Video.Thumbnails.DATA}, selection, selectionArgs, null);
            String thumbPath = null;
            if (thumbCursor.moveToFirst()) {
                thumbPath = thumbCursor.getString(thumbCursor.getColumnIndexOrThrow(MediaStore.Video.Thumbnails.DATA));
            }
            if (thumbPath != null)
                info.setFileImg(ImageManageUtil.bitmap2Byte(ThumbnailsUtil.decodeSampledBitmapFromFd(thumbPath, 180, 100)));

            multList.add(info);
            cursor.moveToNext();
        }
        cursor.close();

        return multList;
    }

    public List getAllAudio() {

        List<Multimedia> multList = new ArrayList<Multimedia>();
        ContentResolver contentResolver = context.getContentResolver();

        String[] projection = new String[]{MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.SIZE, MediaStore.Audio.Media.DURATION, MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.ALBUM_ID, MediaStore.Audio.Media.ALBUM};
        Cursor cursor = contentResolver.query(AUDIO_URI,
                projection, null, null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);

        cursor.moveToFirst();

        for (int i = 0; i < cursor.getCount(); i++) {
            Multimedia info = paseDataToMultimediaInfo(cursor, projection);
            multList.add(info);
            cursor.moveToNext();
        }
        cursor.close();
        return multList;
    }


    @NonNull
    private Multimedia paseDataToMultimediaInfo(Cursor cursor, String[] projection) {

        Multimedia info = new Multimedia();
        String title = cursor.getString(cursor.getColumnIndex(projection[0]));
        L.e(title);
        String uri = cursor.getString(cursor.getColumnIndex(projection[1]));
        long size = cursor.getLong(cursor.getColumnIndex(projection[2]));
        long duration = cursor.getLong(cursor.getColumnIndex(projection[3]));
        info.setFileRealName(title + getTypeNameFromUrl(uri));
        info.setFileName(title);
        info.setFileUrl(uri);
        info.setFileSize(size);
        info.setMultimediaLength(DateUtil.formatDuring(duration));
        return info;
    }


}
