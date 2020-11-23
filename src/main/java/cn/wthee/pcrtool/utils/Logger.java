package cn.wthee.pcrtool.utils;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.os.Looper;
import android.text.Html;
import android.text.Spanned;
import android.widget.FrameLayout;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class Logger extends FrameLayout implements Thread.UncaughtExceptionHandler {
    private static final boolean debuggable = false; //正式环境(false)不打印日志，也不能唤起app的debug界面
    @SuppressLint("StaticFieldLeak")
    private static Logger me;
    private static Thread.UncaughtExceptionHandler mDefaultHandler;
    private Context mCurrentActivity;


    private Logger(final Context context) {
        super(context);
    }

    /**
     * 在application 的 onCreate() 方法初始化
     */
    public static void init(Application application) {
        if (debuggable && me == null) {
            synchronized (Logger.class) {
                if (me == null) {
                    me = new Logger(application.getApplicationContext());
                    //获取系统默认异常处理器
                    mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
                    //线程空闲时设置异常处理，兼容其他框架异常处理能力
                    Looper.myQueue().addIdleHandler(() -> {
                        Thread.setDefaultUncaughtExceptionHandler(me);//线程异常处理设置为自己
                        return false;
                    });
                }
            }
        }
    }

    /**
     * 捕获崩溃信息
     */
    @Override
    public void uncaughtException(@NotNull Thread t, Throwable e) {
        // 打印异常信息
        e.printStackTrace();
        // 我们没有处理异常 并且默认异常处理不为空 则交给系统处理
        if (!handleException(t, e) && mDefaultHandler != null) {
            // 系统处理
            mDefaultHandler.uncaughtException(t, e);
        }
    }

    /*自己处理崩溃事件*/
    private boolean handleException(final Thread t, final Throwable e) {
        if (e == null) {
            return false;
        }
        new Thread() {
            @Override
            public void run() {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                PrintStream printStream = new PrintStream(baos);
                e.printStackTrace(printStream);
                String s = baos.toString();
                String[] split = s.split("\t");
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < split.length; i++) {
                    String s1 = split[i];
                    if ((!s1.contains("android.") && !s1.contains("java."))
                            && s1.contains("at") && i > 0) {
                        s1 = String.format("<br> <font color='#ff0000'>%s</font>", s1);
                    }
                    sb.append(s1).append("\t ");
                }
                mCurrentActivity = ActivityUtil.Companion.getInstance().getCurrentActivity();
                Spanned spanned = Html.fromHtml(sb.toString());
                Looper.prepare();
                AlertDialog.Builder builder = new AlertDialog.Builder(mCurrentActivity);
                builder.setTitle("错误日志：");
                builder.setMessage(spanned);
                //TODO 复制文本
                builder.setPositiveButton("关闭应用", (dialog, which) -> mDefaultHandler.uncaughtException(t, e));
                builder.setCancelable(false);
                builder.show();
                Looper.loop();
            }
        }.start();
        return true;
    }

}
