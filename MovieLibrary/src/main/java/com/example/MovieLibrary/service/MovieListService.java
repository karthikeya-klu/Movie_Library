package com.example.MovieLibrary.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.example.MovieLibrary.dao.MovieListRepository;
import com.example.MovieLibrary.model.ListName;
import com.example.MovieLibrary.model.Movie;
import com.example.MovieLibrary.model.MovieList;
import com.example.MovieLibrary.model.User;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class MovieListService {
	
	@Autowired
    private MovieListRepository movieListRepository;

    @Autowired
    private ListNameService listNameService;
    
    @Autowired
    UserService userService;

    public void addMovieToList(String imdbID, String listName, String userEmail) {
        // Implement your logic to add the movie to the list in the database
    	User user=userService.getUserByEmail(userEmail);
    	ListName listname=listNameService.getListByNameAndUserId(listName, user.getId());
        MovieList movieList = new MovieList();
        movieList.setMovieid(imdbID);
        movieList.setListname(listname);
        movieList.setUser(user);

        movieListRepository.save(movieList);
    }
    
    public void save(MovieList movieList)
    {
    	movieListRepository.save(movieList);
    }

    
    public List<Movie> getMoviesByListName(String listname, String email) {
        // Retrieve the user by email
        User user = userService.getUserByEmail(email);
        if (user != null) {
            // Retrieve the list of movies for the given list name and user ID
            List<MovieList> movieLists = movieListRepository.findByListnameListnameAndUserId(listname, user.getId());
            List<Movie> movies = new ArrayList<>();
            // Iterate over the movie lists and extract the movie IDs
            for (MovieList movieList : movieLists) {
                // Fetch the movie details using the movie ID and add it to the list
                Movie movie = fetchMovieDetails(movieList.getMovieid());
                if (movie != null) {
                    movies.add(movie);
                }
            }
            return movies;
        } 
            return Collections.emptyList(); 
        
    }

    
    @Value("${omdb.api.key}")
    String apiKey;
    
    String OMDB_API_URL="http://www.omdbapi.com/";

    private Movie fetchMovieDetails(String imdbID) {
        RestTemplate restTemplate = new RestTemplate();
        String url = OMDB_API_URL + "?apikey=" + apiKey + "&i=" + imdbID; // Fetch movie details by IMDb ID
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode root = objectMapper.readTree(response.getBody());
            Movie movie = new Movie();
            movie.setTitle(root.path("Title").asText());
            movie.setPoster(root.path("Poster").asText());
            movie.setImdbID(root.path("imdbID").asText());
            return movie;
        } catch (IOException e) {
            e.printStackTrace();
            return null; // Return null in case of an error
        }
    }

}
