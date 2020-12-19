package ir.tdaapp.karsanjob.ViewModel;

import androidx.annotation.NonNull;

public class VM_Insurance {
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
