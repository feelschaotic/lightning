package com.ramo.cache;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.LruCache;
import android.widget.ImageView;

import com.ramo.adapter.SendAllImgAdapter;
import com.ramo.file_transfer.R;
import com.ramo.utils.ImageManageUtil;
import com.ramo.utils.StringUtil;

import java.util.HashSet;
import java.util.Set;

public class ImageLoader {

    public ImageView mImageView;
    private LruCache<String, Bitmap> mCache;
    public static final String TAG = "TAG";
    private RecyclerView recyclerView;
    private Set<ImageAsyncTask> mTasks;

    public ImageLoader(RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
        mTasks = new HashSet<ImageAsyncTask>();
        //确定缓存的大小
        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        int cacheSize = maxMemory / 4;

        mCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                // 每次存入缓存的时候调用，返回存入对象的大小。
                int byteCount = value.getByteCount();
                return byteCount;
            }
        };
    }

    //增加到缓存
    public void addBitmapToCache(String urlStr1, Bitmap bitmap) {
        if (getBitmapFromCache(urlStr1) == null) {
            mCache.put(urlStr1, bitmap);
        }
    }

    //从缓存中获取图片
    public Bitmap getBitmapFromCache(String url) {
        return mCache.get(url);
    }

    public void showImageByAsynctask(ImageView imageView, byte[] urlAsynctask) {
        Bitmap bitmapAsynctask = getBitmapFromCache(StringUtil.byteToStr(urlAsynctask));
        mImageView = imageView;
        // L.e(bitmapAsynctask+"|");
        if (bitmapAsynctask == null) {
            imageView.setImageResource(R.drawable.loading_img_gery);
        } else {
            mImageView.setImageBitmap(bitmapAsynctask);
        }

    }

    //根据起始位置加载特定的图片
    public void loadImages(int start, int end) {
        //  L.e("start:" + start + ";end:" + end);
        for (int i = start; i < end; i++) {
            String byteStr = SendAllImgAdapter.byteStrs[i];
            byte[] bytes = StringUtil.strToByte(byteStr);

            Bitmap bitmap = getBitmapFromCache(byteStr);
            //  L.e(bitmap+"||");
            if (bitmap == null) {
                ImageAsyncTask task = new ImageAsyncTask(bytes);
                task.execute(bytes);
                mTasks.add(task);
            } else {
                mImageView = (ImageView) recyclerView.findViewWithTag(byteStr);
                if (mImageView != null)
                    mImageView.setImageBitmap(bitmap);
            }
        }
    }

    public void cancelAllTasks() {
        if (mTasks != null) {
            for (ImageAsyncTask task : mTasks) {
                task.cancel(false);
            }
        }

    }

    class ImageAsyncTask extends AsyncTask<byte[], Void, Bitmap> {
        private byte[] imageByteStr;

        public ImageAsyncTask(byte[] byteStr) {
            imageByteStr = byteStr;
        }

        @Override
        protected Bitmap doInBackground(byte[]... params) {
            byte[] b = params[0];
            Bitmap bitmap = ImageManageUtil.Byte2Bitmap(b);

            if (bitmap != null) {
                addBitmapToCache(StringUtil.byteToStr(b), bitmap);
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);
            ImageView imageView = (ImageView) recyclerView.findViewWithTag(StringUtil.byteToStr(imageByteStr));
            if (imageView != null && result != null) {
                imageView.setImageBitmap(result);
            }
            mTasks.remove(this);
        }
    }
}
/*
private Handler mHandler=new Handler(){
	public void handleMessage(Message msg) {
		//只有ImageView的Tag与传入的url相同，才更新图片
		if(mImageView.getTag().equals(mUrl))
			mImageView.setImageBitmap((Bitmap) msg.obj);
	};

};
*/
/*
public void showImageThread(ImageView imageView,final String url){
	mImageView=imageView;
	mUrl=url;
	new Thread(){
		public void run() {
			Bitmap bitmap=getBitmapFromURL(url);
			Message message=Message.obtain();
			message.obj=bitmap;
			mHandler.sendMessage(message);
		};

	}.start();
}
*/