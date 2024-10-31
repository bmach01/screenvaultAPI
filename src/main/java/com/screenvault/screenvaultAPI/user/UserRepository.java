package com.screenvault.screenvaultAPI.user;

import com.mongodb.lang.Nullable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<User, String> {
    @Nullable
    User findByUsername(String username);

    @Nullable
    User findByLogin(String login);

}
