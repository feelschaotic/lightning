// Copyright 2011 Google Inc. All Rights Reserved.

package com.ramo.service;

import android.app.IntentService;
import android.content.Intent;

import com.ramo.asynctask.FileServerAsyncTask;
import com.ramo.utils.L;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * A service that process each file transfer request i.e Intent by opening a
 * socket connection with the WiFi Direct Group Owner and writing the file
 */
public class FileTransferService extends IntentService {

    private static final int SOCKET_TIMEOUT = 5000;
    public static final String ACTION_SEND_FILE = "com.example.android.wifidirect.SEND_FILE";
    public static final String EXTRAS_FILE_PATH = "sf_file_url";
    public static final String EXTRAS_GROUP_OWNER_ADDRESS = "sf_go_host";
    public static final String EXTRAS_GROUP_OWNER_PORT = "sf_go_port";

    public FileTransferService(String name) {
        super(name);
    }

    public FileTransferService() {
        super("FileTransferService");
    }

    /*
     * (non-Javadoc)
     *
     * @see android.app.IntentService#onHandleIntent(android.content.Intent)
     */
    @Override
    protected void onHandleIntent(Intent intent) {

        if (intent.getAction().equals(ACTION_SEND_FILE)) {
            String fileUri = intent.getExtras().getString(EXTRAS_FILE_PATH);
            String host = intent.getExtras().getString(
                    EXTRAS_GROUP_OWNER_ADDRESS);

            Socket socket = new Socket();

           int port = intent.getExtras().getInt(EXTRAS_GROUP_OWNER_PORT);
          //  7000
            try {
             //   L.d("Opening client socket - ");
                socket.bind(null);
            //    L.d("!!!host"+host);
                if(host==null)
                    return;
                socket.connect((new InetSocketAddress(host, port)),
                        SOCKET_TIMEOUT);

                L.d("Client socket - " + socket.isConnected());
                OutputStream out = socket.getOutputStream();
                InputStream is = null;
                /*returns an output stream to write data into this socket*/
                    //  ContentResolver cr = context.getContentResolver();

                    try {
                        //is = cr.openInputStream(Uri.parse(fileUri));
                        is = new FileInputStream(new File(fileUri));
                      //  L.e("fileUri:"+fileUri);
                     //   L.e("FileInputStream:"+is);
                    } catch (FileNotFoundException e) {
                        L.e(e.toString());
                    }
                    FileServerAsyncTask.copyFile(is, out);
              //  L.e("Client: Data written");
            } catch (IOException e) {
                L.e(e.getMessage());
            } finally {
                try {

                    if (socket != null)
                        if (socket.isConnected())
                            socket.close();

                } catch (IOException e) {
                    // Give up
                    e.printStackTrace();
                }

            }

        }
    }
}
