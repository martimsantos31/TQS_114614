import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { getRestaurants } from '../services/api';

const RestaurantList = () => {
  const [restaurants, setRestaurants] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchRestaurants = async () => {
      try {
        const response = await getRestaurants();
        setRestaurants(response.data);
        setLoading(false);
      } catch (err) {
        setError('Failed to fetch restaurants');
        setLoading(false);
        console.error(err);
      }
    };

    fetchRestaurants();
  }, []);

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

  return (
    <div className="container mx-auto p-4">
      <h1 className="text-2xl font-bold mb-6 text-center">Restaurants</h1>
      
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        {restaurants.map((restaurant) => (
          <div key={restaurant.id} className="card bg-base-100 shadow-xl">
            {restaurant.imageUrl && (
              <figure>
                <img src={restaurant.imageUrl} alt={restaurant.name} className="h-48 w-full object-cover" />
              </figure>
            )}
            <div className="card-body">
              <h2 className="card-title">{restaurant.name}</h2>
              <p>{restaurant.description || 'No description available'}</p>
              <div className="card-actions justify-end mt-4">
                <Link to={`/restaurants/${restaurant.id}`} className="btn btn-primary">
                  View Meals
                </Link>
              </div>
            </div>
          </div>
        ))}
      </div>
      
      {restaurants.length === 0 && (
        <div className="text-center mt-8">
          <p className="text-gray-500">No restaurants found</p>
        </div>
      )}
    </div>
  );
};

export default RestaurantList; 