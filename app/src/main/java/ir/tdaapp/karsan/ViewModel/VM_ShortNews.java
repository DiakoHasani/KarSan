package ir.tdaapp.karsan.ViewModel;

//مربوط به نمایش لیست اخبار
public class VM_ShortNews {
    private int Id;
    private String Title;
    private String AbstractNews;
    private String DateInsert;
    private String Image;

    public int getId() {
        return Id;
    }

    public String getTitle() {
        return Title;
    }

    public String getAbstractNews() {
        return AbstractNews;
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

    public void setTitle(String title) {
        Title = title;
    }

    public void setAbstractNews(String abstractNews) {
        AbstractNews = abstractNews;
    }

    public void setDateInsert(String dateInsert) {
        DateInsert = dateInsert;
    }

    public void setImage(String image) {
        Image = image;
    }
}
