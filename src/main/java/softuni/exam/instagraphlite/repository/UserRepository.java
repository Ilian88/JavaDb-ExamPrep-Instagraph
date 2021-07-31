package softuni.exam.instagraphlite.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import softuni.exam.instagraphlite.models.entity.User;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {

    Boolean existsByUsername(String username);

    User findUserByUsername(String username);

    @Query("SElECT DISTINCT u FROM User u JOIN FETCH u.posts p ORDER BY size(p) DESC ,u.id")
    List<User> findAllByCountPostDescAndByUserId();
}
