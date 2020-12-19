package ir.tdaapp.karsanjob.ViewModel;

/**
 * Created by Diako on 8/27/2019.
 */

//این کلاس ویو مدل عکس های اسلایدر می باشد
public class VM_SliderHome {

    private int Id;
    private String ImageUrl="";
    private int Order;
    private String Description;
    private String Link;
    private int TypeLink;

    public int getTypeLink() {
        return TypeLink;
    }

    public void setTypeLink(int typeLink) {
        TypeLink = typeLink;
    }

    public void setLink(String link) {
        Link = link;
    }

    public String getLink() {
        return Link;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getImageUrl() {
        return ImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        ImageUrl = imageUrl;
    }

    public int getOrder() {
        return Order;
    }

    public void setOrder(int order) {
        Order = order;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }
}
