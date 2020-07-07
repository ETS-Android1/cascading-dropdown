package com.sample.tanay.dynamicspinner;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;

class DatabaseHelper extends SQLiteOpenHelper {

    static final String DB_NAME = "DynamicSpinnerDb";
    private static final int MAX_LIMIT = 150000;
    private ArrayList<String> tableNames, oldTableNames;
    private SQLiteDatabase db;

    private static final String ID = "Id";
    private static final String NAME = "Name";
    private static final String PARENT_ID = "ParentId";

    private static DatabaseHelper instance;

    static synchronized DatabaseHelper getInstance(Context context, ArrayList<String> tableNames) {
        if (instance == null) {
            instance = new DatabaseHelper(context.getApplicationContext(), tableNames);
        }
        return instance;
    }

    static synchronized DatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = getInstance(context.getApplicationContext(), SharedPrefHelper.helper(context).getTableList(),
                    new ArrayList<String>(), SharedPrefHelper.helper(context).getDatabaseVersion());
        }
        return instance;
    }

    static synchronized DatabaseHelper getInstance(Context context, ArrayList<String> tableNames,
                                                   ArrayList<String> oldTableNames, int version) {
        if (instance == null) {
            instance = new DatabaseHelper(context, tableNames, version, oldTableNames);
        }
        return instance;
    }

    private DatabaseHelper(@Nullable Context context, ArrayList<String> tableNames) {

        super(context, DB_NAME, null, 1);
        this.tableNames = tableNames;
        db = getWritableDatabase();
    }

    private DatabaseHelper(Context context, ArrayList<String> tableNames, int version, ArrayList<String>
            oldTableNames) {
        super(context, DB_NAME, null, version);
        this.tableNames = tableNames;
        this.oldTableNames = oldTableNames;
        db = getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createTables(tableNames, db);
    }

    private void createTables(ArrayList<String> tableNames, SQLiteDatabase db) {
        int index = 0;
        for (String tableName : tableNames) {
            String sql = getTableSQL(tableName, index == 0 ? null : tableNames.get(index - 1));
            db.execSQL(sql);
            index++;
        }
    }

    void buildIndex() {
        for (String table : tableNames) {
            db.execSQL(getIndexSQL(table, NAME));
        }
    }

    private String getTableSQL(String tableName, String foreignKeyName) {
        boolean hasForeignKey = foreignKeyName != null && foreignKeyName.length() > 0;
        return "CREATE TABLE \"" + tableName + "\"( \"" + ID + "\" INTEGER NOT NULL, \"" + NAME +
                "\" TEXT "
                + (hasForeignKey ? ", \"" + PARENT_ID + "\" INTEGER " : "")
                + ");";
    }

    private String getIndexSQL(String tableName, String columnName) {
        return "CREATE INDEX " + tableName.toLowerCase() + "_" + columnName + " ON " +
                tableName + "(" + columnName + ")";
    }

    void loadData(ArrayList<SpinnerElement> spinnerElements, DatabaseListener listener, boolean lazyLoading) {
        if (lazyLoading) {
            loadTableData(spinnerElements.get(0), listener);
        } else {
            loadDataAtOnce(spinnerElements, listener);
        }
    }

    void loadData(ArrayList<SpinnerElement> spinnerElements, DatabaseListener listener, DataNode dataNode) {
        String sql = buildSelectClause(spinnerElements) + buildJoinClause(spinnerElements)
                + buildWhereClauseIfRequired(spinnerElements)
                + (SpinnerElement.hasValues(spinnerElements) ? " AND " : " WHERE ")
                + spinnerElements.get(0).type + "." + PARENT_ID + " = " + dataNode.id + ";";
        executeSQLAndLoadData(sql, spinnerElements, listener, dataNode);
    }

    private void loadTableData(SpinnerElement spinnerElement, DatabaseListener listener) {
        try {
            StringBuilder sqlBuilder = new StringBuilder("SELECT * FROM " + spinnerElement.type);

            if (spinnerElement.hasValues()) {
                sqlBuilder.append(" WHERE");
                int index = 0;
                for (String value : spinnerElement.values) {
                    String sql2 = " " + NAME + " LIKE " + '"' + value + '"' +
                            (index < spinnerElement.values.size() - 1 ? " OR " : "");
                    index++;
                    sqlBuilder.append(sql2);
                }
            }

            Cursor cursor = db.rawQuery(sqlBuilder.toString(), null);
            DataNode rootNode = new DataNode("root");
            rootNode.children = new ArrayList<>();

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    int id = cursor.getInt(cursor.getColumnIndex(ID));
                    String name = cursor.getString(cursor.getColumnIndex(NAME));
                    rootNode.children.add(new DataNode(name, id));
                } while (cursor.moveToNext());
                cursor.close();
            }

            listener.onLoadComplete(rootNode);
        } catch (Exception ex) {
            listener.onLoadError(ex);
        }
    }

    private void executeSQLAndLoadData(String sql, ArrayList<SpinnerElement> spinnerElements,
                                       DatabaseListener listener, DataNode rootNode) {
        DataNode copy = rootNode;

        int offset = 0;
        try {

            while ((true)) {

                String finalSQL = sql + "LIMIT " + MAX_LIMIT + " OFFSET " + (offset * MAX_LIMIT) + ";";
                Cursor cursor = db.rawQuery(finalSQL, null);
                offset++;
                int size = 0;
                if (cursor != null && cursor.moveToFirst()) {
                    size = cursor.getCount();
                    do {
                        for (SpinnerElement spinnerElement : spinnerElements) {
                            int id = cursor.getInt(cursor
                                    .getColumnIndex(spinnerElement.type + "_" + ID));

                            String name = cursor.getString(cursor
                                    .getColumnIndex(spinnerElement.type + "_" + NAME));

                            DataNode dataNode = rootNode.getChild(id);

                            if (dataNode == null) {
                                dataNode = new DataNode(name, id);
                                if (rootNode.children == null) {
                                    rootNode.children = new ArrayList<>();
                                }
                                rootNode.children.add(dataNode);
                            }
                            rootNode = dataNode;
                        }
                        rootNode = copy;
                    } while (cursor.moveToNext());

                    cursor.close();

                    if (size < MAX_LIMIT) {
                        break;
                    }
                } else {
                    break;
                }
            }
            listener.onLoadComplete(rootNode);
        } catch (Exception ex) {
            listener.onLoadError(ex);
        }
    }

    private void loadDataAtOnce(ArrayList<SpinnerElement> spinnerElements, DatabaseListener listener) {
        String sql = buildSelectClause(spinnerElements) + " " + buildJoinClause(spinnerElements)
                + " " + buildWhereClauseIfRequired(spinnerElements);

        executeSQLAndLoadData(sql, spinnerElements, listener, new DataNode("root"));
    }

    private String buildSelectClause(ArrayList<SpinnerElement> spinnerElements) {
        StringBuilder stringBuilder = new StringBuilder("SELECT ");
        for (SpinnerElement spinnerElement : spinnerElements) {
            stringBuilder.append(spinnerElement.type).append('.').append(NAME)
                    .append(" as ").append(spinnerElement.type).append('_')
                    .append(NAME).append(',')
                    .append(spinnerElement.type).append('.')
                    .append(ID).append(" as ").append(spinnerElement.type).append('_')
                    .append(ID).append(',')
            ;
        }
        return stringBuilder.substring(0, stringBuilder.length() - 1);
    }

    private String buildWhereClauseIfRequired(ArrayList<SpinnerElement> spinnerElements) {
        if (SpinnerElement.hasValues(spinnerElements)) {
            return buildWhereClause(spinnerElements);
        } else {
            return " ";
        }
    }

    private String buildWhereClause(ArrayList<SpinnerElement> spinnerElements) {
        StringBuilder stringBuilder = new StringBuilder(" WHERE ");
        boolean notFirstIteration = false;
        for (SpinnerElement spinnerElement : spinnerElements) {
            if (spinnerElement.hasValues()) {
                if (notFirstIteration) {
                    stringBuilder.append(" AND ");
                }
                stringBuilder.append("(");
                boolean notFirstInnerIteration = false;
                for (String type : spinnerElement.values) {
                    if (notFirstInnerIteration) {
                        stringBuilder.append(" OR ");
                    }
                    stringBuilder.append(spinnerElement.type).append('.').append(NAME)
                            .append(" like ").append('"').append(type).append('"');
                    notFirstInnerIteration = true;
                }
                stringBuilder.append(")");
                notFirstIteration = true;
            }
        }
        return stringBuilder.toString();
    }

    private String buildJoinClause(ArrayList<SpinnerElement> spinnerElements) {
        StringBuilder stringBuilder = new StringBuilder(" from ");
        String first = spinnerElements.get(0).type;
        String last = (spinnerElements.size() > 1)
                ? spinnerElements.get(spinnerElements.size() - 1).type : null;
        stringBuilder.append(first);
        int index = 0, firstElement = 0, lastElement = 0;
        for (String tableName : tableNames) {
            if (first.equalsIgnoreCase(tableName)) {
                firstElement = index;
                if (last == null)
                    break;
            }
            if (tableName.equalsIgnoreCase(last)) {
                lastElement = index;
                break;
            }
            index++;
        }
        for (; firstElement < lastElement; firstElement++) {
            first = tableNames.get(firstElement);
            String second = tableNames.get(firstElement + 1);
            stringBuilder.append(" inner join ").append(second)
                    .append(" on ").append(first).append('.')
                    .append(ID).append(" = ").append(second)
                    .append('.').append(PARENT_ID);
        }
        return stringBuilder.toString();
    }

    void saveDataNodes(String tableName, ArrayList<DataNode> dataNodes) {
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            for (DataNode dataNode : dataNodes) {
                values.put(ID, dataNode.id);
                values.put(NAME, dataNode.name);
                if (dataNode.parentId != null)
                    values.put(PARENT_ID, dataNode.parentId);
                db.insert(tableName, null, values);
            }
            db.setTransactionSuccessful();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            db.endTransaction();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.beginTransaction();
        try {
            for (String oldTableName : oldTableNames) {
                String sql = "DROP Table \"" + oldTableName + "\";";
                db.execSQL(sql);
            }
            db.setTransactionSuccessful();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            db.endTransaction();
        }
        createTables(tableNames, db);
    }

    interface DatabaseListener {
        void onLoadComplete(DataNode rootNode);

        void onLoadError(Exception ex);
    }
}
