package com.example.MovieLibrary.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.MovieLibrary.model.User;


@Repository
public interface UserDao extends JpaRepository<User, Long> {
	
	User findByEmail(String email);
	
	
	

}
