package shop.apppang.domain.user.repository;

import org.springframework.stereotype.Repository;
import shop.apppang.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByEmail(String email);

    boolean existsByEmailAndIdNot(String email, Long id);

    Optional<User> findByEmail(String email);

    List<User> findByNameAndPhoneNumber(String name, String phoneNumber);

    Optional<User> findByEmailAndNameAndPhoneNumber(String email, String name, String phoneNumber);
}