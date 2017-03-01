package com.chan.you;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.chan.you.bean.MenuBean;
import com.chan.you.utils.SpUtils;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import static com.chan.you.CommonData.WECHAT_ID;
import static com.chan.you.CommonData.WEIXIN_CHATTING_MIMETYPE;
import static com.chan.you.CommonData.WEIXIN_SNS_MIMETYPE;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "You";
    //    private static final int REQUEST_CODE = 0X111;

    private IWXAPI wxApi;

    private RecyclerView mRecyclerView;
    private ArrayList<MenuBean> mMenus;

    private SharedPreferences sp;
    private static ProgressDialog dialog;

    private MyHandler mHandler = new MyHandler (this);
    private static final int DIALOG_DISMISS_MESSAGE = 0x123;

    private static class MyHandler extends Handler {
        private WeakReference<Context> reference;

        public MyHandler (Context context) {
            reference = new WeakReference<> (context);
        }

        @Override
        public void handleMessage (Message msg) {
            MainActivity activity = (MainActivity) reference.get ();
            if (activity != null && msg.what == DIALOG_DISMISS_MESSAGE) {
                if (dialog != null && dialog.isShowing ()) {
                    dialog.dismiss ();
                }
            }
        }
    }

    @Override
    protected void onPause () {
        super.onPause ();
        Message msg = new Message ();
        msg.what = DIALOG_DISMISS_MESSAGE;
        mHandler.sendMessage (msg);
    }

    @Override
    protected void onDestroy () {
        super.onDestroy ();
        mHandler.removeCallbacksAndMessages (null);
    }

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_main);
        sp = getSharedPreferences (SpUtils.SP_NAME, MODE_PRIVATE);

        wxApi = WXAPIFactory.createWXAPI (this, WECHAT_ID, true);
        wxApi.registerApp (WECHAT_ID);

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

    @Override
    protected void onResume () {
        super.onResume ();
        String name = SpUtils.getString (sp, SpUtils.KEY_NAME);
        if (name == null || name.equals ("")) {
            setTitle ("他/她");
        } else {
            setTitle (name);
        }

    }

    @Override
    public boolean onCreateOptionsMenu (Menu menu) {
        MenuInflater inflater = getMenuInflater ();
        inflater.inflate (R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item) {
        if (item.getItemId () == R.id.settings) {
            Intent intent = new Intent (this, SettingActivity.class);
            startActivity (intent);
        }
        return true;
    }

    //    @Override
    //    protected void onActivityResult (int requestCode, int resultCode, Intent data) {
    //        if (requestCode==REQUEST_CODE){
    //
    //        }
    //    }

    private void getYou (int i) {
        Intent intent;
        switch (i) {
            case 0:
                //打电话
                if (!SpUtils.getPhone (sp).equals ("")) {
                    intent = new Intent (Intent.ACTION_DIAL, Uri.parse ("tel:" + SpUtils.getPhone (sp)));
                    showLoadingDialog ();
                    startActivity (intent);
                } else {
                    Toast.makeText (this, "还没有添加手机号，请到设置界面添加", Toast.LENGTH_SHORT).show ();
                }
                break;
            case 1:
                //发短信
                if (!SpUtils.getPhone (sp).equals ("")) {
                    intent = new Intent (Intent.ACTION_SENDTO, Uri.parse ("smsto:" + SpUtils.getPhone (sp)));
                    showLoadingDialog ();
                    startActivity (intent);
                } else {
                    Toast.makeText (this, "还没有添加手机号，请到设置界面添加", Toast.LENGTH_SHORT).show ();
                }
                break;
            case 2:
                if (!SpUtils.getQQ (sp).equals ("")) {
                    //QQ:根据qq号跳转到聊天界面
                    if (checkApkExist (this, CommonData.QQ_APP_PACKAGE)) {
                        showLoadingDialog ();
                        startActivity (new Intent (Intent.ACTION_VIEW, Uri.parse (
                                "mqqwpa://im/chat?chat_type=wpa&uin=" + SpUtils.getQQ (sp) + "&version=1")));

                    } else {
                        Toast.makeText (this, "本机未安装QQ应用", Toast.LENGTH_SHORT).show ();
                    }
                } else {
                    Toast.makeText (this, "还没有添加QQ，请到设置界面添加", Toast.LENGTH_SHORT).show ();
                }
                break;
            case 3:
                if (!SpUtils.getPhone (sp).equals ("")) {
                    //微信：通过电话查询微信号,根据微信号跳转到聊天界面
                    if (checkApkExist (this, CommonData.WECHAT_APP_PACKAGE)) {
                        shareToFriend (this, getChattingID (this, SpUtils.getPhone (sp), WEIXIN_CHATTING_MIMETYPE));
                        showLoadingDialog ();
                        Log.d (TAG,
                               "getYou: id=" + getChattingID (this, SpUtils.getPhone (sp), WEIXIN_CHATTING_MIMETYPE));
                        Toast.makeText (this, "暂时只支持跳转到微信", Toast.LENGTH_SHORT).show ();

                        //                        WXTextObject textObject = new WXTextObject ();
                        //                        textObject.text = "aaa";
                        //                        WXMediaMessage msg = new WXMediaMessage ();
                        //                        msg.mediaObject = textObject;
                        //                        msg.description = "aaa";
                        //
                        //                        SendMessageToWX.Req req = new SendMessageToWX.Req ();
                        ////                        req.transaction = buildTransaction();
                        //                        req.message = msg;
                        //                        req.scene = SendMessageToWX.Req.WXSceneSession;
                        //
                        //                        wxApi.sendReq (req);


                    } else {
                        Toast.makeText (this, "本机未安装微信应用", Toast.LENGTH_SHORT).show ();
                    }
                } else {
                    Toast.makeText (this, "微信根据手机号匹配，请到设置界面添加手机号", Toast.LENGTH_SHORT).show ();
                }

                break;
            case 4:
                if (!SpUtils.getWeibo (sp).equals ("")) {
                    //微博:根据昵称/uid号跳转到用户界面，uid通过weibo的log查看
                    if (checkApkExist (this, CommonData.WEIBO_APP_PACKAGE)) {
                        intent = new Intent (Intent.ACTION_VIEW);
                        // intent.setData (Uri.parse ("sinaweibo://userinfo?uid=" + F_WEIBO));
                        intent.setData (Uri.parse ("sinaweibo://userinfo?nickname=" + SpUtils.getWeibo (sp)));
                        Intent chooseIntent = Intent.createChooser (intent, "Weibo");
                        showLoadingDialog ();
                        startActivity (chooseIntent);
                    } else {
                        Toast.makeText (this, "本机未安装新浪微博应用", Toast.LENGTH_SHORT).show ();
                    }
                } else {
                    Toast.makeText (this, "还没有添加微博，请到设置界面添加", Toast.LENGTH_SHORT).show ();
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
        Log.d (TAG, "shareToFriend: intent:" + intent.toString ());
        context.startActivity (intent);
    }

    /**
     * 朋友圈
     *
     * @param context
     * @param id
     */
    public static void shareToTimeLine (Context context, int id) {
        Intent intent = new Intent (Intent.ACTION_VIEW);
        intent.setFlags (Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType (Uri.withAppendedPath (ContactsContract.Data.CONTENT_URI, String.valueOf (id)),
                               WEIXIN_SNS_MIMETYPE);
        context.startActivity (intent);
    }


    /**
     * 根据电话号码查询微信id
     **/
    public static int getChattingID (Context context, String querymobile, String mimeType) {
        if (context == null || querymobile == null || querymobile.equals ("")) {
            return 0;
        }
        ContentResolver resolver = context.getContentResolver ();
        Uri uri = Uri.parse ("content://com.android.contacts/data");
        StringBuilder sb = new StringBuilder ();
        sb.append (ContactsContract.Data.MIMETYPE).append (" = ").append ("'");
        sb.append (mimeType).append ("'");
        sb.append (" AND ").append ("replace(data1,' ','')").append (" = ").append ("'")
          .append (String.valueOf (querymobile)).append ("'");
        Log.d (TAG, "getChattingID: ---------uri---------" + uri);
        Log.d (TAG, "getChattingID: ---------sb---------" + sb.toString ());
        Cursor cursor = resolver.query (uri, new String[] {ContactsContract.Data._ID}, sb.toString (), null, null);
        Log.d (TAG, "getChattingID: ---------cursor---------" + cursor.toString ());
        for (int i = 0; i < cursor.getColumnNames ().length; i++) {
            Log.d (TAG, "getChattingID: columnName:" + cursor.getColumnNames ()[i]);
            Log.d (TAG, "getChattingID: cursor.getCount ()" + cursor.getCount ());
        }
        while (cursor.moveToNext ()) {
            int wexin_id = cursor.getInt (cursor.getColumnIndex (ContactsContract.Data._ID));
            return wexin_id;
        }
        cursor.close ();
        return 0;
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

    private void showLoadingDialog () {
        if (dialog == null) {
            dialog = new ProgressDialog (this);
            dialog.setMessage ("正在跳转...");
            dialog.setCancelable (false);
        }
        dialog.show ();
    }

}
