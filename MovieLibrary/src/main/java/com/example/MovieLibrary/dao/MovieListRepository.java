package com.example.MovieLibrary.dao;


import org.springframework.data.jpa.repository.JpaRepository;
import com.example.MovieLibrary.model.MovieList;

import java.util.List;

public interface MovieListRepository extends JpaRepository<MovieList, Long> {

	
	List<MovieList> findByListnameListnameAndUserId(String listName, Long userId);
	
	
}

