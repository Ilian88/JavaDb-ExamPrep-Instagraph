package softuni.exam.instagraphlite.service;

import softuni.exam.instagraphlite.models.entity.User;

import java.io.IOException;


public interface UserService {


    boolean areImported();
    String readFromFileContent() throws IOException;
    String importUsers() throws IOException;

    boolean usernameExists(String username);

    String exportUsersWithTheirPosts();

    User findUserByUsername(String username);
}
