package pt.ua.deti.tqs.meal.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pt.ua.deti.tqs.meal.domain.Meal;
import pt.ua.deti.tqs.meal.domain.Reservation;
import pt.ua.deti.tqs.meal.domain.Restaurant;
import pt.ua.deti.tqs.meal.exception.ResourceNotFoundException;
import pt.ua.deti.tqs.meal.service.MealService;
import pt.ua.deti.tqs.meal.service.ReservationService;
import pt.ua.deti.tqs.meal.service.RestaurantService;
import pt.ua.deti.tqs.meal.service.WeatherService;
import pt.ua.deti.tqs.meal.service.WeatherService.WeatherForecast;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@CrossOrigin(origins = "http://localhost:5473")
public class WebController {
    private static final Logger logger = LoggerFactory.getLogger(WebController.class);
    
    @Autowired
    private RestaurantService restaurantService;
    
    @Autowired
    private MealService mealService;
    
    @Autowired
    private ReservationService reservationService;
    
    @Autowired
    private WeatherService weatherService;
    
    @GetMapping("/")
    public String home(Model model) {
        logger.info("Home page requested");
        
        List<Restaurant> restaurants = restaurantService.getAllRestaurants();
        model.addAttribute("restaurants", restaurants);
        
        return "home";
    }
    
    @GetMapping("/restaurant/{id}")
    public String restaurantDetail(@PathVariable Long id, Model model) {
        logger.info("Restaurant detail page requested for ID: {}", id);
        
        try {
            Restaurant restaurant = restaurantService.getRestaurantById(id);
            model.addAttribute("restaurant", restaurant);
            
            int days = 7; // Show meals for the next 7 days
            List<Meal> meals = mealService.getMealsForRestaurant(id, days);
            
            // Group meals by date
            Map<LocalDate, List<Meal>> mealsByDate = meals.stream()
                    .collect(Collectors.groupingBy(Meal::getAvailableDate));
            
            // Format dates for display
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, d MMMM", Locale.ENGLISH);
            Map<String, List<Meal>> formattedMeals = new LinkedHashMap<>();
            
            // Get weather forecasts for the dates
            Map<LocalDate, WeatherForecast> weatherForecasts = weatherService.getForecastForDates(
                    mealsByDate.keySet());
            
            // Sort dates and format for display
            mealsByDate.keySet().stream()
                    .sorted()
                    .forEach(date -> {
                        String displayDate = date.format(formatter);
                        String today = LocalDate.now().equals(date) ? " (Today)" : "";
                        String tomorrow = LocalDate.now().plusDays(1).equals(date) ? " (Tomorrow)" : "";
                        displayDate = displayDate + today + tomorrow;
                        
                        formattedMeals.put(displayDate, mealsByDate.get(date));
                    });
            
            model.addAttribute("mealsByDate", formattedMeals);
            model.addAttribute("weatherForecasts", weatherForecasts);
            
            return "restaurant-detail";
        } catch (ResourceNotFoundException e) {
            return "redirect:/";
        }
    }
    
    @GetMapping("/meal/{id}")
    public String mealDetail(@PathVariable Long id, Model model) {
        logger.info("Meal detail page requested for ID: {}", id);
        
        Optional<Meal> mealOpt = mealService.getMealById(id);
        
        if (mealOpt.isEmpty()) {
            return "redirect:/";
        }
        
        Meal meal = mealOpt.get();
        model.addAttribute("meal", meal);
        
        // Get weather for the meal date
        WeatherForecast weather = weatherService.getForecastForDate(meal.getAvailableDate());
        model.addAttribute("weather", weather);
        
        return "meal-detail";
    }
    
    @PostMapping("/reserve")
    public String reserveMeal(@RequestParam Long mealId, Model model) {
        logger.info("Reservation requested for meal ID: {}", mealId);
        
        try {
            Reservation reservation = reservationService.createReservation(mealId);
            model.addAttribute("reservation", reservation);
            
            // Format date for display
            Meal meal = reservation.getMeal();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, d MMMM", Locale.ENGLISH);
            model.addAttribute("formattedDate", meal.getAvailableDate().format(formatter));
            
            return "reservation-confirmation";
        } catch (ResourceNotFoundException e) {
            model.addAttribute("error", "Meal not found");
            return "error";
        } catch (Exception e) {
            model.addAttribute("error", "Could not create reservation: " + e.getMessage());
            return "error";
        }
    }
    
