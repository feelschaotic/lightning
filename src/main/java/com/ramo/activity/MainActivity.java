package com.ramo.activity;

import android.animation.ObjectAnimator;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ramo.application.MyApplication;
import com.ramo.bean.File;
import com.ramo.file_transfer.R;
import com.ramo.fragment.AllAppFragment_;
import com.ramo.fragment.AllAudioFragment_;
import com.ramo.fragment.AllFileClassificationFragment_;
import com.ramo.fragment.AllImgFragment;
import com.ramo.fragment.AllImgFragment_;
import com.ramo.fragment.AllVideoFragment_;
import com.ramo.listener.BackHandlerInterface;
import com.ramo.service.ClipboardService;
import com.ramo.utils.ExtraName;
import com.ramo.utils.ImageManageUtil;
import com.ramo.utils.L;
import com.ramo.utils.SendMessageUtil;
import com.ramo.utils.ServerUtil;
import com.ramo.utils.T;
import com.ramo.view.SendFileShoppingCartDialog;
import com.ramo.view.TopBar;
import com.ramo.view.ViewPagerIndicator;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@EActivity(R.layout.activity_main)
public class MainActivity extends FragmentActivity implements BackHandlerInterface {

    /*topbar*/
    @ViewById
    TopBar top_bar;

    /*viewPager*/
    @ViewById(R.id.ViewPagerIndicator)
    ViewPagerIndicator ViewPagerIndicator;
    @ViewById(R.id.viewPager)
    ViewPager viewPager;
    @ViewById
    DrawerLayout drawerlayout;

    AllImgFragment selectedFragment;

    private List<Fragment> fragmentList = new ArrayList<Fragment>();
    private List<String> titleList = Arrays.asList("文档", "图片", "音乐", "视频", "应用", "其他");
    private FragmentPagerAdapter adapter;

    @ViewById
    public static
    LinearLayout send_files_shopping_cart_ll;
    static TextView selected_file_num;
    TextView begin_send_file;
    private static Animation selected_file_num_anim;
    private static Animation select_img_anim;

    @ViewById(R.id.animView)
    static
    ImageView anim_iv;


    @ViewById
    ListView left_menu_lv;
    @ViewById(R.id.left_nav_head)
    ImageView left_nav_head;
    private final int ContactsCode = 100;

    public static Map<String, String> ipMap;

    @AfterViews
    protected void init() {
        ipMap = new HashMap();
        initCart();
        initFragment();
        initTab();
        initData();
        MyApplication.sendList.clear();
        viewPager.setAdapter(adapter);

        //  check_tag_video = new int[MyApplication.allVidepList.size()];
        // check_tag_audio = new int[MyApplication.allAudioList.size()];

        select_img_anim = AnimationUtils.loadAnimation(this, R.anim.select_img_mobile_animation);

        //viewPager.setCurrentItem(4);

    }

    private void initClipboardService() {
        startService(new Intent(this, Clipboard Service.class));
    }

