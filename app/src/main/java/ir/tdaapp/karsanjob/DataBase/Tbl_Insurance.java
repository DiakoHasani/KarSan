package ir.tdaapp.karsanjob.DataBase;

import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

import ir.tdaapp.karsanjob.Adapter.DBAdapter;
import ir.tdaapp.karsanjob.ViewModel.VM_Insurance;

public class Tbl_Insurance {
    DBAdapter db;

    public Tbl_Insurance(DBAdapter db) {
        this.db = db;
    }

    public List<VM_Insurance> GetAll() {

        List<VM_Insurance> vals = new ArrayList<>();

        try {

            Cursor query = db.ExecuteQ("select * from Tbl_Insurance");

            for (int i = 0; i < query.getCount(); i++) {
                VM_Insurance insurance=new VM_Insurance();

                insurance.setId(query.getInt(query.getColumnIndex("Id")));
                insurance.setTitle(query.getString(query.getColumnIndex("Title")));

                vals.add(insurance);
                query.moveToNext();
            }

            query.close();
            db.close();
            return vals;

        } catch (Exception e) {
            db.close();
            return vals;
        }
    }

    public int Position(int Id){
        List<VM_Insurance> vals=GetAll();

        for (int i=0;i<vals.size();i++){
            if (vals.get(i).getId()==Id){
                return i;
            }
        }
        return 0;
    }

    public String GetTitleById(int id) {
        List<VM_Insurance> insurances = GetAll();

        for (int i = 0; i < insurances.size(); i++) {
            VM_Insurance insurance = insurances.get(i);
            if (insurance.getId() == id) {
                return insurance.getTitle();
            }
        }
        return "";
    }
}
