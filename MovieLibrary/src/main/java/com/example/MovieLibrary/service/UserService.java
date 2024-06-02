package com.example.MovieLibrary.service;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.MovieLibrary.dao.MovieListRepository;
import com.example.MovieLibrary.dao.UserDao;
import com.example.MovieLibrary.model.MovieList;
import com.example.MovieLibrary.model.User;

@Service
public class UserService {
	
	
	@Autowired
	UserDao userdao;
	
	@Autowired
	MovieListRepository movieListRepository;
	

	
	

	public void register(User user) {
		
		
		
		userdao.save(user);
		
		
		
	}
	
	public boolean login(String email, String password) {
		User user = userdao.findByEmail(email);
	    if (user != null && password.equals(user.getPassword())) {
	        return true;
	    }
	    return false;
    }

	public User findByEmail(String email) {
		// TODO Auto-generated method stub
		User user=userdao.findByEmail(email);
		return user;
	}

	public User getUserByEmail(String email) {
        return userdao.findByEmail(email);
    }

	public Long findUserIdByEmail(String email) {
        User user = userdao.findByEmail(email);
        return user != null ? user.getId() : null;
    }

	
	
	
	

	

}
