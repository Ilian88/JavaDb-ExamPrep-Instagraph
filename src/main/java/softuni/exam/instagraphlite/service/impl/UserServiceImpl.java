package softuni.exam.instagraphlite.service.impl;

import com.google.gson.Gson;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import softuni.exam.instagraphlite.models.dto.UserSeedDto;
import softuni.exam.instagraphlite.models.entity.Picture;
import softuni.exam.instagraphlite.models.entity.User;
import softuni.exam.instagraphlite.repository.UserRepository;
import softuni.exam.instagraphlite.service.PictureService;
import softuni.exam.instagraphlite.service.UserService;
import softuni.exam.instagraphlite.util.ValidationUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Objects;

@Service
public class UserServiceImpl implements UserService {

    private static final String USERS_FILE_PATH = "src/main/resources/files/users.json";
    private final PictureService pictureService;
    private final UserRepository userRepository;
    private final Gson gson;
    private final ModelMapper modelMapper;
    private final ValidationUtil validationUtil;

    public UserServiceImpl(PictureService pictureService, UserRepository userRepository, Gson gson, ModelMapper modelMapper
            , ValidationUtil validationUtil) {
        this.pictureService = pictureService;
        this.userRepository = userRepository;
        this.gson = gson;
        this.modelMapper = modelMapper;
        this.validationUtil = validationUtil;
    }

    @Override
    public boolean areImported() {
        return this.userRepository.count() > 0;
    }

    @Override
    public String readFromFileContent() throws IOException {
        return Files.readString(Path.of(USERS_FILE_PATH));
    }

    @Override
    public String importUsers() throws IOException {
        UserSeedDto [] userSeedDtos = gson.fromJson(readFromFileContent(),UserSeedDto[].class);
        StringBuilder sb = new StringBuilder();
        Arrays.stream(userSeedDtos)
                .filter(dto -> {
                    return validationUtil.isValid(dto) && !usernameExists(dto.getUsername());
                })
                .map(dto -> {
                    User user = modelMapper.map(dto,User.class);
                    Picture picture = this.pictureService.findPictureByPath(dto.getProfilePicture());

                    if (picture != null) {
                        user.setPicture(picture);
                        sb.append(String.format("User: Successfully imported User: %s",
                                dto.getUsername()));

                        return user;

                    } else {
                        sb.append("Invalid User");
                    }

                    sb.append(System.lineSeparator());

                   return null;
                })
                .filter(Objects::nonNull)
                .forEach(userRepository::save);

        return sb.toString();
    }

    @Override
    public boolean usernameExists(String username) {
        return this.userRepository.existsByUsername(username);
    }

    @Override
    public String exportUsersWithTheirPosts() {

        StringBuilder sb = new StringBuilder();
        this.userRepository.findAllByCountPostDescAndByUserId()
                .forEach(u -> {
                    sb
                            .append(String.format("""
                                                    User: %s
                                                    Post count: %d
                                                    """,
                                                    u.getUsername()
                                            ,u.getPosts().size())
                                    );
                        u.getPosts().forEach(p -> {
                            sb.append(String.format("""
                                            Post Details:
                                            ----Caption: %s
                                             ----Picture Size: %.2f""",
                                    p.getCaption()
                            ,p.getPicture().getSize()));
                            sb.append(System.lineSeparator());
                        });
                });

        return sb.toString();
    }

    @Override
    public User findUserByUsername(String username) {
        return this.userRepository.findUserByUsername(username);
    }
}
