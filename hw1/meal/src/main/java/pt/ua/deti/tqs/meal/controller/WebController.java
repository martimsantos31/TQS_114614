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
        
        Optional<Restaurant> restaurantOpt = restaurantService.getRestaurantById(id);
        
        if (restaurantOpt.isEmpty()) {
            return "redirect:/";
        }
        
        Restaurant restaurant = restaurantOpt.get();
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
        
        Optional<Reservation> reservationOpt = reservationService.createReservation(mealId);
        
        if (reservationOpt.isEmpty()) {
            model.addAttribute("error", "Could not create reservation");
            return "error";
        }
        
        Reservation reservation = reservationOpt.get();
        model.addAttribute("reservation", reservation);
        
        // Format date for display
        Meal meal = reservation.getMeal();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, d MMMM", Locale.ENGLISH);
        model.addAttribute("formattedDate", meal.getAvailableDate().format(formatter));
        
        return "reservation-confirmation";
    }
    
    @GetMapping("/reservation/{token}")
    public String viewReservation(@PathVariable String token, Model model) {
        logger.info("View reservation requested for token: {}", token);
        
        Optional<Reservation> reservationOpt = reservationService.getReservationByToken(token);
        
        if (reservationOpt.isEmpty()) {
            model.addAttribute("error", "Reservation not found");
            return "error";
        }
        
        Reservation reservation = reservationOpt.get();
        model.addAttribute("reservation", reservation);
        
        // Format date for display
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, d MMMM", Locale.ENGLISH);
        model.addAttribute("formattedDate", reservation.getMeal().getAvailableDate().format(formatter));
        
        return "reservation-detail";
    }
    
    @GetMapping("/cancel-reservation/{token}")
    public String cancelReservation(@PathVariable String token, Model model) {
        logger.info("Cancel reservation requested for token: {}", token);
        
        boolean cancelled = reservationService.deleteReservation(token);
        
        if (!cancelled) {
            model.addAttribute("error", "Could not cancel reservation");
            return "error";
        }
        
        return "reservation-cancelled";
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
        if (reservation.get().getMeal() != null && reservation.get().getMeal().getAvailableDate() != null) {
            LocalDate mealDate = reservation.get().getMeal().getAvailableDate();
            WeatherForecast forecast = weatherService.getForecastForDate(mealDate);
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