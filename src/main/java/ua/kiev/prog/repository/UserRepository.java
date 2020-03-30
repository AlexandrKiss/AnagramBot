package ua.kiev.prog.repository;

import ua.kiev.prog.models.*;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<CustomUser, Long> {
    CustomUser findByChatId(long id);
}
