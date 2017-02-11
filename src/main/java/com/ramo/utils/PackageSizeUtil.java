package com.ramo.utils;

import android.content.Context;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.os.RemoteException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ramo on 2016/3/30.
 */
public class PackageSizeUtil {

    private static Context context;
    private long cachesize; //缓存大小
    private long datasize;  //数据大小
    private long codesize;  //应用程序大小
    private long totalsize; //总大小
    private List<Long> allAppSizeList;

    public List<Long> getAllAppSizeList() {
        return allAppSizeList;
    }

    public PackageSizeUtil(Context context) {
        this.context = context;
        allAppSizeList=new ArrayList<Long>();
    }

    public void getPacakgeSize(String packageName) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        if (packageName != null) {
            //使用反射机制得到PackageManager类的隐藏函数getPackageSizeInfo
            PackageManager packageManager = context.getPackageManager();  //得到pm对象
            Method method = packageManager.getClass().getMethod("getPackageSizeInfo", String.class, IPackageStatsObserver.class);
            PackageSizeObserver packageSizeObserver = new PackageSizeObserver();
            method.invoke(packageManager, packageName, packageSizeObserver);
        }
    }

    //aidl文件形成的Bindler机制服务类
    class PackageSizeObserver extends IPackageStatsObserver.Stub {
        @Override
        public void onGetStatsCompleted(PackageStats stats, boolean succeeded) throws RemoteException {
            cachesize = stats.cacheSize;
            codesize = stats.codeSize;
            datasize = stats.dataSize;
            totalsize = cachesize + codesize + datasize;
            allAppSizeList.add(totalsize);
        }
    }


}