    @GetMapping("/reservation/{token}")
    public String viewReservation(@PathVariable String token, Model model) {
        logger.info("View reservation requested for token: {}", token);
        
        try {
            Reservation reservation = reservationService.getReservationByToken(token);
            model.addAttribute("reservation", reservation);
            
            // Format date for display
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, d MMMM", Locale.ENGLISH);
            model.addAttribute("formattedDate", reservation.getMeal().getAvailableDate().format(formatter));
            
            return "reservation-detail";
        } catch (ResourceNotFoundException e) {
            model.addAttribute("error", "Reservation not found");
            return "error";
        }
    }
    
    @GetMapping("/cancel-reservation/{token}")
    public String cancelReservation(@PathVariable String token, Model model) {
        logger.info("Cancel reservation requested for token: {}", token);
        
        try {
            reservationService.deleteReservation(token);
            return "reservation-cancelled";
        } catch (ResourceNotFoundException | IllegalStateException e) {
            model.addAttribute("error", "Could not cancel reservation: " + e.getMessage());
            return "error";
        }
    }
    
    @GetMapping("/reservations/new")
    public String newReservation(@RequestParam Long mealId, Model model) {
        logger.info("Displaying new reservation form for meal ID: {}", mealId);
        
        Optional<Meal> meal = mealService.getMealById(mealId);
        if (meal.isEmpty()) {
            return "redirect:/";
        }
        
        model.addAttribute("meal", meal.get());
        
        return "reservation-form";
    }
    
    @PostMapping("/reservations/create")
    public String createReservation(@RequestParam Long mealId, RedirectAttributes redirectAttributes) {
        logger.info("Creating new reservation for meal ID: {}", mealId);
        
        try {
            Reservation reservation = reservationService.createReservation(mealId);
            redirectAttributes.addFlashAttribute("success", "Reservation created successfully!");
            redirectAttributes.addFlashAttribute("token", reservation.getToken());
            return "redirect:/reservations/confirmation";
        } catch (ResourceNotFoundException e) {
            redirectAttributes.addFlashAttribute("error", "Failed to create reservation. Meal not found.");
            return "redirect:/";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to create reservation: " + e.getMessage());
            return "redirect:/";
        }
    }
    
    @GetMapping("/reservations/confirmation")
    public String confirmReservation() {
        return "reservation-confirmation";
    }
    
    @GetMapping("/reservations/check")
    public String checkReservationForm() {
        return "check-reservation";
    }
    
    @PostMapping("/reservations/check")
    public String checkReservation(@RequestParam String token, RedirectAttributes redirectAttributes) {
        logger.info("Checking reservation with token: {}", token);
        
        try {
            reservationService.getReservationByToken(token);
            redirectAttributes.addAttribute("token", token);
            return "redirect:/reservations/{token}";
        } catch (ResourceNotFoundException e) {
            redirectAttributes.addFlashAttribute("error", "Reservation not found");
            return "redirect:/reservations/check";
        }
    }
    
    @GetMapping("/reservations/{token}")
    public String getReservation(@PathVariable String token, Model model) {
        logger.info("Displaying reservation details for token: {}", token);
        
        try {
            Reservation reservation = reservationService.getReservationByToken(token);
            model.addAttribute("reservation", reservation);
            
            // Get weather forecast for the meal date
            if (reservation.getMeal() != null && reservation.getMeal().getAvailableDate() != null) {
                LocalDate mealDate = reservation.getMeal().getAvailableDate();
                WeatherForecast forecast = weatherService.getForecastForDate(mealDate);
                model.addAttribute("forecast", forecast);
            }
            
            return "reservation-details";
        } catch (ResourceNotFoundException e) {
            return "redirect:/reservations/check";
        }
    }
    
    @PostMapping("/reservations/{token}/use")
    public String useReservation(@PathVariable String token, RedirectAttributes redirectAttributes) {
        logger.info("Using reservation with token: {}", token);
        
        try {
            Reservation reservation = reservationService.markReservationAsUsed(token);
            redirectAttributes.addFlashAttribute("success", "Reservation marked as used successfully!");
            return "redirect:/reservations/{token}";
        } catch (ResourceNotFoundException e) {
            redirectAttributes.addFlashAttribute("error", "Reservation not found");
            return "redirect:/reservations/check";
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("error", "Reservation already used");
            return "redirect:/reservations/{token}";
        }
    }
    
    @GetMapping("/about")
    public String about() {
        return "about";
    }
} 