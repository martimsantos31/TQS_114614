import { BrowserRouter as Router, Routes, Route, Link } from 'react-router-dom';
import RestaurantList from './components/RestaurantList';
import RestaurantDetail from './components/RestaurantDetail';
import ReservationDetail from './components/ReservationDetail';
import ReservationLookup from './components/ReservationLookup';

function App() {
  return (
    <Router>
      <div className="min-h-screen bg-base-200">
        <div className="navbar bg-base-100 shadow-md mb-4">
          <div className="navbar-start">
            <Link to="/" className="btn btn-ghost text-xl">Campus Meals</Link>
          </div>
          <div className="navbar-end">
            <Link to="/check-reservation" className="btn btn-ghost">
              Check Reservation
            </Link>
          </div>
        </div>
        
        <Routes>
          <Route path="/" element={<RestaurantList />} />
          <Route path="/restaurants/:id" element={<RestaurantDetail />} />
          <Route path="/reservations/:token" element={<ReservationDetail />} />
          <Route path="/check-reservation" element={<ReservationLookup />} />
        </Routes>
      </div>
    </Router>
  );
}

export default App;
