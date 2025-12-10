package com.lostFound.lostFound.repository;


import com.lostFound.lostFound.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepo extends JpaRepository<User, Long> {

}