    private void initData() {
        String[] data = {"语音助手", "连接打印机", "剪贴板共享", "通讯录分享", "pc测试上传"};
        left_menu_lv.setAdapter(new ArrayAdapter<String>(this, R.layout.left_nav_menu_item, R.id.left_menu_tv, data));
        left_menu_lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = null;
                switch (position) {
                    case 0:
                        startActivity(new Intent(MainActivity.this, VoiceActivity.class));
                        break;
                    case 1:
                        intent = new Intent(MainActivity.this, WebServiceActivity_.class);
                        intent.putExtra(ServerUtil.URL, "http://15500p32q9.51mypc.cn/printFile.html");
                        startActivity(intent);
                        break;
                    case 2:
                        initClipboardService();
                        T.showShort(MainActivity.this,"监听中");
                        break;
                    case 3:
                        //打开系统联系人，查找
                        intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                        startActivityForResult(intent, ContactsCode);
                        break;
                    case 4:
                        intent = new Intent(MainActivity.this, WebServiceActivity_.class);
                        intent.putExtra(ServerUtil.URL, ServerUtil.UPLOAD_URL);
                        startActivity(intent);
                        break;

                }
            }
        });
    }

    private void initCart() {

        selected_file_num = (TextView) send_files_shopping_cart_ll.findViewById(R.id.selected_file_num);
        begin_send_file = (TextView) send_files_shopping_cart_ll.findViewById(R.id.begin_send_file);
        selected_file_num_anim = AnimationUtils.loadAnimation(this, R.anim.selected_file_num_anim);
    }


    private void initFragment() {

        Fragment fragment5 = new AllAppFragment_();
        Fragment fragment2 = new AllImgFragment_();
        Fragment fragment3 = new AllAudioFragment_();
        Fragment fragment4 = new AllVideoFragment_();
        Fragment fragment1 = new AllFileClassificationFragment_();
        fragmentList.add(fragment1);
        fragmentList.add(fragment2);
        fragmentList.add(fragment3);
        fragmentList.add(fragment4);
        fragmentList.add(fragment5);

        adapter = new FragmentPagerAdapter(getSupportFragmentManager()) {

            @Override
            public int getCount() {
                return fragmentList.size();
            }

            @Override
            public Fragment getItem(int position) {
                return fragmentList.get(position);
            }
        };

        initListener();
    }

    private void initListener() {
        top_bar.setOnTopBarClickListener(new TopBar.TopBarClickListener() {
            @Override
            public void leftClick() {
                drawerlayout.openDrawer(Gravity.LEFT);
            }

            @Override
            public void RightClick() {
                startActivity(new Intent(MainActivity.this, HistoricalRecordActivity_.class));
            }
        });
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                ViewPagerIndicator.scroll(position, positionOffset);
            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        begin_send_file.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              /*  if (!MyApplication.connectSuccessd) {
                    T.showShort(MainActivity.this, "请先连接好友");
                    return;
                }*/

                Intent intent = new Intent(view.getContext(), HistoricalRecordActivity_.class);
                intent.putExtra(MyApplication.INTENT_EXTRA_NAME, (Serializable) MyApplication.sendList);
                startActivity(intent);
            }
        });

        selected_file_num.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendFileShoppingCartDialog.Builder builder = new SendFileShoppingCartDialog.Builder(MainActivity.this);
                builder.setData(new ArrayList<File>(MyApplication.sendList));
                builder.create().show();
            }
        });
        left_nav_head.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, EditHeadAndNameActivity_.class));
            }
        });
    }

    public static void changeSendFileNum() {
        selected_file_num.setText("已选（" + MyApplication.sendList.size() + "）");
        selected_file_num.startAnimation(selected_file_num_anim);
        //  L.e(send_files_shopping_cart_ll.getTranslationY() + "");
        if (MyApplication.sendList.size() == 0)
            ObjectAnimator.ofFloat(send_files_shopping_cart_ll, "translationY", send_files_shopping_cart_ll.getTranslationY(), send_files_shopping_cart_ll.getTranslationY() + send_files_shopping_cart_ll.getHeight()).setDuration(300).start();
    }
    public static void removeSendListAndChangeStyle(File file) {
        MyApplication.sendList.remove(file);
        changeSendFileNum();
    }

    public static void addSendListAndChangeStyle(ImageView imageView, File file) {
        if (!MyApplication.sendList.contains(file)) {
            MyApplication.sendList.add(file);
        }
        MainActivity.changeSendFileNum();
        isListOneAnim();
        changeStyle(imageView);
    }

    public static void changeStyle(ImageView imageView) {
        if (imageView == null)
            return;
        imageView.setDrawingCacheEnabled(true);
        anim_iv.setImageBitmap(Bitmap.createBitmap(imageView.getDrawingCache()));
        imageView.setDrawingCacheEnabled(false);

        int[] location = new int[2];
        imageView.getLocationOnScreen(location);
        int x = location[0];
        int y = location[1];
        //  L.e("left and top:" + x + ":" + y);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(imageView.getWidth(), imageView.getHeight());
        layoutParams.setMargins(x, y, 0, 0);
        anim_iv.setLayoutParams(layoutParams);
        anim_iv.setVisibility(View.VISIBLE);

        anim_iv.startAnimation(select_img_anim);
    }


    private void initAnim() {
        if (MyApplication.isSendingFiles) {
            top_bar.getRightBtn().setImageResource(R.anim.sending_files_anim);
            AnimationDrawable animationDrawable = (AnimationDrawable) top_bar.getRightBtn().getDrawable();
            animationDrawable.start();
        } else {
            top_bar.setRightBg(ImageManageUtil.RToDrawable(R.drawable.main_topbar_install));
        }
    }

    public static void isListOneAnim() {
        if (MyApplication.sendList.size() == 1)
            ObjectAnimator.ofFloat(MainActivity.send_files_shopping_cart_ll, "translationY", MainActivity.send_files_shopping_cart_ll.getTranslationY(), MainActivity.send_files_shopping_cart_ll.getTranslationY() - MainActivity.send_files_shopping_cart_ll.getHeight()).setDuration(300).start();
    }

    private void initTab() {
        ViewPagerIndicator.setVisibleTagCount(4);
        ViewPagerIndicator.setTabItemTitles(titleList);
        ViewPagerIndicator.setView_pager(viewPager);
    }

    @Click(R.id.main_topbar_send_file_tv)
    public void topbarSendTvClick() {
        Intent intent = new Intent();
        intent.setClass(this, RadarAndSweepActivtiy_.class);
        intent.putExtra(ExtraName.SERVER_RO_CLIENT, ExtraName.SERVER);
        startActivity(intent);
    }

    @Click(R.id.main_topbar_receive_file_tv)
    public void topbarReceiveTvClick() {
        //+了一步连接的界面
        Intent intent = new Intent(this, RadarAndSweepActivtiy_.class);
        intent.putExtra(ExtraName.SERVER_RO_CLIENT, ExtraName.CLIENT);
        startActivity(intent);

    }

    @Override
    public void onBackPressed() {
        if (selectedFragment == null || !selectedFragment.onBackPressed()) {
            super.onBackPressed();
        }
    }

    @Override
    public void setSelectedFragment(AllImgFragment backHandledFragment) {
        this.selectedFragment = backHandledFragment;
    }

    @Override
    protected void onResume() {
        super.onResume();
        initAnim();
    }

    @Override
    protected void finalize() throws Throwable {
        L.e("MainActivity:" + getResources().getString(R.string.NoMemoryLeak));
        super.finalize();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case ContactsCode:
                if (data != null) {
                    getContactsInfo(data);
                }
                break;
        }
    }

    private void getContactsInfo(Intent data) {
        Uri uri = data.getData();
        ContentResolver resolver = getContentResolver();
        Cursor cursor = resolver.query(uri, null, null, null, null);
        while (cursor.moveToNext()) {
            //取得联系人信息
            int columnNameIndex = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
            String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));

            String name = cursor.getString(columnNameIndex);
            Cursor phoneCur = resolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId, null, null);
            if (phoneCur.moveToNext()) {
                String phone = phoneCur.getString(phoneCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                sendSMS(phone);
                phoneCur.close();
            }
        }
        cursor.close();

    }

    private void sendSMS(String phone) {
        SendMessageUtil.sendSMS(phone);
    }

    @Click(R.id.main_topbar_head)
    public void openLeftMenu() {
        drawerlayout.openDrawer(Gravity.LEFT);
    }
}
