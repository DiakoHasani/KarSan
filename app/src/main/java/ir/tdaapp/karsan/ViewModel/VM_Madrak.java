package ir.tdaapp.karsan.ViewModel;

import androidx.annotation.NonNull;

//مربوط به مدرک
public class VM_Madrak {
    public VM_Madrak() {
    }

    public VM_Madrak(int id, String title) {
        Id = id;
        Title = title;
    }

    private int Id;
    private String Title;

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getTitle() {
        return Title;
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
