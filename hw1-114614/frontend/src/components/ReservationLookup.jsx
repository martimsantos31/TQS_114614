import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { getReservation } from '../services/api';

const ReservationLookup = () => {
  const navigate = useNavigate();
  const [reservationCode, setReservationCode] = useState('');
  const [error, setError] = useState(null);
  const [isLoading, setIsLoading] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    if (!reservationCode.trim()) {
      setError('Please enter a reservation code');
      return;
    }
    
    setIsLoading(true);
    setError(null);
    
    try {
      await getReservation(reservationCode);
      // If successful, navigate to the reservation details page
      navigate(`/reservations/${reservationCode}`);
    } catch (err) {
      setError('Invalid reservation code. Please check and try again.');
      console.error(err);
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="container mx-auto p-4 max-w-md">
      <h1 className="text-2xl font-bold mb-6 text-center">Check Reservation</h1>
      
      <div className="card bg-base-100 shadow-xl">
        <div className="card-body">
          <p className="mb-4">Enter your reservation code to view details or check-in.</p>
          
          <form onSubmit={handleSubmit}>
            <div className="form-control">
              <label className="label">
                <span className="label-text">Reservation Code</span>
              </label>
              <input
                type="text"
                placeholder="Enter your code"
                className="input input-bordered w-full"
                value={reservationCode}
                onChange={(e) => setReservationCode(e.target.value)}
              />
            </div>
            
            {error && (
              <div className="alert alert-error mt-4">
                <svg xmlns="http://www.w3.org/2000/svg" className="stroke-current shrink-0 h-6 w-6" fill="none" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M10 14l2-2m0 0l2-2m-2 2l-2-2m2 2l2 2m7-2a9 9 0 11-18 0 9 9 0 0118 0z" />
                </svg>
                <span>{error}</span>
              </div>
            )}
            
            <div className="form-control mt-6">
              <button 
                type="submit" 
                className={`btn btn-primary ${isLoading ? 'loading' : ''}`}
                disabled={isLoading}
              >
                {isLoading ? 'Checking...' : 'Check Reservation'}
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  );
};

export default ReservationLookup; 