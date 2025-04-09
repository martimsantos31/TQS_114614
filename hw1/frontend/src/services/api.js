import axios from 'axios';

// Base URL for the API
const API_URL = 'http://localhost:8081';

// Create an axios instance
const api = axios.create({
  baseURL: API_URL,
  headers: {
    'Content-Type': 'application/json',
  },
  // Enable sending cookies and auth headers
  withCredentials: false 
});

// Restaurant-related API calls
export const getRestaurants = () => api.get('/api/v1/restaurants');
export const getRestaurantById = (id) => api.get(`/api/v1/restaurants/${id}`);

// Meal-related API calls
export const getMeals = (restaurantId, days = 7) => 
  api.get(`/api/v1/meals?restaurantId=${restaurantId}&days=${days}`);

// Reservation-related API calls
export const createReservation = (mealId) => 
  api.post(`/api/v1/reservations?mealId=${mealId}`);
export const getReservation = (token) => 
  api.get(`/api/v1/reservations/${token}`);
export const useReservation = (token) => 
  api.put(`/api/v1/reservations/${token}/use`);
export const cancelReservation = (token) => 
  api.delete(`/api/v1/reservations/${token}`);

export default api; 