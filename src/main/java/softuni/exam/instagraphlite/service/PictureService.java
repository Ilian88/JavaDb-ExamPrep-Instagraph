package softuni.exam.instagraphlite.service;

import softuni.exam.instagraphlite.models.entity.Picture;

import java.io.IOException;
import java.util.List;
import java.util.Set;

public interface PictureService {
    boolean areImported();
    String readFromFileContent() throws IOException;
    String importPictures() throws IOException;

    Picture findPictureByPath(String path);

    boolean doesEntityExist(String path);

    String exportPictures();



}
