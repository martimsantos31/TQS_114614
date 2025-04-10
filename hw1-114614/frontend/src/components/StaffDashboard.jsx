import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { getRestaurants } from '../services/api';

export default function StaffDashboard() {
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
        console.error("Error loading restaurants:", err);
        // Fallback data for testing
        const fallbackData = [
          { id: 1, name: "Tasca do Manel", description: "No description available" },
          { id: 2, name: "Marisqueira Atlântico", description: "No description available" },
          { id: 3, name: "Pizzaria Bella Italia", description: "No description available" }
        ];
        setRestaurants(fallbackData);
        setLoading(false);
      }
    };

    fetchRestaurants();
  }, []);

  if (loading) return <div className="flex justify-center p-8"><span className="loading loading-spinner loading-lg"></span></div>;
  if (error) return <div className="alert alert-error shadow-lg m-4">{error}</div>;

  return (
    <div className="container mx-auto p-4">
      <h1 className="text-2xl font-bold mb-6 text-center">Restaurants</h1>
      
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        {restaurants.map(restaurant => (
          <div key={restaurant.id} className="card bg-base-100 shadow-xl">
            <div className="card-body">
              <h2 className="card-title">{restaurant.name}</h2>
              <p>{restaurant.description || "No description available"}</p>
              <div className="card-actions justify-end mt-4">
                <Link to={`/staff/restaurants/${restaurant.id}`} className="btn btn-primary">
                  View Reservations
                </Link>
              </div>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
} 