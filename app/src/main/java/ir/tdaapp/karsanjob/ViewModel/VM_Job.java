package ir.tdaapp.karsanjob.ViewModel;

import androidx.annotation.NonNull;

//در اینجا شغل ها ست می شوند
public class VM_Job {

    private int Id;
    private String Title;

    public VM_Job() {
    }

    public VM_Job(int id, String title) {
        Id = id;
        Title = title;
    }

    public int getId() {
        return Id;
    }

    public String getTitle() {
        return Title;
    }

    public void setId(int id) {
        Id = id;
    }

    public void setTitle(String title) {
        Title = title;
    }

    @NonNull
    @Override
    public String toString() {
        return Title;
    }
}
