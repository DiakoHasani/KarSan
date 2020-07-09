package ir.tdaapp.karsan.DataBase;

import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

import ir.tdaapp.karsan.Adapter.DBAdapter;
import ir.tdaapp.karsan.ViewModel.VM_Madrak;

public class Tbl_Madrak {
    private DBAdapter dbAdapter;

    public Tbl_Madrak(DBAdapter dbAdapter) {
        this.dbAdapter = dbAdapter;
    }

    public List<VM_Madrak> GetAll() {
        List<VM_Madrak> madraks = new ArrayList<>();

        try {
            Cursor cursor = dbAdapter.ExecuteQ("select * from Tbl_Madrak");

            for (int i = 0; i < cursor.getCount(); i++) {
                VM_Madrak madrak = new VM_Madrak();

                madrak.setId(cursor.getInt(cursor.getColumnIndex("Id")));
                madrak.setTitle(cursor.getString(cursor.getColumnIndex("Title")));
                madraks.add(madrak);
                cursor.moveToNext();
            }

            cursor.close();
            dbAdapter.close();
        } catch (Exception e) {

        }

        return madraks;
    }

    //در اینجا یک ای دی گرفته و پوزیشن آن را بازگشت می دهد
    public int GetPosition(int Id) {

        List<VM_Madrak> all = GetAll();

        for (int i = 0; i < all.size(); i++) {
            if (all.get(i).getId() == Id) {
                return i;
            }
        }
        return -1;
    }

    //در اینجا تایتل مدرک را براساس ای دی برگشت می دهد
    public String GetTitleById(int id) {
        List<VM_Madrak> madraks = GetAll();

        for (int i = 0; i < madraks.size(); i++) {
            VM_Madrak madrak = madraks.get(i);
            if (madrak.getId() == id) {
                return madrak.getTitle();
            }
        }
        return "";
    }

    //در اینجا یک لیست ای دی را گرفته و لیستی از ویومدل مدرک برگشت می دهد
    public List<VM_Madrak> GetMadraksById(List<Integer>Ids){
        List<VM_Madrak>vals=new ArrayList<>();

        List<VM_Madrak>AllMadraks=GetAll();

        for (int i=0;i<AllMadraks.size();i++){
            for (int j=0;j<Ids.size();j++){
                if (AllMadraks.get(i).getId()==Ids.get(j)){
                    vals.add(AllMadraks.get(i));
                }
            }
        }

        return vals;
    }
}
