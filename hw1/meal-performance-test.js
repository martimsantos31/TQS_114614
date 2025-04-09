import http from 'k6/http';
import { check, sleep } from 'k6';
import { Counter, Rate, Trend } from 'k6/metrics';
import { randomIntBetween } from 'https://jslib.k6.io/k6-utils/1.2.0/index.js';

// Custom metrics
const restaurantListErrors = new Counter('restaurant_list_errors');
const restaurantListRequestDuration = new Trend('restaurant_list_duration');
const cacheMissRate = new Rate('cache_miss_rate');

// Configure the test
export const options = {
  stages: [
    { duration: '10s', target: 10 },   
    { duration: '20s', target: 50 },  
    { duration: '30s', target: 50 },   
    { duration: '10s', target: 0 },    
  ],
  thresholds: {
    'http_req_duration': ['p(95)<500'], 
    'restaurant_list_errors': ['count<10'], 
    'restaurant_list_duration': ['p(95)<300'],
    'cache_miss_rate': ['rate<0.5'], 
  },
};

const BASE_URL = __ENV.BASE_URL || 'http://localhost:8081';
console.log(`Using BASE_URL: ${BASE_URL}`);


export default function() {
  getRestaurants();
  
  getWeatherCacheStats();
  
  const restaurantId = randomIntBetween(1, 6);
  createReservation(restaurantId);
  
  sleep(randomIntBetween(1, 3));
}

function getRestaurants() {
  const start = new Date();
  
  const response = http.get(`${BASE_URL}/api/v1/restaurants`, {
    headers: { 'Accept': 'application/json' },
  });
  
  restaurantListRequestDuration.add(new Date() - start);
  
  const success = check(response, {
    'restaurants list status is 200': (r) => r.status === 200,
    'restaurants list has items': (r) => {
      try {
        const body = JSON.parse(r.body);
        return Array.isArray(body) && body.length > 0;
      } catch (e) {
        console.error(`Error parsing response: ${e.message}`);
        return false;
      }
    },
  });
  
  if (!success) {
    restaurantListErrors.add(1);
    console.error(`Restaurant list request failed: ${response.status} - ${response.body}`);
  }
  
  return response;
}

// Get weather cache statistics
function getWeatherCacheStats() {
  const response = http.get(`${BASE_URL}/api/v1/metrics/weather-cache`, {
    headers: { 'Accept': 'application/json' },
  });
  
  // Check if response is as expected
  check(response, {
    'weather cache stats status is 200': (r) => r.status === 200,
  });
  
  // Parse response body to get cache stats
  if (response.status === 200) {
    try {
      const stats = JSON.parse(response.body);
      const totalRequests = stats.hits + stats.misses;
      if (totalRequests > 0) {
        // Calculate cache miss rate
        const missRate = stats.misses / totalRequests;
        cacheMissRate.add(missRate);
      }
    } catch (e) {
      console.error(`Error parsing weather cache stats: ${e.message}`);
    }
  }
  
  return response;
}

// Create a reservation for a random meal
function createReservation(restaurantId) {
  // First, get available meals for the restaurant
  const mealsResponse = http.get(`${BASE_URL}/api/v1/restaurants/${restaurantId}/meals`, {
    headers: { 'Accept': 'application/json' },
  });
  
  // Check if response is as expected
  const mealsSuccess = check(mealsResponse, {
    'meals list status is 200': (r) => r.status === 200,
    'meals list has items': (r) => {
      try {
        const meals = JSON.parse(r.body);
        return Array.isArray(meals) && meals.length > 0;
      } catch (e) {
        console.error(`Error parsing meals: ${e.message}`);
        return false;
      }
    },
  });
  
  // If we have meals, create a reservation for the first one
  if (mealsSuccess) {
    try {
      const meals = JSON.parse(mealsResponse.body);
      if (meals.length > 0) {
        const mealId = meals[0].id;
        
        // Create reservation
        const reservationResponse = http.post(`${BASE_URL}/api/v1/reservations?mealId=${mealId}`, null, {
          headers: { 'Accept': 'application/json' },
        });
        
        // Check reservation response
        check(reservationResponse, {
          'reservation creation status is 200': (r) => r.status === 200,
          'reservation has token': (r) => {
            try {
              const resData = JSON.parse(r.body);
              return resData.token !== undefined && resData.token !== null;
            } catch (e) {
              console.error(`Error parsing reservation response: ${e.message}`);
              return false;
            }
          },
        });
        
        // Return the reservation response
        return reservationResponse;
      }
    } catch (e) {
      console.error(`Error processing meals response: ${e.message}`);
    }
  }
  
  return null;
} 