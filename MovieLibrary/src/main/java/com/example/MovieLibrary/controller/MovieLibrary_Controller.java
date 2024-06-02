package com.example.MovieLibrary.controller;

import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import com.example.MovieLibrary.dao.MovieListRepository;
import com.example.MovieLibrary.model.ListName;
import com.example.MovieLibrary.model.Movie;
import com.example.MovieLibrary.model.MovieList;
import com.example.MovieLibrary.model.User;
import com.example.MovieLibrary.service.ListNameService;
import com.example.MovieLibrary.service.MovieListService;
import com.example.MovieLibrary.service.UserService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.core.context.SecurityContextHolder;

import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.security.Principal;
import java.util.*;


@Controller
public class MovieLibrary_Controller {
	
	
	
	
	@Autowired
	UserService userservice;
	
	
	@Autowired
	ListNameService listnameservice;
	
	@Autowired
	MovieListService movieListService;
	
	@Autowired
	HttpSession httpsession;
	
	
	@Value("${omdb.api.key}")
     String apiKey;
	
	
	String OMDB_API_URL="http://www.omdbapi.com/";
	
	@GetMapping("/search")
    public String searchMovies(@RequestParam("query") String query, Model model) {
		
		String email = getLoggedInUserEmail();
		
		if(email!=null)
		{
			User user = userservice.getUserByEmail(email);
			if(user!=null)
			{
		 
        RestTemplate restTemplate = new RestTemplate();
        String url = OMDB_API_URL + "?apikey=" + apiKey + "&s=" + query;
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

        ObjectMapper objectMapper = new ObjectMapper();
        List<Movie> movies = new ArrayList<>();
        try {
            JsonNode root = objectMapper.readTree(response.getBody());
            JsonNode searchResults = root.path("Search");
            if (searchResults.isArray()) {
                for (JsonNode movieNode : searchResults) {
                    Movie movie = new Movie();
                    movie.setTitle(movieNode.path("Title").asText());
                    movie.setPoster(movieNode.path("Poster").asText());
                    movie.setImdbID(movieNode.path("imdbID").asText());
                    System.out.println(movie.getImdbID());
                    movies.add(movie);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
			
        
       
        
        
        
        List<ListName> listnames = listnameservice.getListNamesByUserId(user.getId());
        
        model.addAttribute("email",email);
        model.addAttribute("movies", movies);
        model.addAttribute("listName",listnames);
        
        return "searchResults";
    }
		}
		return "home";
	}
	
	@PostMapping("/addToList")
	public String addToList(@RequestParam String imdbID, @RequestParam String listName, @RequestParam String email) {
		
		
		
        movieListService.addMovieToList(imdbID, listName, email);
        return "redirect:/home"; // Redirect to the movie list page after adding
    }
	
	
	
	@GetMapping("/")
	public String login(Model model)
	{
		return "login";
	}
	
	@GetMapping("/login")
	public String login1(Model model)
	{
		return "login";
	}
	
	@GetMapping("/register")
	public String register(Model model)
	{
		model.addAttribute("user",new User());
		return "register";
	}
	
	@GetMapping("/home")
    public String home(Model model) {
        String email = getLoggedInUserEmail();
        if (email != null) {
            User user = userservice.getUserByEmail(email);
            if (user != null) {
                List<ListName> listNames = listnameservice.getListNamesByUserId(user.getId());
                Map<String, List<Movie>> listMoviesMap = new HashMap<>();
                for (ListName listName : listNames) {
                    List<Movie> movies1 = movieListService.getMoviesByListName(listName.getListname(), email); // Fetch movies by list name
                    listMoviesMap.put(listName.getListname(), movies1); // Add list name and its movies to the map
                }
                model.addAttribute("listName", listNames);
                model.addAttribute("email", email);
                model.addAttribute("listMoviesMap",listMoviesMap);
                return "home";
            }
        }
        return "login";
    }
	
	@GetMapping("/mylists")
	public String mylists(Model model)
	{
		String email=getLoggedInUserEmail();
		if(email!=null)
		{
			User user=userservice.getUserByEmail(email);
			if(user!=null)
			{
				List<ListName> listnames=listnameservice.getListNamesByUserId(user.getId());
				Map<String,List<Movie>> listMoviesMap=new HashMap<>();
				for(ListName listname: listnames)
				{
					List<Movie>movies1=movieListService.getMoviesByListName(listname.getListname(), email);
					listMoviesMap.put(listname.getListname(), movies1);
				}
				model.addAttribute("listName", listnames);
				model.addAttribute("listMoviesMap",listMoviesMap);
				model.addAttribute("email",email);
			}
			return "mylists";
		}
		return "login";
	}
	
	
	 
	

	
	
	@PostMapping("/registeruser")
    public String registerUser(@ModelAttribute User user) {
        
        userservice.register(user);
        return "redirect:/login";
    }
	
	@PostMapping("/loginuser")
    public String loginUser(@RequestParam("email") String email,@RequestParam("password") String password) {
        if (userservice.login(email, password)) {
        	
        	httpsession.setAttribute("loggedInUser", email);
        	return "redirect:/home"; 
        } else {
            return "redirect:/login";
        }
    }
	
	@PostMapping("/createList")
	public String addList(@RequestParam String email,@RequestParam String name)
	{
		if(email!=null)
		{
		listnameservice.addList(name, getLoggedInUserEmail());
		
		return "redirect:/home";
		}
		return "login";
		
	}
	
	
	private String getLoggedInUserEmail() {
        return (String) httpsession.getAttribute("loggedInUser");
    }
	
	@GetMapping("/logout")
    public String logout() {
        // Invalidate the session
        httpsession.invalidate();
        return "redirect:/login"; // Redirect the user to the login page after logout
    }

	private boolean isLoggedIn() {
		
		return httpsession.getAttribute("loggedInUser")!=null;
	}
	
	
	
	
	
	 
	

}
