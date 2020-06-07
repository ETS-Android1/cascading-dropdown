package com.sample.tanay.dynamicspinner;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.SparseArray;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

class SpinnerThread extends HandlerThread {

    private static final String TAG = "SpinnerThread";

    private static SpinnerThread instance;

    synchronized static SpinnerThread getInstance(Context context) {
        if (instance == null) {
            instance = new SpinnerThread(context.getApplicationContext());
            instance.start();
        }
        return instance;
    }

    private DatabaseHelper mDatabaseHelper;
    private SharedPrefHelper mSharedPrefHelper;
    private SpinnerHandler mInternalHandler;
    private Handler mMainHandler;
    private AtomicInteger mAtomicInteger;
    private SparseArray<Listener> mListenerArray;


    private SpinnerThread(Context context) {
        super(TAG);
        this.mDatabaseHelper = DatabaseHelper.getInstance(context,
                SharedPrefHelper.helper(context).getTableList());
        this.mSharedPrefHelper = SharedPrefHelper.helper(context);
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

    void load(ArrayList<SpinnerElement> spinnerElements, Listener listener, boolean lazyLoading) {
        load(spinnerElements, listener, lazyLoading, null);
    }

    void load(ArrayList<SpinnerElement> spinnerElements, Listener listener, DataNode rootNode) {
        load(spinnerElements, listener, false, rootNode);
    }

    private void load(ArrayList<SpinnerElement> spinnerElements, Listener listener,
                      boolean lazyLoading, DataNode rootNode) {
        if (mSharedPrefHelper.isDbSaved()) {
            int id = mAtomicInteger.incrementAndGet();
            Message message = new Message();
            message.obj = new Request(spinnerElements, id, lazyLoading, rootNode);
            mListenerArray.put(id, listener);
            getInternalHandler().sendMessage(message);
        } else {
            listener.onDatabaseNotExist();
        }
    }


    private void process(ArrayList<SpinnerElement> spinnerElements, final int tag, boolean lazyLoading,
                         DataNode rootNode) {

        getMainHandler().post(new Runnable() {
            @Override
            public void run() {
                Listener listener = mListenerArray.get(tag);
                if (listener != null)
                    listener.onLoadStart();
            }
        });

        final DatabaseHelper.DatabaseListener listener = new DatabaseHelper.DatabaseListener() {
            @Override
            public void onLoadComplete(final DataNode rootNode) {
                getMainHandler().post(new Runnable() {
                    @Override
                    public void run() {
                        Listener listener = mListenerArray.get(tag);
                        if (listener != null)
                            listener.onLoadSuccess(rootNode);
                        mListenerArray.remove(tag);
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
                        mListenerArray.remove(tag);
                    }
                });
            }
        };

        if (rootNode == null)
            mDatabaseHelper.loadData(spinnerElements, listener, lazyLoading);
        else
            mDatabaseHelper.loadData(spinnerElements, listener, rootNode);
    }

    class SpinnerHandler extends Handler {

        SpinnerHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            Request request = (Request) msg.obj;
            process(request.spinnerElements, request.requestId, request.lazyLoading,
                    request.selectedDataNode);
        }
    }

    public interface Listener {
        void onLoadStart();

        void onLoadFailed(Exception exception);

        void onLoadSuccess(DataNode rootNode);

        void onDatabaseNotExist();
    }

    private static class Request {
        private ArrayList<SpinnerElement> spinnerElements;
        private int requestId;
        private boolean lazyLoading;
        private DataNode selectedDataNode;

        Request(ArrayList<SpinnerElement> spinnerElements, int requestId, boolean lazyLoading) {
            this(spinnerElements, requestId, lazyLoading, null);
        }

        Request(ArrayList<SpinnerElement> spinnerElements, int requestId, boolean lazyLoading, DataNode selectedDataNode) {
            this.spinnerElements = spinnerElements;
            this.requestId = requestId;
            this.lazyLoading = lazyLoading;
            this.selectedDataNode = selectedDataNode;
        }
    }
}
