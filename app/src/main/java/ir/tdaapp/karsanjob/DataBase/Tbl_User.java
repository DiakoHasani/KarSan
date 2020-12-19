package ir.tdaapp.karsanjob.DataBase;

import android.database.Cursor;

import ir.tdaapp.karsanjob.Adapter.DBAdapter;

public class Tbl_User {
    DBAdapter dbAdapter;

    public Tbl_User(DBAdapter dbAdapter) {
        this.dbAdapter = dbAdapter;
    }

    public void Add(String UniCode) {
        try {

            UniCode=UniCode.replace("\"","");
            dbAdapter.ExecuteQ("insert into Tbl_User values('" + UniCode + "')");
            dbAdapter.close();

        } catch (Exception e) {

        }
    }

    public void RemoveAll() {
        try {
            dbAdapter.ExecuteQ("delete from Tbl_User");
            dbAdapter.close();
        } catch (Exception e) {

        }
    }

    public String GetUniCode() {
        try {
            Cursor cursor = dbAdapter.ExecuteQ("select UserCode from Tbl_User Limit 1");

            String Code = cursor.getString(0);

            dbAdapter.close();
            cursor.close();

            return Code.replace("\"","");
        } catch (Exception e) {
            return "";
        }
    }

    //در اینجا چک می شود که کاربر حساب کاربری دارد
    public boolean HasAccount() {
        try {

            Cursor cursor = dbAdapter.ExecuteQ("select * from Tbl_User");

            int count = cursor.getCount();

            return count > 0;

        } catch (Exception e) {
            return false;
        }
    }
}
