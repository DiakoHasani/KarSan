package ir.tdaapp.karsan.DataBase;

import android.database.Cursor;

import ir.tdaapp.karsan.Adapter.DBAdapter;
import ir.tdaapp.karsan.ViewModel.VM_Versions;

public class Tbl_Setting {
    DBAdapter dbAdapter;

    public Tbl_Setting(DBAdapter dbAdapter) {
        this.dbAdapter = dbAdapter;
    }

    //در اینجا ورژن های برنامه و دیتابیس برگشت داده می شوند
    public VM_Versions GetVersions() {

        VM_Versions versions = new VM_Versions();

        try {
            Cursor query = dbAdapter.ExecuteQ("select * from Tbl_Setting LIMIT 1");

            versions.setVersionApp(query.getFloat(query.getColumnIndex("VersionApp")));
            versions.setVersionSql(query.getFloat(query.getColumnIndex("VersionSql")));

        } catch (Exception e) {
            versions.setVersionSql(1.0f);
            versions.setVersionApp(1.0f);
        }

        return versions;
    }

    public void UpdateSqlVersion(float Version) {
        dbAdapter.ExecuteQ("update Tbl_Setting set VersionSql=" + Version);
    }
}
