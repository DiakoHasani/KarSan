package ir.tdaapp.karsanjob.DataBase;

import android.database.Cursor;

import ir.tdaapp.karsanjob.Adapter.DBAdapter;

public class DataBase_UTC {

    public static int GetId(String TblName, DBAdapter db){

        Cursor GetId = db.ExecuteQ("select MAX(Id) from "+TblName);
        int Id;
        if (GetId.getString(0) != null)
            Id = Integer.parseInt(GetId.getString(0)) + 1;
        else
            Id = 1;

        return Id;
    }
}
