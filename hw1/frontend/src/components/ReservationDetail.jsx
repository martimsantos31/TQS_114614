import { useState, useEffect } from 'react';
import { useParams, useNavigate, useLocation } from 'react-router-dom';
import { getReservation, useReservation, cancelReservation } from '../services/api';

const ReservationDetail = () => {
  const { token } = useParams();
  const navigate = useNavigate();
  const location = useLocation();
  const isNewReservation = location.state?.isNewReservation || false;
  const [reservation, setReservation] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [useStatus, setUseStatus] = useState(null);
  const [cancelStatus, setCancelStatus] = useState(null);
  const [showBanner, setShowBanner] = useState(isNewReservation);
  const [showCancelConfirm, setShowCancelConfirm] = useState(false);

  useEffect(() => {
    const fetchReservation = async () => {
      try {
        const response = await getReservation(token);
        setReservation(response.data);
        setLoading(false);
      } catch (err) {
        setError('Failed to fetch reservation details');
        setLoading(false);
        console.error(err);
      }
    };

    fetchReservation();
  }, [token]);

  const handleUseReservation = async () => {
    try {
      setUseStatus({ loading: true });
      const response = await useReservation(token);
      setReservation(response.data);
      setUseStatus({ success: true });
    } catch (err) {
      setUseStatus({ error: 'Failed to use reservation' });
      console.error(err);
    }
  };

  const handleCancelReservation = async () => {
    try {
      setCancelStatus({ loading: true });
      await cancelReservation(token);
      setCancelStatus({ 
        success: true,
        message: 'Your reservation has been successfully cancelled. Thank you for letting us know!'
      });
      
      // Wait a moment to show success message before redirecting
      setTimeout(() => {
        navigate('/', { replace: true });
      }, 3000); // Increased to 3 seconds to give more time to read the message
    } catch (err) {
      // Extract the specific error message from the response if available
      let errorMessage = 'Failed to cancel reservation';
      
      if (err.response) {
        // Use the server's error message if available
        if (err.response.data && typeof err.response.data === 'string') {
          errorMessage = err.response.data;
        } else if (err.response.status === 400) {
          errorMessage = 'Cannot cancel this reservation. It may have already been used or doesn\'t exist.';
        } else if (err.response.status === 404) {
          errorMessage = 'Reservation not found. Please check your reservation code.';
        }
      }
      
      setCancelStatus({ 
        error: errorMessage 
      });
      setShowCancelConfirm(false);
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

  if (!reservation) {
    return (
      <div className="alert alert-warning shadow-lg mx-auto max-w-3xl mt-4">
        <div>
          <svg xmlns="http://www.w3.org/2000/svg" className="stroke-current flex-shrink-0 h-6 w-6" fill="none" viewBox="0 0 24 24">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z" />
          </svg>
          <span>Reservation not found</span>
        </div>
      </div>
    );
  }

  return (
    <div className="container mx-auto p-4 max-w-lg">
      <div className="flex items-center mb-6">
        <button 
          className="btn btn-circle btn-ghost" 
          onClick={() => navigate(-1)}
        >
          ←
        </button>
        <h1 className="text-2xl font-bold ml-2">Reservation Details</h1>
      </div>

      {showBanner && (
        <div className="alert alert-success mb-4">
          <svg xmlns="http://www.w3.org/2000/svg" className="stroke-current shrink-0 h-6 w-6" fill="none" viewBox="0 0 24 24">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
          </svg>
          <div>
            <h3 className="font-bold">Reservation Created Successfully!</h3>
            <div className="text-sm">Please save your reservation code for future reference.</div>
          </div>
          <button className="btn btn-sm" onClick={() => setShowBanner(false)}>Dismiss</button>
        </div>
      )}

      {/* Cancel Confirmation Modal */}
      {showCancelConfirm && (
        <div className="fixed inset-0 bg-black bg-opacity-50 z-50 flex items-center justify-center">
          <div className="bg-base-100 p-6 rounded-lg shadow-xl max-w-md w-full">
            <h3 className="font-bold text-lg mb-2">Cancel Reservation</h3>
            <p className="mb-4">Are you sure you want to cancel your reservation for:</p>
            
            <div className="bg-base-200 p-3 rounded-md mb-4">
              <p className="font-semibold">{reservation.mealName}</p>
              <p className="text-sm">at {reservation.restaurantName}</p>
              <p className="text-sm">{formatDate(reservation.mealDate)}</p>
            </div>
            
            <div className="alert alert-warning mb-4">
              <svg xmlns="http://www.w3.org/2000/svg" className="stroke-current shrink-0 h-6 w-6" fill="none" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z" />
              </svg>
              <span>This action cannot be undone. Your reservation spot will be made available to other students.</span>
            </div>
            
            <div className="modal-action mt-4 flex justify-end">
              <button 
                className="btn btn-outline" 
                onClick={() => setShowCancelConfirm(false)}
              >
                Keep Reservation
              </button>
              <button 
                className={`btn btn-error ml-2 ${cancelStatus?.loading ? 'loading' : ''}`}
                onClick={handleCancelReservation}
                disabled={cancelStatus?.loading}
              >
                Yes, Cancel Reservation
              </button>
            </div>
          </div>
        </div>
      )}

      <div className="card bg-base-100 shadow-xl">
        <div className="card-body">
          <h2 className="card-title">{reservation.mealName}</h2>
          <p className="text-sm text-gray-600">at {reservation.restaurantName}</p>
          
          <div className="divider"></div>
          
          <div className="grid grid-cols-2 gap-4">
            <div>
              <p className="text-sm font-semibold">Date</p>
              <p>{formatDate(reservation.mealDate)}</p>
            </div>
            
            <div>
              <p className="text-sm font-semibold">Status</p>
              <div className={`badge ${reservation.used ? 'badge-success' : 'badge-primary'}`}>
                {reservation.used ? 'Used' : 'Valid'}
              </div>
            </div>
            
            <div className="col-span-2 bg-base-200 p-4 rounded-lg border-2 border-primary">
              <p className="text-sm font-semibold mb-2">Reservation Code</p>
              <div className="flex items-center justify-between">
                <code className="text-lg font-bold">{reservation.token}</code>
                <button 
                  className="btn btn-sm btn-outline"
                  onClick={() => {
                    navigator.clipboard.writeText(reservation.token);
                    alert('Reservation code copied to clipboard!');
                  }}
                >
                  Copy Code
                </button>
              </div>
            </div>
          </div>
          
          {useStatus?.success && (
            <div className="alert alert-success mt-4">
              <svg xmlns="http://www.w3.org/2000/svg" className="stroke-current shrink-0 h-6 w-6" fill="none" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
              </svg>
              <span>Reservation successfully used!</span>
            </div>
          )}
          
          {cancelStatus?.success && (
            <div className="alert alert-success mt-4">
              <svg xmlns="http://www.w3.org/2000/svg" className="stroke-current shrink-0 h-6 w-6" fill="none" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
              </svg>
              <div>
                <h3 className="font-bold">Reservation Cancelled</h3>
                <div className="text-sm">{cancelStatus.message || 'Reservation successfully cancelled! Redirecting...'}</div>
              </div>
            </div>
          )}
          
          {(useStatus?.error || cancelStatus?.error) && (
            <div className="alert alert-error mt-4">
              <svg xmlns="http://www.w3.org/2000/svg" className="stroke-current shrink-0 h-6 w-6" fill="none" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M10 14l2-2m0 0l2-2m-2 2l-2-2m2 2l2 2m7-2a9 9 0 11-18 0 9 9 0 0118 0z" />
              </svg>
              <div>
                <h3 className="font-bold">Error</h3>
                <div className="text-sm">{useStatus?.error || cancelStatus?.error}</div>
                {cancelStatus?.error && (
                  <div className="mt-2">
                    <p className="text-sm">If you're having trouble, please contact the cafeteria staff for assistance.</p>
                  </div>
                )}
              </div>
            </div>
          )}
          
          <div className="card-actions justify-between mt-4">
            {!reservation.used && (
              <button 
                className={`btn btn-error ${cancelStatus?.loading ? 'loading' : ''}`}
                onClick={() => setShowCancelConfirm(true)}
                disabled={reservation.used || cancelStatus?.loading || cancelStatus?.success}
              >
                Cancel Reservation
              </button>
            )}
            
            <button 
              className={`btn btn-primary ${reservation.used ? 'btn-disabled' : ''} ${useStatus?.loading ? 'loading' : ''}`}
              onClick={handleUseReservation}
              disabled={reservation.used || useStatus?.loading || cancelStatus?.success}
            >
              {reservation.used ? 'Already Used' : 'Use Reservation'}
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};

export default ReservationDetail; 