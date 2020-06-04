package com.sample.tanay.dynamicspinner;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.JsonReader;
import android.util.Log;
import android.util.SparseArray;
import android.util.SparseIntArray;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

class DataProcessor extends HandlerThread {

    private static final String TAG = "DataProcessor";

    private static final String SETUP_COMPLETE = "org.samagra.SETUP_COMPLETE";
    private static final String SETUP_START = "org.samagra.SETUP_START";
    private static final String SETUP_FAIL = "org.samagra.SETUP_FAIL";

    private static DataProcessor instance;

    private Handler mHandler;
    private ProcessorHandler mInternalHandler;

    private WeakReference<Context> mApplicationContext;
    private ArrayList<String> names;
    private SparseArray<ArrayList<DataNode>> levelList;

    private DatabaseHelper mDatabaseHelper;
    private SharedPrefHelper mSharedPrefHelper;
    private WeakReference<DynamicSpinnerView.SetupListener> mSetupListenerWeakReference;
    private LocalBroadcastManager mLocalBroadcastManager;

    synchronized static DataProcessor newInstance(Context context) {
        if (instance == null) {
            instance = new DataProcessor(context);
            instance.start();
        }
        return instance;
    }

    private DataProcessor(Context context) {
        super(TAG);
        mSharedPrefHelper = SharedPrefHelper.helper(context);
        mApplicationContext = new WeakReference<>(context.getApplicationContext());
        mLocalBroadcastManager = LocalBroadcastManager.getInstance(context.getApplicationContext());
        levelList = new SparseArray<>();
        names = new ArrayList<>();
    }

    private Handler getMainHandler() {
        if (mHandler == null) {
            mHandler = new Handler(Looper.getMainLooper());
        }
        return mHandler;
    }

    private Handler getInternalHandler() {
        if (mInternalHandler == null) {
            mInternalHandler = new ProcessorHandler(getLooper());
        }
        return mInternalHandler;
    }

    void setup(String filename, DynamicSpinnerView.SetupListener setupListener) {
        mSetupListenerWeakReference = new WeakReference<>(setupListener);

        Message message = new Message();
        message.obj = filename;
        message.what = 1;

        mLocalBroadcastManager.sendBroadcast(new Intent(SETUP_START));

        setupListener.onSetupStart();

        if (mSharedPrefHelper.isDbSaved()) {
            setupListener.onSetupComplete();
            quitSafely();
        } else {
            getInternalHandler().sendMessage(message);
        }
    }

    private DatabaseHelper getDatabaseHelper() {
        if (mDatabaseHelper == null) {
            Context appContext = mApplicationContext.get();
            if (appContext != null) {
                mDatabaseHelper = DatabaseHelper.getInstance(appContext, names);
            } else {
                quit();
            }
        }
        return mDatabaseHelper;
    }

    private void process(String fileName) {
        if (mApplicationContext.get() != null) {
            Context activity = mApplicationContext.get();
            try {

                Log.d("time", "step 1 starting read " + System.currentTimeMillis());

                DataNode dataNode = getDataInTreeFormat(activity, fileName);

                Log.d("time", "step 2 read complete " + System.currentTimeMillis());

                dataNode.assignId(0, new SparseIntArray());
                dataNode.assignParentId(false);


                for (int index = 0; index < names.size(); index++) {
                    levelList.put(index, new ArrayList<DataNode>());
                }

                Log.d("time", "step 3 starting save database " + System.currentTimeMillis());

                saveInfo(dataNode, -1);

                for (int index = 0; index < names.size(); index++) {
                    if (levelList.get(index).size() > 0) {
                        getDatabaseHelper().saveDataNodes(names.get(index), levelList.get(index));
                        levelList.get(index).clear();
                    }
                }

                Log.d("time", "step 4 database saved " + System.currentTimeMillis());

                getDatabaseHelper().buildIndex();

                Log.d("time", "index built " + System.currentTimeMillis());

                mSharedPrefHelper.saveTableList(new TableList(names));
                mSharedPrefHelper.setDbSaved();

                Log.d("time", "step 6 info saved in shared pref saved");

                if (mSetupListenerWeakReference.get() != null) {
                    getMainHandler().post(new Runnable() {
                        @Override
                        public void run() {
                            mSetupListenerWeakReference.get().onSetupComplete();
                        }
                    });
                }

                mLocalBroadcastManager.sendBroadcast(new Intent(SETUP_COMPLETE));
            } catch (final Exception ex) {
                mLocalBroadcastManager
                        .sendBroadcast(new Intent(SETUP_FAIL));
            } finally {
                quitSafely();
            }
        } else {
            quitSafely();
        }
    }


    private void saveInfo(DataNode dataNode, int level) {
        if (dataNode.children != null) {
            level = level + 1;
            if (levelList.get(level) != null) {
                if ((levelList.get(level).size() + dataNode.children.size()) > 2000) {
                    getDatabaseHelper().saveDataNodes(names.get(level), levelList.get(level));
                    levelList.get(level).clear();
                }
                levelList.get(level).addAll(dataNode.children);
            }
            for (DataNode child : dataNode.children) {
                saveInfo(child, level);
            }
        }
    }


    private DataNode getDataInTreeFormat(Context context, String filename) {
        DataNode rootNode = new DataNode("root");
        DataNode copyDataNode = rootNode;

        InputStreamReader inputStreamReader = null;


        try {
            inputStreamReader = new InputStreamReader(context.getApplicationContext().getAssets().open(filename));
            JsonReader jsonReader = new JsonReader(inputStreamReader);
            jsonReader.beginArray();
            boolean isFirstIteration = true;
            while (jsonReader.hasNext()) {
                jsonReader.beginObject();
                while (jsonReader.hasNext()) {
                    String type = jsonReader.nextName();
                    if (isFirstIteration) {
                        names.add(type);
                    }
                    String name = jsonReader.nextString();
                    DataNode childRootNode = rootNode.getChild(name);
                    if (childRootNode == null) {
                        if (rootNode.children == null) {
                            rootNode.children = new ArrayList<>();
                        }
                        childRootNode = new DataNode(name);
                        rootNode.children.add(childRootNode);
                    }
                    rootNode = childRootNode;
                }
                jsonReader.endObject();
                rootNode = copyDataNode;
                isFirstIteration = false;
            }
            jsonReader.endArray();
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (inputStreamReader != null) {
                try {
                    inputStreamReader.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
        return rootNode;
    }


    private class ProcessorHandler extends Handler {

        ProcessorHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            process((String) msg.obj);
        }
    }
}
