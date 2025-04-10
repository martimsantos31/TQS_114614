import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { getRestaurantById, getMeals, createReservation } from '../services/api';

const RestaurantDetail = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const [restaurant, setRestaurant] = useState(null);
  const [meals, setMeals] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [reservationStatus, setReservationStatus] = useState(null);

  useEffect(() => {
    const fetchRestaurantAndMeals = async () => {
      try {
        const [restaurantResponse, mealsResponse] = await Promise.all([
          getRestaurantById(id),
          getMeals(id)
        ]);
        
        setRestaurant(restaurantResponse.data);
        setMeals(mealsResponse.data);
        setLoading(false);
      } catch (err) {
        setError('Failed to fetch restaurant details');
        setLoading(false);
        console.error(err);
      }
    };

    fetchRestaurantAndMeals();
  }, [id]);

  const handleReservation = async (mealId) => {
    try {
      setReservationStatus({ loading: true, mealId });
      const response = await createReservation(mealId);
      
      // Navigate to reservation details page with the token and isNewReservation flag
      navigate(`/reservations/${response.data.token}`, { 
        state: { isNewReservation: true } 
      });
    } catch (err) {
      setReservationStatus({ 
        error: 'Failed to create reservation', 
        mealId 
      });
      console.error(err);
    }
  };

  const formatDate = (dateString) => {
    const options = { weekday: 'long', year: 'numeric', month: 'long', day: 'numeric' };
    return new Date(dateString).toLocaleDateString(undefined, options);
  };

  if (loading) {
    return (
      <div className="flex justify-center items-center h-64">
        <span className="loading loading-spinner loading-lg text-primary"></span>
      </div>
    );
  }

  if (error) {
    return (
      <div className="alert alert-error shadow-lg mx-auto max-w-3xl mt-4">
        <div>
          <svg xmlns="http://www.w3.org/2000/svg" className="stroke-current flex-shrink-0 h-6 w-6" fill="none" viewBox="0 0 24 24">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M10 14l2-2m0 0l2-2m-2 2l-2-2m2 2l2 2m7-2a9 9 0 11-18 0 9 9 0 0118 0z" />
          </svg>
          <span>{error}</span>
        </div>
      </div>
    );
  }

  if (!restaurant) {
    return (
      <div className="alert alert-warning shadow-lg mx-auto max-w-3xl mt-4">
        <div>
          <svg xmlns="http://www.w3.org/2000/svg" className="stroke-current flex-shrink-0 h-6 w-6" fill="none" viewBox="0 0 24 24">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z" />
          </svg>
          <span>Restaurant not found</span>
        </div>
      </div>
    );
  }

  // Group meals by date
  const mealsByDate = meals.reduce((acc, meal) => {
    const date = meal.date;
    if (!acc[date]) {
      acc[date] = [];
    }
    acc[date].push(meal);
    return acc;
  }, {});

  // Sort dates
  const sortedDates = Object.keys(mealsByDate).sort();

  return (
    <div className="container mx-auto p-4">
      <div className="flex items-center mb-4">
        <button 
          className="btn btn-circle btn-ghost" 
          onClick={() => navigate(-1)}
        >
          ←
        </button>
        <h1 className="text-2xl font-bold ml-2">{restaurant.name}</h1>
      </div>

      {restaurant.description && (
        <p className="mb-6">{restaurant.description}</p>
      )}

      {sortedDates.length === 0 ? (
        <div className="alert alert-info shadow-lg">
          <div>
            <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" className="stroke-current flex-shrink-0 w-6 h-6">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"></path>
            </svg>
            <span>No meals available at this restaurant</span>
          </div>
        </div>
      ) : (
        sortedDates.map(date => (
          <div key={date} className="mb-8">
            <h2 className="text-xl font-semibold mb-4">{formatDate(date)}</h2>
            
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
              {mealsByDate[date].map(meal => (
                <div key={meal.id} className="card bg-base-100 shadow-xl">
                  <div className="card-body">
                    <h3 className="card-title">{meal.name}</h3>
                    <p>{meal.description || 'No description available'}</p>
                    
                    {meal.weather && (
                      <div className="flex items-center mt-2 p-2 bg-base-200 rounded-md">
                        <svg xmlns="http://www.w3.org/2000/svg" className="h-5 w-5 mr-1" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                          <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M3 15a4 4 0 004 4h9a5 5 0 10-.1-9.999 5.5 5.5 0 10-10.78 2.77A4 4 0 003 15z" />
                        </svg>
                        <div>
                          <span className="font-medium">{meal.weather.condition || meal.weather.description}</span>
                          <div className="flex items-center space-x-2 mt-1">
                            {(meal.weather.minTemperature !== undefined && meal.weather.maxTemperature !== undefined) ? (
                              <span>
                                <span className="text-blue-500">{Math.round(meal.weather.minTemperature)}°</span>
                                <span className="mx-1">-</span>
                                <span className="text-red-500">{Math.round(meal.weather.maxTemperature)}°</span>
                                <span className="text-xs ml-1">C</span>
                              </span>
                            ) : (
                              <span>{Math.round(meal.weather.temperature)}°C</span>
                            )}
                          </div>
                        </div>
                      </div>
                    )}
                    
                    <div className="flex justify-between items-center mt-4">
                    
                      
                      <button 
                        className={`btn btn-primary ${meal.availableQuantity <= 0 ? 'btn-disabled' : ''} ${
                          reservationStatus?.loading && reservationStatus.mealId === meal.id ? 'loading' : ''
                        }`} 
                        onClick={() => handleReservation(meal.id)}
                        disabled={meal.availableQuantity <= 0 || (reservationStatus?.loading && reservationStatus.mealId === meal.id)}
                      >
                        Reserve
                      </button>
                    </div>
                    
                    {reservationStatus?.error && reservationStatus.mealId === meal.id && (
                      <div className="text-error text-sm mt-2">{reservationStatus.error}</div>
                    )}
                  </div>
                </div>
              ))}
            </div>
          </div>
        ))
      )}
    </div>
  );
};

export default RestaurantDetail; 