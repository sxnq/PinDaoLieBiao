package xunqaing.bwie.com.pindaoliebiao;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;
import java.util.ArrayList;

import xunqaing.bwie.com.pindaoliebiao.newsdrag.adapter.DragAdapter;
import xunqaing.bwie.com.pindaoliebiao.newsdrag.adapter.OtherAdapter;
import xunqaing.bwie.com.pindaoliebiao.newsdrag.bean.ChannelItem;
import xunqaing.bwie.com.pindaoliebiao.newsdrag.bean.ChannelManage;
import xunqaing.bwie.com.pindaoliebiao.newsdrag.view.DragGrid;
import xunqaing.bwie.com.pindaoliebiao.newsdrag.view.OtherGridView;


/**
 * 频道管理
 * @Author RA
 * @Blog http://blog.csdn.net/vipzjyno1
 */
public class MainActivity extends Activity implements OnItemClickListener {
    /** 用户栏目的GRIDVIEW */
    private DragGrid userGridView;
    /** 其它栏目的GRIDVIEW */
    private OtherGridView otherGridView;
    /** 用户栏目对应的适配器，可以拖动 */
    DragAdapter userAdapter;
    /** 其它栏目对应的适配器 */
    OtherAdapter otherAdapter;
    /** 其它栏目列表 */
    ArrayList<ChannelItem> otherChannelList = new ArrayList<ChannelItem>();
    /** 用户栏目列表 */
    ArrayList<ChannelItem> userChannelList = new ArrayList<ChannelItem>();
    /** 是否在移动，由于这边是动画结束后才进行的数据更替，设置这个限制为了避免操作太频繁造成的数据错乱。 */
    boolean isMove = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.subscribe_activity);
        initView();
        initData();


        userGridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            private RadioButton rb_phoneliuliang;
            private RadioButton wifi;

            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                //设置图片
                builder.setIcon(R.mipmap.ic_launcher);
                //设置标题
                builder.setTitle("网络选择");
                View view1 = View.inflate(MainActivity.this, R.layout.dialog, null);
                builder.setView(view1);

                builder.show();

                wifi = (RadioButton) view1.findViewById(R.id.rb_wifi);
                rb_phoneliuliang = (RadioButton) view1.findViewById(R.id.rb_phoneliuliang);
                wifi.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);

                        View view1 = View.inflate(MainActivity.this, R.layout.wifi_dialog, null);

                        builder1.setView(view1);
                        builder1.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        });
                        builder1.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                downloadApk();
                            }
                        });
                        builder1.show();
                    }
                });
                rb_phoneliuliang.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(MainActivity.this, "跳转到设置WiFi页面", Toast.LENGTH_SHORT).show();
                    }
                });
                return false;
            }
        });
    }

    /** 初始化数据*/
    private void initData() {
        userChannelList = ((ArrayList<ChannelItem>) ChannelManage.getManage(IApplication.getApp().getSQLHelper()).getUserChannel());
        otherChannelList = ((ArrayList<ChannelItem>)ChannelManage.getManage(IApplication.getApp().getSQLHelper()).getOtherChannel());
        userAdapter = new DragAdapter(this, userChannelList);
        userGridView.setAdapter(userAdapter);
        otherAdapter = new OtherAdapter(this, otherChannelList);
        otherGridView.setAdapter(this.otherAdapter);
        //设置GRIDVIEW的ITEM的点击监听
        otherGridView.setOnItemClickListener(this);
        userGridView.setOnItemClickListener(this);
        //        userGridView.setOnItemLongClickListener(this);
    }
    //下载apk
    protected void downloadApk() {
        //微信v-url : "http://imtt.dd.qq.com/16891/722607E77ADA0E2BB6BE2FD1411F4A86.apk?fsname=com.tencent.mm_6.5.8_1060.apk&csr=97c2"

        //v-url : "http://imtt.dd.qq.com/16891/79E01F3A25B7C41E8863FD926CC4B9CA.apk?fsname=com.tencent.mobileqq_7.0.0_676.apk&csr=97c2"
        //apk下载地址，放置apk的路径
        //1.获取sd卡路径
        String path = Environment.getExternalStorageDirectory().getPath() + "/my.apk";
        //2.发送请求，获取apk，并放到指定路径
        RequestParams rp = new RequestParams("http://imtt.dd.qq.com/16891/722607E77ADA0E2BB6BE2FD1411F4A86.apk?fsname=com.tencent.mm_6.5.8_1060.apk&csr=97c2");
        rp.setSaveFilePath(path);
        rp.setAutoRename(true);
        x.http().get(rp, new Callback.ProgressCallback<File>() {
            //下载成功
            @Override
            public void onSuccess(File result) {
                Toast.makeText(MainActivity.this, "下载完成,开始安装!", Toast.LENGTH_SHORT).show();
                installApk(result);
            }

            //下载出现问题
            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.v("tag","失败");
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {
                Log.v("tag","结束");

            }

            @Override
            public void onWaiting() {

            }

            //刚刚开始下载
            @Override
            public void onStarted() {
                Log.v("tag","开始");
            }

            //下载过程中方法
            @Override
            public void onLoading(long total, long current, boolean isDownloading) {
            }
        });


    }


    //新版本APK下载完毕后，能够启动应用安装器安装apk的intent相关选项是？

    private void installApk(File file) {
        //系统应用界面,源码,安装apk入口
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        //文件作为数据源
        //设置安装的类型
        intent.setDataAndType(Uri.fromFile(file),"application/vnd.android.package-archive");
        startActivity(intent);

    }

    /** 初始化布局*/
    private void initView() {
        userGridView = (DragGrid) findViewById(R.id.userGridView);
        otherGridView = (OtherGridView) findViewById(R.id.otherGridView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    /** GRIDVIEW对应的ITEM点击监听接口  */
    @Override
    public void onItemClick(AdapterView<?> parent, final View view, final int position,long id) {
        //如果点击的时候，之前动画还没结束，那么就让点击事件无效
        if(isMove){
            return;
        }
        switch (parent.getId()) {
            case R.id.userGridView:
                //position为 0，1 的不可以进行任何操作
                if (position != 0 && position != 1) {
                    final ImageView moveImageView = getView(view);
                    if (moveImageView != null) {
                        TextView newTextView = (TextView) view.findViewById(R.id.text_item);
                        final int[] startLocation = new int[2];
                        newTextView.getLocationInWindow(startLocation);
                        final ChannelItem channel = ((DragAdapter) parent.getAdapter()).getItem(position);//获取点击的频道内容
                        otherAdapter.setVisible(false);
                        //添加到最后一个
                        otherAdapter.addItem(channel);
                        new Handler().postDelayed(new Runnable() {
                            public void run() {
                                try {
                                    int[] endLocation = new int[2];
                                    //获取终点的坐标
                                    otherGridView.getChildAt(otherGridView.getLastVisiblePosition()).getLocationInWindow(endLocation);
                                    MoveAnim(moveImageView, startLocation , endLocation, channel,userGridView);
                                    userAdapter.setRemove(position);
                                } catch (Exception localException) {
                                }
                            }
                        }, 50L);
                    }
                }
                break;
            case R.id.otherGridView:
                final ImageView moveImageView = getView(view);
                if (moveImageView != null){
                    TextView newTextView = (TextView) view.findViewById(R.id.text_item);
                    final int[] startLocation = new int[2];
                    newTextView.getLocationInWindow(startLocation);
                    final ChannelItem channel = ((OtherAdapter) parent.getAdapter()).getItem(position);
                    userAdapter.setVisible(false);
                    //添加到最后一个
                    userAdapter.addItem(channel);
                    new Handler().postDelayed(new Runnable() {
                        public void run() {
                            try {
                                int[] endLocation = new int[2];
                                //获取终点的坐标
                                userGridView.getChildAt(userGridView.getLastVisiblePosition()).getLocationInWindow(endLocation);
                                MoveAnim(moveImageView, startLocation , endLocation, channel,otherGridView);
                                otherAdapter.setRemove(position);
                            } catch (Exception localException) {
                            }
                        }
                    }, 50L);
                }
                break;
            default:
                break;
        }
    }
    /**
     * 点击ITEM移动动画
     * @param moveView
     * @param startLocation
     * @param endLocation
     * @param moveChannel
     * @param clickGridView
     */
    private void MoveAnim(View moveView, int[] startLocation,int[] endLocation, final ChannelItem moveChannel,
                          final GridView clickGridView) {
        int[] initLocation = new int[2];
        //获取传递过来的VIEW的坐标
        moveView.getLocationInWindow(initLocation);
        //得到要移动的VIEW,并放入对应的容器中
        final ViewGroup moveViewGroup = getMoveViewGroup();
        final View mMoveView = getMoveView(moveViewGroup, moveView, initLocation);
        //创建移动动画
        TranslateAnimation moveAnimation = new TranslateAnimation(
                startLocation[0], endLocation[0], startLocation[1],
                endLocation[1]);
        moveAnimation.setDuration(300L);//动画时间
        //动画配置
        AnimationSet moveAnimationSet = new AnimationSet(true);
        moveAnimationSet.setFillAfter(false);//动画效果执行完毕后，View对象不保留在终止的位置
        moveAnimationSet.addAnimation(moveAnimation);
        mMoveView.startAnimation(moveAnimationSet);
        moveAnimationSet.setAnimationListener(new AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
                isMove = true;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                moveViewGroup.removeView(mMoveView);
                // instanceof 方法判断2边实例是不是一样，判断点击的是DragGrid还是OtherGridView
                if (clickGridView instanceof DragGrid) {
                    otherAdapter.setVisible(true);
                    otherAdapter.notifyDataSetChanged();
                    userAdapter.remove();
                }else{
                    userAdapter.setVisible(true);
                    userAdapter.notifyDataSetChanged();
                    otherAdapter.remove();
                }
                isMove = false;
            }
        });
    }

    /**
     * 获取移动的VIEW，放入对应ViewGroup布局容器
     * @param viewGroup
     * @param view
     * @param initLocation
     * @return
     */
    private View getMoveView(ViewGroup viewGroup, View view, int[] initLocation) {
        int x = initLocation[0];
        int y = initLocation[1];
        viewGroup.addView(view);
        LinearLayout.LayoutParams mLayoutParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        mLayoutParams.leftMargin = x;
        mLayoutParams.topMargin = y;
        view.setLayoutParams(mLayoutParams);
        return view;
    }

    /**
     * 创建移动的ITEM对应的ViewGroup布局容器
     */
    private ViewGroup getMoveViewGroup() {
        ViewGroup moveViewGroup = (ViewGroup) getWindow().getDecorView();
        LinearLayout moveLinearLayout = new LinearLayout(this);
        moveLinearLayout.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        moveViewGroup.addView(moveLinearLayout);
        return moveLinearLayout;
    }

    /**
     * 获取点击的Item的对应View，
     * @param view
     * @return
     */
    private ImageView getView(View view) {
        view.destroyDrawingCache();
        view.setDrawingCacheEnabled(true);
        Bitmap cache = Bitmap.createBitmap(view.getDrawingCache());
        view.setDrawingCacheEnabled(false);
        ImageView iv = new ImageView(this);
        iv.setImageBitmap(cache);
        return iv;
    }

    /** 退出时候保存选择后数据库的设置  */
    private void saveChannel() {
        ChannelManage.getManage(IApplication.getApp().getSQLHelper()).deleteAllChannel();
        ChannelManage.getManage(IApplication.getApp().getSQLHelper()).saveUserChannel(userAdapter.getChannnelLst());
        ChannelManage.getManage(IApplication.getApp().getSQLHelper()).saveOtherChannel(otherAdapter.getChannnelLst());
    }

    @Override
    public void onBackPressed() {
        saveChannel();
        super.onBackPressed();
    }
}

