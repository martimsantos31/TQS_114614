package pt.ua.deti.tqs.lab6_4carsassured.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pt.ua.deti.tqs.lab6_4carsassured.model.Car;
import pt.ua.deti.tqs.lab6_4carsassured.service.CarManagerService;

import java.util.List;

@RestController
@RequestMapping("/cars")
public class CarController {
    private CarManagerService carManagerService;

    public CarController(CarManagerService carManagerService) {
        this.carManagerService = carManagerService;
    }

    @PostMapping
    public ResponseEntity<Car> createCar(@RequestBody Car car){
        return ResponseEntity.ok(carManagerService.save(car));
    }

    @GetMapping
    public List<Car> getAllCars(){
        return carManagerService.findAll();
    }

    @GetMapping("/{carId}")
    public ResponseEntity<Car> getCarById(@PathVariable Long carId){
        return carManagerService.getCarDetails(carId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

}
