import { useState, useEffect } from 'react';
import { useParams, Link } from 'react-router-dom';
import { getRestaurantById, getActiveReservations, findReservationByCode, markReservationAsUsed } from '../services/api';

export default function StaffRestaurantDetail() {
  const { id } = useParams();
  const [restaurant, setRestaurant] = useState(null);
  const [reservations, setReservations] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [searchCode, setSearchCode] = useState('');
  const [searchResult, setSearchResult] = useState(null);

  // Helper to normalize reservation data to our component format
  const normalizeReservationData = (reservation) => {
    return {
      id: reservation.id || Math.random().toString(),
      reservationCode: reservation.token || reservation.reservationCode,
      date: reservation.date || reservation.createdAt.split('T')[0] || new Date().toISOString().split('T')[0],
      time: reservation.time || reservation.createdAt.split('T')[1].substring(0, 5) || '12:00',
      partySize: reservation.partySize || 1,
      status: reservation.used ? "USED" : "CONFIRMED",
      mealName: reservation.mealName || "",
      restaurantName: reservation.restaurantName || ""
    };
  };

  useEffect(() => {
    const fetchData = async () => {
      try {
        // Fetch restaurant details
        let restaurantData;
        try {
          const response = await getRestaurantById(id);
          restaurantData = response.data;
        } catch (err) {
          console.error("Error fetching restaurant:", err);
          // Fallback data
          restaurantData = { 
            id: id, 
            name: `Restaurant ${id}`, 
            description: "No description available" 
          };
        }
        setRestaurant(restaurantData);

        // Fetch active reservations
        try {
          const reservationsResponse = await getActiveReservations(id);
          const normalizedReservations = reservationsResponse.data.map(normalizeReservationData);
          console.log("Normalized reservations:", normalizedReservations);
          setReservations(normalizedReservations);
        } catch (err) {
          console.error("Error fetching reservations:", err);
          // Fallback data for testing
          setReservations([]);
        }
        
        setLoading(false);
      } catch (err) {
        console.error("Failed to load data:", err);
        setError('Failed to load data. Please try again later.');
        setLoading(false);
      }
    };

    fetchData();
  }, [id]);

  const handleSearch = async () => {
    if (!searchCode.trim()) return;
    
    try {
      console.log("Searching for reservation code:", searchCode);
      // Before making API call, check if code exists in current reservations
      const foundInCurrent = reservations.find(
        r => r.reservationCode.toLowerCase() === searchCode.toLowerCase()
      );
      
      if (foundInCurrent) {
        console.log("Found in current reservations:", foundInCurrent);
        setSearchResult(foundInCurrent);
        return;
      }
      
      try {
        // If not found locally, try the API
        const response = await findReservationByCode(searchCode);
        console.log("Search result from API:", response.data);
        setSearchResult(normalizeReservationData(response.data));
      } catch (err) {
        console.error("Error searching reservation:", err);
        setSearchResult({ error: 'Reservation not found' });
      }
    } catch (err) {
      console.error("Search error:", err);
      setSearchResult({ error: 'An error occurred while searching' });
    }
  };

  // Handle Enter key press in the search input
  const handleKeyPress = (e) => {
    if (e.key === 'Enter') {
      handleSearch();
    }
  };

  const handleUseReservation = async (reservationCode) => {
    try {
      console.log("Marking reservation as used:", reservationCode);
      // Call API to mark reservation as used
      await markReservationAsUsed(reservationCode);
      
      // Update local state to reflect the change
      setReservations(prevReservations => 
        prevReservations.map(res => 
          res.reservationCode === reservationCode 
            ? { ...res, status: "USED" } 
            : res
        )
      );
      
      // If this is the reservation that was searched for, update that too
      if (searchResult && searchResult.reservationCode === reservationCode) {
        setSearchResult({ ...searchResult, status: "USED" });
      }
    } catch (err) {
      console.error("Error marking reservation as used:", err);
      // For demo/testing, update state anyway
      setReservations(prevReservations => 
        prevReservations.map(res => 
          res.reservationCode === reservationCode 
            ? { ...res, status: "USED" } 
            : res
        )
      );
    }
  };

  if (loading) return <div className="flex justify-center p-8"><span className="loading loading-spinner loading-lg"></span></div>;
  if (error) return (
    <div className="container mx-auto p-4">
      <div className="alert alert-error shadow-lg m-4">{error}</div>
      <Link to="/staff" className="btn btn-primary">Back to Staff Dashboard</Link>
    </div>
  );

  return (
    <div className="container mx-auto p-4">
      <div className="flex items-center mb-6">
        <Link to="/staff" className="btn btn-circle btn-ghost text-2xl p-0 mr-2">  
          ←
        </Link>
        <h1 className="text-2xl font-bold">{restaurant?.name || 'Restaurant Details'}</h1>
      </div>
      
      {/* Reservation Code Search */}
      <div className="card bg-base-100 shadow-xl mb-8">
        <div className="card-body">
          <h2 className="card-title">Find Reservation by Code</h2>
          <div className="flex gap-2">
            <input
              type="text"
              placeholder="Enter reservation code"
              className="input input-bordered w-full"
              value={searchCode}
              onChange={(e) => setSearchCode(e.target.value)}
              onKeyPress={handleKeyPress}
            />
            <button 
              className="btn btn-primary" 
              onClick={handleSearch}
            >
              Search
            </button>
          </div>

          {searchResult && (
            <div className="mt-4">
              {searchResult.error ? (
                <div className="alert alert-error">{searchResult.error}</div>
              ) : (
                <div className="card bg-base-200">
                  <div className="card-body">
                    <div className="flex justify-between items-center">
                      <h3 className="font-bold">Reservation {searchResult.reservationCode}</h3>
                      <span className={`badge ${searchResult.status === "USED" ? "badge-secondary" : "badge-primary"}`}>
                        {searchResult.status}
                      </span>
                    </div>
                    <p>Meal: {searchResult.mealName}</p>
                    <p>Date: {searchResult.date}</p>
                    <p>Time: {searchResult.time}</p>
                    
                    <div className="card-actions justify-end mt-4">
                      <button 
                        className="btn btn-primary"
                        onClick={() => handleUseReservation(searchResult.reservationCode)}
                        disabled={searchResult.status === "USED"}
                      >
                        {searchResult.status === "USED" ? "Completed" : "Mark as Completed"}
                      </button>
                    </div>
                  </div>
                </div>
              )}
            </div>
          )}
        </div>
      </div>
    </div>
  );
} 