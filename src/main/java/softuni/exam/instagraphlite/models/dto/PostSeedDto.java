package softuni.exam.instagraphlite.models.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "post")
@XmlAccessorType(XmlAccessType.FIELD)
public class PostSeedDto {

    @XmlElement(name = "caption")
    private String caption;

    @XmlElement(name = "user")
    private UserPostDto userPostDto;

    @XmlElement(name = "picture")
    private PicturePostDto picturePostDto;

    public PostSeedDto() {
    }

    @NotNull
    @Size(min = 21)
    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    @NotNull
    public UserPostDto getUserPostDto() {
        return userPostDto;
    }

    public void setUserPostDto(UserPostDto userPostDto) {
        this.userPostDto = userPostDto;
    }

    @NotNull
    public PicturePostDto getPicturePostDto() {
        return picturePostDto;
    }

    public void setPicturePostDto(PicturePostDto picturePostDto) {
        this.picturePostDto = picturePostDto;
    }
}
