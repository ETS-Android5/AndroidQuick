package io.github.sdwfqin.widget.recyclerview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.ref.WeakReference;

/**
 * 描述：自动滚动的RecyclerView
 * <p>
 * adapter继承BaseAutoPollAdapter或者是参照BaseAutoPollAdapter
 *
 * @author zhangqin
 * @date 2018/3/14
 */
public class AutoPollRecyclerView extends RecyclerView {

    private long TIME_AUTO_POLL = 50;
    AutoPollTask autoPollTask;
    /**
     * 标示是否正在自动轮询
     */
    private boolean running;
    /**
     * 标示是否可以自动轮询,可在不需要的是否置false
     */
    private boolean canRun;

    public AutoPollRecyclerView(@NonNull Context context) {
        this(context, null);
    }

    public AutoPollRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AutoPollRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        autoPollTask = new AutoPollTask(this);
    }

    static class AutoPollTask implements Runnable {
        private final WeakReference<AutoPollRecyclerView> mReference;

        //使用弱引用持有外部类引用->防止内存泄漏
        public AutoPollTask(AutoPollRecyclerView reference) {
            this.mReference = new WeakReference<>(reference);
        }

        @Override
        public void run() {
            AutoPollRecyclerView recyclerView = mReference.get();
            if (recyclerView != null && recyclerView.running && recyclerView.canRun) {
                recyclerView.scrollBy(2, 2);
                recyclerView.postDelayed(recyclerView.autoPollTask, recyclerView.TIME_AUTO_POLL);
            }
        }
    }

    /**
     * 开启:如果正在运行,先停止->再开启
     */
    public void start() {
        if (running) {
            stop();
        }
        canRun = true;
        running = true;
        postDelayed(autoPollTask, TIME_AUTO_POLL);
    }

    public void stop() {
        running = false;
        removeCallbacks(autoPollTask);
    }


    /**
     * 设置滚动延时
     *
     * @param delayedTime
     */
    public void setDelayedTime(long delayedTime) {
        TIME_AUTO_POLL = delayedTime;
    }

    /**
     * 是否正在运行
     *
     * @return
     */
    public boolean isRuning() {
        return running;
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (running) {
                    stop();
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_OUTSIDE:
                if (canRun) {
                    start();
                }
                break;
            default:
        }
        return super.onTouchEvent(e);
    }
}