package com.example.shinz.gsdemo;

import android.view.View;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shinz.gsdemo.R;
import com.example.shinz.gsdemo.base.BaseFragment;

/**
 * Created by ShinZ on 2017/5/17.
 */

public class HomeFragment extends BaseFragment implements View.OnClickListener{
    private final static String TAG = HomeFragment.class.getSimpleName();

    private TextView tv_search_home;
    private TextView tv_message_home;
    private RecyclerView rv_home;
    private ImageView ib_top;

    @Override
    public View initView() {
        Log.e(TAG,"主页面的Fragment的UI被初始化了");
        View view = View.inflate(mContext,R.layout.fragment_home,null);
        //初始化布局控件
        tv_search_home = (TextView) view.findViewById(R.id.tv_search_home);
        tv_message_home = (TextView) view.findViewById(R.id.tv_message_home);
        rv_home = (RecyclerView) view.findViewById(R.id.rv_home);
        ib_top = (ImageView) view.findViewById(R.id.ib_top);

        //设置点击事件
        ib_top.setOnClickListener(this);
        tv_search_home.setOnClickListener(this);
        tv_message_home.setOnClickListener(this);
        return view;
    }


    @Override
    public void initData() {
        super.initData();
        Log.e(TAG,"主页面的Fragment的数据被初始化了");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.ib_top: //置顶的监听
                rv_home.scrollToPosition(0);
                break;
            case R.id.tv_search_home:  //搜索的监听
                Toast.makeText(mContext,"搜索",Toast.LENGTH_SHORT).show();
                break;
            case R.id.tv_message_home: //消息监听
                Toast.makeText(mContext,"进入消息中心",Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
