package softuni.exam.instagraphlite.service.impl;

import com.google.gson.Gson;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import softuni.exam.instagraphlite.models.dto.PictureSeedDto;
import softuni.exam.instagraphlite.models.entity.Picture;
import softuni.exam.instagraphlite.repository.PictureRepository;
import softuni.exam.instagraphlite.service.PictureService;
import softuni.exam.instagraphlite.util.ValidationUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

@Service
public class PictureServiceImpl implements PictureService {

    private static final String PICTURES_FILE_PATH = "src/main/resources/files/pictures.json";

    private final PictureRepository pictureRepository;

    private final Gson gson;
    private final ValidationUtil validationUtil;
    private final ModelMapper modelMapper;

    public PictureServiceImpl(PictureRepository pictureRepository,  Gson gson
            , ValidationUtil validationUtil, ModelMapper modelMapper) {
        this.pictureRepository = pictureRepository;

        this.gson = gson;
        this.validationUtil = validationUtil;
        this.modelMapper = modelMapper;
    }

    @Override
    public boolean areImported() {
        return this.pictureRepository.count() > 0;
    }

    @Override
    public String readFromFileContent() throws IOException {
        return Files.readString(Path.of(PICTURES_FILE_PATH));
    }

    @Override
    public String importPictures() throws IOException {
        PictureSeedDto[] pictureSeedDtos = gson.fromJson(readFromFileContent(),PictureSeedDto[].class);
        StringBuilder sb = new StringBuilder();
        Arrays.stream(pictureSeedDtos)
                .filter(dto -> {
                    boolean isValid = validationUtil.isValid(dto) && !doesEntityExist(dto.getPath());

                    if (isValid) {
                        sb.append(String.format("Picture: Successfully imported Picture, with size %.2f",dto.getSize()));
                    } else {
                        sb.append("Picture: Invalid Picture");
                    }

                    sb.append(System.lineSeparator());

                    return isValid;
                })
                .map(dto -> modelMapper.map(dto, Picture.class))
                .forEach(pictureRepository::save);

        return sb.toString();
    }

    @Override
    public Picture findPictureByPath(String path){
        return this.pictureRepository.findPictureByPath(path);
    }

    @Override
    public boolean doesEntityExist(String path) {
        return this.pictureRepository.existsByPath(path);
    }

    @Override
    public String exportPictures() {
        StringBuilder sb = new StringBuilder();

        this.pictureRepository.findAllBySizeGreaterThan(30000.00)
              .forEach(p -> {
                    sb.append(String.format("%.2f – %s"
                    ,p.getSize()
                    ,p.getPath()));

                    sb.append(System.lineSeparator());
                });

        return sb.toString();
    }




}
