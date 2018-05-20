package com.bot.wattanachaitodolist.repository;

import com.bot.wattanachaitodolist.domain.User;
import org.springframework.data.repository.Repository;

import java.util.Optional;

public interface UserRepository extends Repository<User, String> {

    Optional<User> findByUserId(String user);

    User save(User user);
}
