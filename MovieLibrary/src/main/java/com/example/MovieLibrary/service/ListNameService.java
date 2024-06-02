package com.example.MovieLibrary.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.MovieLibrary.dao.ListCat;
import com.example.MovieLibrary.model.ListName;
import com.example.MovieLibrary.model.MovieList;
import com.example.MovieLibrary.model.User;

@Service
public class ListNameService {
	
	@Autowired
	ListCat listcat;
	
	@Autowired
	UserService userservice;
	
	public String addList(String name,String userEmail) {
    	User user=userservice.getUserByEmail(userEmail);
    	if(user!=null)
    	{
    		ListName listname=new ListName();
    		listname.setListname(name);
    		listname.setUser(user);
    		listcat.save(listname);
    	}
        return "success";
    }

	
	public List<ListName> getListNamesByUserId(Long userId) {
        return listcat.findByUserId(userId);
    }


	public ListName getListByNameAndUserId(String listName, Long id) {
		// TODO Auto-generated method stub
		return listcat.findByListnameAndUser_Id(listName, id);
	}


	public List<ListName> getAllListNamesByUserId(User user) {
		// TODO Auto-generated method stub
		listcat.findByUserId(user.getId());
		return null;
	}


	

}
