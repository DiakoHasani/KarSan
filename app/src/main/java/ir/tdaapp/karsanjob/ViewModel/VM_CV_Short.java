package ir.tdaapp.karsanjob.ViewModel;

public class VM_CV_Short {

    private int Id;
    private String Name;
    private String Madrak;
    private String Major;
    private String DateInsert;
    private String Image;

    public int getId() {
        return Id;
    }

    public String getName() {
        return Name;
    }

    public String getMadrak() {
        return Madrak;
    }

    public String getMajor() {
        return Major;
    }

    public String getDateInsert() {
        return DateInsert;
    }

    public String getImage() {
        return Image;
    }

    public void setId(int id) {
        Id = id;
    }

    public void setName(String name) {
        Name = name;
    }

    public void setMadrak(String madrak) {
        Madrak = madrak;
    }

    public void setMajor(String major) {
        Major = major;
    }

    public void setDateInsert(String dateInsert) {
        DateInsert = dateInsert;
    }

    public void setImage(String image) {
        Image = image;
    }
}
