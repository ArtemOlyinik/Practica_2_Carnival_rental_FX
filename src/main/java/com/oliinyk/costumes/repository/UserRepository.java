package com.oliinyk.costumes.repository;

import com.oliinyk.costumes.model.User;
import java.util.Optional;

public interface UserRepository extends Repository<User> {
    Optional<User> findByEmail(String email);
}
