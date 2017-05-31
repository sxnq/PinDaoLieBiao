package xunqaing.bwie.com.pindaoliebiao;

import android.app.Application;
import android.graphics.Bitmap;
import android.os.Environment;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import org.xutils.DbManager;
import org.xutils.x;

import xunqaing.bwie.com.pindaoliebiao.newsdrag.db.SQLHelper;


public class IApplication extends Application {

    public DbManager.DaoConfig configTj;
    private static IApplication mAppApplication;
    private SQLHelper sqlHelper;
    @Override
    public void onCreate() {
        super.onCreate();
        x.Ext.init(this);

        initImageLoader();
        mAppApplication = this;
        initData();
//        Config.DEBUG = true;

    }


    private void initImageLoader() {


        String path = Environment.getExternalStorageDirectory() + "/imageload";


        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
                .memoryCacheExtraOptions(480, 800)//缓存图片最大的长和宽
                .threadPoolSize(2)//线程池的数量
                .threadPriority(4)
                .memoryCacheSize(2 * 1024 * 1024)//设置内存缓存区大小
                .diskCacheSize(20 * 1024 * 1024)//设置sd卡缓存区大小
                //                .diskCache(new UnlimitedDiskCache(new File(path)))//自定义缓存目录
                .writeDebugLogs()//打印日志内容
                .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                .build();
        ImageLoader.getInstance().init(config);
    }

    /**
     * 圆角图片
     */
    public static DisplayImageOptions MyDisplayImageOptions() {

        DisplayImageOptions options = new DisplayImageOptions.Builder()

                .showImageOnLoading(R.mipmap.ic_launcher) //设置图片在下载期间显示的图片
                .showImageForEmptyUri(R.mipmap.ic_launcher)//设置图片Uri为空或是错误的时候显示的图片
                .showImageOnFail(R.mipmap.ic_launcher)  //设置图片加载/解码过程中错误时候显示的图片
                .cacheInMemory(true)//设置下载的图片是否缓存在内存中
                .cacheOnDisk(true)
                .considerExifParams(true)  //是否考虑JPEG图像EXIF参数（旋转，翻转）
                .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)//设置图片以如何的编码方式显示
                .bitmapConfig(Bitmap.Config.RGB_565)//设置图片的解码类型//
                .displayer(new RoundedBitmapDisplayer(360))//是否设置为圆角，弧度为多少
                //      .displayer(new FadeInBitmapDisplayer(100))//是否图片加载好后渐入的动画时间
                .build();//构建完成

        return options;
    }

    public void initData() {
        configTj = new DbManager.DaoConfig();
        configTj.setDbName("database.db");
        configTj.setDbVersion(1);
        configTj.setDbUpgradeListener(new DbManager.DbUpgradeListener() {
            @Override
            public void onUpgrade(DbManager db, int oldVersion, int newVersion) {

            }
        });
    }
    /** 获取Application */
    public static IApplication getApp() {
        return mAppApplication;
    }
    /** 获取数据库Helper */
    public SQLHelper getSQLHelper() {
        if (sqlHelper == null)
            sqlHelper = new SQLHelper(mAppApplication);
        return sqlHelper;
    }
    /** 摧毁应用进程时候调用 */
    public void onTerminate() {
        if (sqlHelper != null)
            sqlHelper.close();
        super.onTerminate();
    }
}