package com.example.MovieLibrary.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.MovieLibrary.model.ListName;

@Repository
public interface ListCat extends JpaRepository<ListName, Integer> {
	
	List<ListName> findByUserId(Long userId);

	ListName findByListnameAndUser_Id(String listName, Long id);

	
}
