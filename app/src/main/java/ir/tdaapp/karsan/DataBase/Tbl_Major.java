package ir.tdaapp.karsan.DataBase;

import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

import ir.tdaapp.karsan.Adapter.DBAdapter;
import ir.tdaapp.karsan.ViewModel.VM_Major;

public class Tbl_Major {
    DBAdapter dbAdapter;

    public Tbl_Major(DBAdapter dbAdapter){
        this.dbAdapter=dbAdapter;
    }


    public List<VM_Major>GetAll(){
        List<VM_Major> majors=new ArrayList<>();

        try {
            Cursor query=dbAdapter.ExecuteQ("select * from Tbl_Major order by Title asc");

            for (int i=0;i<query.getCount();i++){
                VM_Major major=new VM_Major();
                major.setId(query.getInt(query.getColumnIndex("Id")));
                major.setTitle(query.getString(query.getColumnIndex("Title")));

                majors.add(major);
                query.moveToNext();
            }

            query.close();
            dbAdapter.close();
        }catch (Exception e){

        }

        return majors;
    }

    //در اینجا یک ای دی گرفته و پوزیشن آن را بازگشت می دهد
    public int GetPosition(int Id){
        List<VM_Major> all=GetAll();

        for (int i=0;i<all.size();i++){
            if (all.get(i).getId()==Id){
                return i;
            }
        }
        return -1;
    }

    //در اینجا یک ایدی گرفته و تایتل آن را برگشت می دهد
    public String GetTitleById(int id){
        List<VM_Major> majors=GetAll();

        for (int i=0;i<majors.size();i++){
            VM_Major major=majors.get(i);

            if (major.getId()==id){
                return major.getTitle();
            }
        }
        return "";
    }

    //در اینجا لیست آی دی گرفته می شود و لیستی از رشته های تحصیلی برگشت داده می شود
    public List<VM_Major> GetMajorsByIds(List<Integer> Ids){
        List<VM_Major> vals=new ArrayList<>();

        List<VM_Major>All=GetAll();

        for (int i=0;i<All.size();i++){
            for (int j=0;j<Ids.size();j++){
                if (All.get(i).getId()==Ids.get(j)){
                    vals.add(All.get(i));
                }
            }
        }

        return vals;
    }
}
