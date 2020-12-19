package ir.tdaapp.karsanjob.DataBase;

import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

import ir.tdaapp.karsanjob.Adapter.DBAdapter;

public class Tbl_FavoritesItem {
    DBAdapter db;

    public Tbl_FavoritesItem(DBAdapter db) {
        this.db = db;
    }

    public void Add(int ItemId) {
        int Id = DataBase_UTC.GetId("Tbl_FavoritesItem", db);
        db.ExecuteQ("insert into Tbl_FavoritesItem (Id,ItemId) values (" + Id + "," + ItemId + ")");
        db.close();
    }

    public void Delete(int ItemId) {
        db.ExecuteQ("delete from Tbl_FavoritesItem where ItemId=" + ItemId);
        db.close();
    }

    public boolean IsLike(int ItemId) {
        Cursor q = db.ExecuteQ("select * from Tbl_FavoritesItem where ItemId=" + ItemId);
        if (q.getCount() > 0)
            return true;
        return false;
    }

    public List<Integer> GetAllItemId() {
        List<Integer> vals = new ArrayList<>();

        Cursor q = db.ExecuteQ("select * from Tbl_FavoritesItem");

        for (int i = 0; i < q.getCount(); i++) {
            vals.add(q.getInt(q.getColumnIndex("ItemId")));
            q.moveToNext();
        }
        q.close();
        db.close();
        return vals;
    }
}
