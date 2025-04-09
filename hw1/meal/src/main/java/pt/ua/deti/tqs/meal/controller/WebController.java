package pt.ua.deti.tqs.meal.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pt.ua.deti.tqs.meal.domain.Meal;
import pt.ua.deti.tqs.meal.domain.Reservation;
import pt.ua.deti.tqs.meal.domain.Restaurant;
import pt.ua.deti.tqs.meal.service.MealService;
import pt.ua.deti.tqs.meal.service.ReservationService;
import pt.ua.deti.tqs.meal.service.RestaurantService;
import pt.ua.deti.tqs.meal.service.WeatherService;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
        logger.info("Displaying home page");
        List<Restaurant> restaurants = restaurantService.getAllRestaurants();
        model.addAttribute("restaurants", restaurants);
        return "home";
    }
    
    @GetMapping("/restaurants/{id}")
    public String getRestaurant(@PathVariable Long id, Model model) {
        logger.info("Displaying restaurant page for ID: {}", id);
        
        Optional<Restaurant> restaurant = restaurantService.getRestaurantById(id);
        if (restaurant.isEmpty()) {
            return "redirect:/";
        }
        
        List<Meal> meals = mealService.getMealsForRestaurant(id, 7);
        
        // Get weather forecasts for the meal dates
        Map<LocalDate, WeatherService.WeatherForecast> forecasts = weatherService.getForecastForDates(
                meals.stream()
                    .map(Meal::getDate)
                    .collect(Collectors.toSet())
        );
        
        model.addAttribute("restaurant", restaurant.get());
        model.addAttribute("meals", meals);
        model.addAttribute("forecasts", forecasts);
        
        return "restaurant";
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
        
        Optional<Reservation> reservation = reservationService.createReservation(mealId);
        if (reservation.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Failed to create reservation. Meal may no longer be available.");
            return "redirect:/";
        }
        
        redirectAttributes.addFlashAttribute("success", "Reservation created successfully!");
        redirectAttributes.addFlashAttribute("token", reservation.get().getToken());
        
        return "redirect:/reservations/confirmation";
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
        
        Optional<Reservation> reservation = reservationService.getReservationByToken(token);
        if (reservation.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Reservation not found");
            return "redirect:/reservations/check";
        }
        
        redirectAttributes.addAttribute("token", token);
        return "redirect:/reservations/{token}";
    }
    
    @GetMapping("/reservations/{token}")
    public String getReservation(@PathVariable String token, Model model) {
        logger.info("Displaying reservation details for token: {}", token);
        
        Optional<Reservation> reservation = reservationService.getReservationByToken(token);
        if (reservation.isEmpty()) {
            return "redirect:/reservations/check";
        }
        
        model.addAttribute("reservation", reservation.get());
        
        // Get weather forecast for the meal date
        if (reservation.get().getMeal() != null && reservation.get().getMeal().getDate() != null) {
            LocalDate mealDate = reservation.get().getMeal().getDate();
            WeatherService.WeatherForecast forecast = weatherService.getForecastForDate(mealDate);
            model.addAttribute("forecast", forecast);
        }
        
        return "reservation-details";
    }
    
    @PostMapping("/reservations/{token}/use")
    public String useReservation(@PathVariable String token, RedirectAttributes redirectAttributes) {
        logger.info("Using reservation with token: {}", token);
        
        Optional<Reservation> reservation = reservationService.markReservationAsUsed(token);
        if (reservation.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Failed to use reservation. It may be already used or not found.");
            return "redirect:/reservations/{token}";
        }
        
        redirectAttributes.addFlashAttribute("success", "Reservation marked as used successfully!");
        
        return "redirect:/reservations/{token}";
    }
    
    @GetMapping("/about")
    public String about() {
        return "about";
    }
} 