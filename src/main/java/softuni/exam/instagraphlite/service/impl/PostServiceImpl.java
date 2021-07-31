package softuni.exam.instagraphlite.service.impl;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import softuni.exam.instagraphlite.models.dto.PostSeedRootDto;
import softuni.exam.instagraphlite.models.entity.Picture;
import softuni.exam.instagraphlite.models.entity.Post;
import softuni.exam.instagraphlite.models.entity.User;
import softuni.exam.instagraphlite.repository.PostRepository;
import softuni.exam.instagraphlite.service.PictureService;
import softuni.exam.instagraphlite.service.PostService;
import softuni.exam.instagraphlite.service.UserService;
import softuni.exam.instagraphlite.util.ValidationUtil;
import softuni.exam.instagraphlite.util.XmlParser;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

@Service
public class PostServiceImpl implements PostService {

    private static final String POST_FILES_PATH = "src/main/resources/files/posts.xml";

    private final PostRepository postRepository;
    private final XmlParser xmlParser;
    private final ModelMapper modelMapper;
    private final ValidationUtil validationUtil;
    private final PictureService pictureService;
    private final UserService userService;



    public PostServiceImpl(PostRepository postRepository, XmlParser xmlParser, ModelMapper modelMapper
            , ValidationUtil validationUtil, PictureService pictureService, UserService userService) {
        this.postRepository = postRepository;
        this.xmlParser = xmlParser;
        this.modelMapper = modelMapper;
        this.validationUtil = validationUtil;
        this.pictureService = pictureService;
        this.userService = userService;
    }

    @Override
    public boolean areImported() {
        return this.postRepository.count() > 0;
    }

    @Override
    public String readFromFileContent() throws IOException {
        return Files.readString(Path.of(POST_FILES_PATH));
    }

    @Override
    public String importPosts() throws IOException, JAXBException {
        PostSeedRootDto postSeedRootDto = xmlParser.fromFile(POST_FILES_PATH,PostSeedRootDto.class);
        StringBuilder sb = new StringBuilder();
        postSeedRootDto.getPostSeedDto().stream()
                .filter(dto -> {
                    boolean isValid = validationUtil.isValid(dto)
                            && userService.usernameExists(dto.getUserPostDto().getUsername())
                            && pictureService.doesEntityExist(dto.getPicturePostDto().getPath());

                    sb.append(isValid ?
                            "Post: Successfully imported Post, made by " + dto.getUserPostDto().getUsername()
                            : sb.append("Invalid post")
                            .append(System.lineSeparator()));

                    return isValid;
                })
                .map(dto -> {
                    Post post = modelMapper.map(dto,Post.class);
                    post.setUser(userService.findUserByUsername(dto.getUserPostDto().getUsername()));
                    post.setPicture(pictureService.findPictureByPath(dto.getPicturePostDto().getPath()));

                    return post;
                })
                .forEach(postRepository::save);

        return sb.toString();
    }
}
