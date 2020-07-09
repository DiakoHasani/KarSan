package ir.tdaapp.karsan.ViewModel;

import androidx.annotation.NonNull;

//برای رشته های تحصیلی
public class VM_Major {
    public VM_Major(){}
    public VM_Major(int id, String title) {
        Id = id;
        Title = title;
    }

    private int Id;
    private String Title;

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
