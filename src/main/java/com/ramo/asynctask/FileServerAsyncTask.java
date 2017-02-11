package com.ramo.asynctask;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.widget.Toast;

import com.ramo.activity.HistoricalRecordActivity_;
import com.ramo.application.MyApplication;
import com.ramo.utils.NetStateUtil;
import com.ramo.utils.T;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * A simple server socket that accepts connection and writes some data on
 * the stream.
 */
public class FileServerAsyncTask extends
        AsyncTask<Void, Void, String> {

    private Context context;

    /**
     * @param context
     */
    public FileServerAsyncTask(Context context) {
        this.context = context;
    }

    @Override
    protected String doInBackground(Void... params) {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(7000);
            Socket client = serverSocket.accept();
            final File f = new File(
                    Environment.getExternalStorageDirectory() + "/"
                            + "Download" + "/wifip2pshared-"
                            + System.currentTimeMillis() + ".jpg");

            File dirs = new File(f.getParent());

            if (!dirs.exists())
                dirs.mkdirs();
            f.createNewFile();


                /*Returns an input stream to read data from this socket*/
            InputStream inputstream = client.getInputStream();
            copyFile(inputstream, new FileOutputStream(f));
            serverSocket.close();
            return f.getAbsolutePath();

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
     */
    @Override
    protected void onPostExecute(String result) {

        Toast.makeText(context, "result" + result, Toast.LENGTH_SHORT).show();
        if (result != null) {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.parse("file://" + result), "image/*");
            context.startActivity(intent);
        } else {
            jumpOfClient();
        }

    }

    private void jumpOfClient() {

        NetStateUtil netStateUtil = new NetStateUtil(context);
        //接着再开启接收的客户端
        if (netStateUtil.isNetworkConnected()) {
            T.showShort(context, "使用P2P加速中");
            new BTAsyncTask().execute();
        } else {
            if (MyApplication.groupList.size() <= 1) {
                T.showShort(context, "接收中。。");
                new ReceiveFileAsync(context).execute();
            } else {
                T.showShort(context, "群组接收中。。");
                for (String ip : MyApplication.groupList) {
                    new ReceiveFileAsync(context).execute(ip);
                }
            }
        }
        context.startActivity(new Intent(context, HistoricalRecordActivity_.class));
    }

    /*
     * (non-Javadoc)
     *
     * @see android.os.AsyncTask#onPreExecute()
     */
    @Override
    protected void onPreExecute() {

    }


    public static boolean copyFile(InputStream inputStream, OutputStream out) {
        byte buf[] = new byte[1024];
        int len;
        try {
            while ((len = inputStream.read(buf)) != -1) {
                out.write(buf, 0, len);

            }
            out.close();
            inputStream.close();
        } catch (IOException e) {
            return false;
        }
        return true;
    }
}