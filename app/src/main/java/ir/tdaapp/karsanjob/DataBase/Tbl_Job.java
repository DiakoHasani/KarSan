package ir.tdaapp.karsanjob.DataBase;

import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

import ir.tdaapp.karsanjob.Adapter.DBAdapter;
import ir.tdaapp.karsanjob.ViewModel.VM_Job;

public class Tbl_Job {
    DBAdapter dbAdapter;


    public Tbl_Job(DBAdapter dbAdapter) {
        this.dbAdapter = dbAdapter;
    }

    //در اینجا تمامی شغل ها برگشت داده می شود
    public List<VM_Job> GetAll() {
        List<VM_Job> jobs = new ArrayList<>();

        try {
            Cursor cur = dbAdapter.ExecuteQ("select * from Tbl_Job order by Title asc");
            //در اینجا شغل ها را در دیتابیس خوانده می شود
            for (int i = 0; i < cur.getCount(); i++) {
                VM_Job job = new VM_Job();
                job.setId(cur.getInt(cur.getColumnIndex("Id")));
                job.setTitle(cur.getString(cur.getColumnIndex("Title")));

                jobs.add(job);
                cur.moveToNext();
            }
            cur.close();
            dbAdapter.close();
        } catch (Exception e) {

        }

        return jobs;
    }

    //در اینجا تایتل تمامی شغل ها برگشت داده می شود
    public String[] GetAll_Title() {
        Cursor cur = dbAdapter.ExecuteQ("select * from Tbl_Job");

        String[] jobs = new String[cur.getCount()];

        //در اینجا شغل ها را در دیتابیس خوانده می شود
        for (int i = 0; i < cur.getCount(); i++) {

            jobs[i] = cur.getString(cur.getColumnIndex("Title"));
            cur.moveToNext();
        }

        return jobs;
    }

    public String GetTitleById(int Id) {
        List<VM_Job> jobs = GetAll();

        for (int i = 0; i < jobs.size(); i++) {
            VM_Job job = jobs.get(i);

            if (job.getId() == Id) {
                dbAdapter.close();
                return job.getTitle();
            }
        }
        dbAdapter.close();
        return "";
    }

    public int GetPositionById(int id){
        List<VM_Job>All=GetAll();

        for (int i=0;i<All.size();i++){
            if (All.get(i).getId()==id){
                return i;
            }
        }
        return 0;
    }
}
