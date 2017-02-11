package com.ramo.fragment;

/**
 * Created by ramo on 2016/7/22.
 */
public class CheckTag {
    public static int[] check_tag_img;
    public static int[] check_tag_img_folder;
    public static int[] check_tag_app;
    public static int[] check_tag_audio;
    public static int[] check_tag_video;

    public static boolean isTagNull(int[] check_tag) {
        return check_tag == null || check_tag.length == 0;
    }
}
