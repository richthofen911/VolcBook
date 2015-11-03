package net.callofdroidy.volcbook;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;

import java.io.File;

/**
 * Created by admin on 03/11/15.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = Environment.getExternalStorageDirectory() + File.separator + "/VolcBook/" + File.separator + "ielts.db";
    public static final int DATABASE_VERSION = 1;

    DatabaseHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db){
        Log.e("DBHelper", "onCreate");
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // do something like delete entries and call onCreate() again
        //
        onCreate(db);
    }
}
