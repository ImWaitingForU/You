package com.chan.you;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.chan.you.bean.MenuBean;

import java.util.ArrayList;

import static com.chan.you.CommonData.F_PHONE;
import static com.chan.you.CommonData.F_QQ;
import static com.chan.you.CommonData.F_WEIBO;
import static com.chan.you.CommonData.WEIXIN_CHATTING_MIMETYPE;
import static com.chan.you.CommonData.WEIXIN_SNS_MIMETYPE;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "You";
    private RecyclerView mRecyclerView;
    private ArrayList<MenuBean> mMenus;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_main);

        setTitle ("成熟稳重的冯老湿");

        mMenus = new ArrayList<> ();
        mMenus.add (new MenuBean ("给他打电话", R.drawable.ic_call));
        mMenus.add (new MenuBean ("给他发短信", R.drawable.ic_sms));
        mMenus.add (new MenuBean ("Q他", R.drawable.ic_qq));
        mMenus.add (new MenuBean ("微信他", R.drawable.ic_wechat));
        mMenus.add (new MenuBean ("看他微博", R.drawable.ic_weibo));
        mMenus.add (new MenuBean ("添加更多", R.drawable.ic_add));

        mRecyclerView = (RecyclerView) findViewById (R.id.rv);
        mRecyclerView.setLayoutManager (new GridLayoutManager (this, 2, LinearLayoutManager.VERTICAL, false));
        DividerItemDecoration itemDecoration1 = new DividerItemDecoration (this, DividerItemDecoration.HORIZONTAL);
        DividerItemDecoration itemDecoration2 = new DividerItemDecoration (this, DividerItemDecoration.VERTICAL);
        mRecyclerView.addItemDecoration (itemDecoration1);
        mRecyclerView.addItemDecoration (itemDecoration2);
        MyAdapter adapter = new MyAdapter ();
        mRecyclerView.setAdapter (adapter);
        mRecyclerView.setHasFixedSize (false);
        mRecyclerView.setLayoutFrozen (false);
        mRecyclerView.setNestedScrollingEnabled (false);
        mRecyclerView.addOnItemTouchListener (new OnItemChildClickListener () {
            @Override
            public void SimpleOnItemChildClick (BaseQuickAdapter baseQuickAdapter, View view, int i) {
                getYou (i);
            }
        });

    }

    private void getYou (int i) {
        Intent intent;
        switch (i) {
            case 0:
                //打电话
                intent = new Intent (Intent.ACTION_DIAL, Uri.parse ("tel:" + F_PHONE));
                startActivity (intent);
                break;
            case 1:
                //发短信
                intent = new Intent (Intent.ACTION_SENDTO, Uri.parse ("smsto:" + F_PHONE));
                //intent.putExtra("sms_body", message);
                startActivity (intent);
                break;
            case 2:
                //QQ:根据qq号跳转到聊天界面
                if (checkApkExist (this, CommonData.QQ_APP_PACKAGE)) {
                    startActivity (new Intent (Intent.ACTION_VIEW, Uri.parse (
                            "mqqwpa://im/chat?chat_type=wpa&uin=" + F_QQ + "&version=1")));
                } else {
                    Toast.makeText (this, "本机未安装QQ应用", Toast.LENGTH_SHORT).show ();
                }
                break;
            case 3:
                //微信：通过电话查询微信号,根据微信号跳转到聊天界面
                if (checkApkExist (this, CommonData.WECHAT_APP_PACKAGE)) {
                    shareToFriend (this, getChattingID (this, F_PHONE, WEIXIN_CHATTING_MIMETYPE));
                    Log.d (TAG, "getYou: id=" + getChattingID (this, F_PHONE, WEIXIN_SNS_MIMETYPE));
                } else {
                    Toast.makeText (this, "本机未安装微信应用", Toast.LENGTH_SHORT).show ();
                }
                break;
            case 4:
                //微博:根据uid号跳转到用户界面，uid通过weibo的log查看
                if (checkApkExist (this, CommonData.WEIBO_APP_PACKAGE)) {
                    intent = new Intent (Intent.ACTION_VIEW);
                    intent.setData (Uri.parse ("sinaweibo://userinfo?uid=" + F_WEIBO));
                    Intent chooseIntent = Intent.createChooser (intent, "Weibo");
                    startActivity (chooseIntent);
                } else {
                    Toast.makeText (this, "本机未安装新浪微博应用", Toast.LENGTH_SHORT).show ();
                }
                break;
            case 5:
                Toast.makeText (this, "尽请期待~", Toast.LENGTH_SHORT).show ();
                break;
            default:
                break;
        }
    }

    /**
     * 进去聊天界面
     *
     * @param context
     * @param id      手机通讯录中版本的微信的自动增长列
     */
    public static void shareToFriend (Context context, int id) {
        Intent intent = new Intent (Intent.ACTION_VIEW);
        intent.setFlags (Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType (Uri.withAppendedPath (ContactsContract.Data.CONTENT_URI, String.valueOf (id)),
                               WEIXIN_CHATTING_MIMETYPE);
        context.startActivity (intent);
    }

    /**
     * 根据电话号码查询微信id
     **/
    public static int getChattingID (Context context, String querymobile, String mimeType) {
        if (context == null || querymobile == null || querymobile.equals ("")) {
            return -1;
        }
        ContentResolver resolver = context.getContentResolver ();
        Uri uri = Uri.parse ("content://com.android.contacts/data");
        StringBuilder sb = new StringBuilder ();
        sb.append (ContactsContract.Data.MIMETYPE).append (" = ").append ("'");
        sb.append (mimeType).append ("'");
        sb.append (" AND ").append ("replace(data1,' ','')").append (" = ").append ("'").append (querymobile)
          .append ("'");
        Cursor cursor = resolver.query (uri, new String[] {ContactsContract.Data._ID}, sb.toString (), null, null);
        Log.d (TAG, "getChattingID: sb=" + sb);
        Log.d (TAG, "getChattingID: cursor" + cursor.toString ());
        while (cursor.moveToNext ()) {
            int wexin_id = cursor.getInt (cursor.getColumnIndex (ContactsContract.Data._ID));
            Log.d (TAG, "getChattingID: weixinId=" + wexin_id);
            return wexin_id;
        }
        cursor.close ();
        return -1;
    }


    /**
     * 检查应用是否存在
     *
     * @param context
     * @param packageName
     * @return
     */
    public boolean checkApkExist (Context context, String packageName) {
        if (packageName == null || "".equals (packageName)) { return false; }
        try {
            ApplicationInfo info = context.getPackageManager ()
                                          .getApplicationInfo (packageName, PackageManager.GET_UNINSTALLED_PACKAGES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }


    private class MyAdapter extends BaseQuickAdapter<MenuBean> {

        public MyAdapter () {
            super (R.layout.menu_layout, mMenus);
        }

        @Override
        protected void convert (BaseViewHolder baseViewHolder, MenuBean menuBean) {
            baseViewHolder.setText (R.id.tvMenu, menuBean.getTitle ())
                          .setImageResource (R.id.ivMenu, menuBean.getImgId ()).addOnClickListener (R.id.menu);
        }
    }

}
