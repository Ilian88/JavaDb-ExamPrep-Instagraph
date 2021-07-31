package softuni.exam.instagraphlite.models.entity;

import javax.persistence.*;

@Entity
@Table(name = "posts")
public class Post extends BaseEntity{

    private String caption;

    private User user;

    private Picture picture;

    public Post() {
    }

    @Column(nullable = false)
    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }


    @ManyToOne
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }


    @ManyToOne(fetch = FetchType.EAGER)
    public Picture getPicture() {
        return picture;
    }

    public void setPicture(Picture picture) {
        this.picture = picture;
    }
}
