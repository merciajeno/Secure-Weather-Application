package com.mercia.weather.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mercia.weather.entities.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long>{

}
