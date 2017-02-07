package com.chan.you;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.chan.you.bean.MenuBean;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private ArrayList<MenuBean> mMenus;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_main);

        mMenus = new ArrayList<> ();
        mMenus.add (new MenuBean ("打电话", R.drawable.ic_call));
        mMenus.add (new MenuBean ("发短信", R.drawable.ic_sms));
        mMenus.add (new MenuBean ("QQ", R.drawable.ic_qq));
        mMenus.add (new MenuBean ("微信", R.drawable.ic_wechat));
        mMenus.add (new MenuBean ("微博", R.drawable.ic_weibo));
        mMenus.add (new MenuBean ("陌陌", R.drawable.ic_momo));

        mRecyclerView = (RecyclerView) findViewById (R.id.rv);
        mRecyclerView.setLayoutManager (new GridLayoutManager (this, 2));
        DividerItemDecoration itemDecoration1 = new DividerItemDecoration (this, DividerItemDecoration.HORIZONTAL);
        DividerItemDecoration itemDecoration2 = new DividerItemDecoration (this, DividerItemDecoration.VERTICAL);
        mRecyclerView.addItemDecoration (itemDecoration1);
        mRecyclerView.addItemDecoration (itemDecoration2);
        MyAdapter adapter = new MyAdapter ();
        mRecyclerView.setAdapter (adapter);
        mRecyclerView.addOnItemTouchListener (new OnItemChildClickListener () {
            @Override
            public void SimpleOnItemChildClick (BaseQuickAdapter baseQuickAdapter, View view, int i) {

            }
        });

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
