package ir.tdaapp.karsanjob.ViewModel;

public class VM_Short_Item {
    private int Id;
    private String Title;
    private String Price;
    private String Time;
    private int Type;
    private String Image;

    public int getId() {
        return Id;
    }

    public String getTitle() {
        return Title;
    }

    public String getPrice() {
        return Price;
    }

    public String getTime() {
        return Time;
    }


    public void setId(int id) {
        Id = id;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public void setPrice(String price) {
        Price = price;
    }

    public void setTime(String time) {
        Time = time;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public int getType() {
        return Type;
    }

    public void setType(int type) {
        Type = type;
    }
}
