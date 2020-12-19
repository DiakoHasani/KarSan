package ir.tdaapp.karsanjob.Adapter;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import java.io.IOException;

import ir.tdaapp.karsanjob.DataBase.DataBaseHelper;

public class DBAdapter {

    protected static final String TAG = "DataAdapter";
    private final Context mContext;
    private SQLiteDatabase mDb;
    private DataBaseHelper mDbHelper;

    public DBAdapter(Context context) {
        this.mContext = context;
        mDbHelper = new DataBaseHelper(mContext);
    }

    public DBAdapter createDatabase() throws SQLException {
        try {
            mDbHelper.createDataBase();
        }
        catch (IOException mIOException) {
            Log.e(TAG, mIOException.toString() + "  UnableToCreateDatabase");
            throw new Error("UnableToCreateDatabase");
        }
        return this;
    }

    public DBAdapter open() throws SQLException {
        try {
            mDbHelper.openDataBase();
            mDbHelper.close();
            mDb = mDbHelper.getReadableDatabase();
        }
        catch (SQLException mSQLException) {
            Log.e(TAG, "open >>" + mSQLException.toString());
            throw mSQLException;
        }
        return this;
    }

    public void close() {
        mDbHelper.close();
    }

    public Cursor getData(String Query) {
        try {
            Cursor mCur = mDb.rawQuery(Query,null);
            if (mCur!=null) {
                mCur.moveToNext();
            }
            return mCur;
        }
        catch (SQLException mSQLException) {
            Log.e(TAG, "getTestData >>"+ mSQLException.toString());
            throw mSQLException;
        }
    }

    public Cursor ExecuteQ(String Q) {
        final DBAdapter mDbHelper = new DBAdapter(mContext);
        mDbHelper.createDatabase();
        mDbHelper.close();
        mDbHelper.open();
        final Cursor _Cursor = mDbHelper.getData(Q);
        return _Cursor;
    }

}