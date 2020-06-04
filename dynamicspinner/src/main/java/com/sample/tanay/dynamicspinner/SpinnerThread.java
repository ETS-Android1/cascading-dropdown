package com.sample.tanay.dynamicspinner;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.SparseArray;

import androidx.annotation.NonNull;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

class SpinnerThread extends HandlerThread {

    private static final String TAG = "SpinnerThread";

    private static SpinnerThread instance;

    public synchronized static SpinnerThread getInstance(Context context) {
        if (instance == null) {
            instance = new SpinnerThread(context.getApplicationContext());
            instance.start();
        }
        return instance;
    }

    private DatabaseHelper mDatabaseHelper;
    private SpinnerHandler mInternalHandler;
    private Handler mMainHandler;
    private AtomicInteger mAtomicInteger;
    private SparseArray<Listener> mListenerArray;


    private SpinnerThread(Context context) {
        super(TAG);
        this.mDatabaseHelper = DatabaseHelper.getInstance(context,
                SharedPrefHelper.helper(context).getTableList());
        this.mAtomicInteger = new AtomicInteger(0);
        mListenerArray = new SparseArray<>();
    }

    private SpinnerHandler getInternalHandler() {
        if (mInternalHandler == null) {
            mInternalHandler = new SpinnerHandler(getLooper());
        }
        return mInternalHandler;
    }

    private Handler getMainHandler() {
        if (mMainHandler == null) {
            mMainHandler = new Handler(Looper.getMainLooper());
        }
        return mMainHandler;
    }

    void load(ArrayList<SpinnerElement> spinnerElements, Listener listener) {
        int id = mAtomicInteger.incrementAndGet();
        Message message = new Message();
        message.arg1 = id;
        message.obj = spinnerElements;
        mListenerArray.put(id, listener);
        getInternalHandler().sendMessage(message);

    }

    private void process(ArrayList<SpinnerElement> spinnerElements, final int tag) {
        getMainHandler().post(new Runnable() {
            @Override
            public void run() {
                Listener listener = mListenerArray.get(tag);
                if (listener != null)
                    listener.onLoadStart();
            }
        });
        mDatabaseHelper.loadData(spinnerElements, new DatabaseHelper.DatabaseListener() {
            @Override
            public void onLoadComplete(final DataNode rootNode) {
                getMainHandler().post(new Runnable() {
                    @Override
                    public void run() {
                        Listener listener = mListenerArray.get(tag);
                        if (listener != null)
                            listener.onLoadSuccess(rootNode);
                    }
                });
            }

            @Override
            public void onLoadError(final Exception ex) {
                getMainHandler().post(new Runnable() {
                    @Override
                    public void run() {
                        Listener listener = mListenerArray.get(tag);
                        if (listener != null)
                            listener.onLoadFailed(ex);
                    }
                });
            }
        });
    }

    class SpinnerHandler extends Handler {

        SpinnerHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            process((ArrayList<SpinnerElement>) msg.obj, msg.arg1);
        }
    }

    public interface Listener {
        void onLoadStart();

        void onLoadFailed(Exception exception);

        void onLoadSuccess(DataNode rootNode);
    }

}
