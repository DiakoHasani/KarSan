package ir.tdaapp.karsanjob.ViewModel;

import androidx.annotation.NonNull;
import ir.tdaapp.karsanjob.Enum.Gender;

public class VM_Gender {
    private Gender Type;
    private String Title;
    private int Id;

    public VM_Gender(Gender type, String title,int Id) {
        Type = type;
        Title = title;
        this.Id=Id;
    }

    public VM_Gender() {
    }

    public Gender getType() {
        return Type;
    }

    public String getTitle() {
        return Title;
    }

    public void setType(Gender type) {
        Type = type;
    }

    public void setTitle(String title) {
        Title = title;
    }

    @NonNull
    @Override
    public String toString() {
        return Title;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }
}
