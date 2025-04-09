import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import RestaurantList from './components/RestaurantList';
import RestaurantDetail from './components/RestaurantDetail';
import ReservationDetail from './components/ReservationDetail';
import ReservationLookup from './components/ReservationLookup';
import Layout from './components/Layout';
import StaffDashboard from './components/StaffDashboard';
import StaffRestaurantDetail from './components/StaffRestaurantDetail';

function App() {
  return (
    <Router>
        <Routes>
          <Route path="/" element={<Layout />}>
            {/* User Routes */}
            <Route index element={<RestaurantList />} />
            <Route path="restaurants/:id" element={<RestaurantDetail />} />
            <Route path="reservations/:token" element={<ReservationDetail />} />
            <Route path="check-reservation" element={<ReservationLookup />} />
            
            {/* Staff Routes */}
            <Route path="staff" element={<StaffDashboard />} />
            <Route path="staff/restaurants/:id" element={<StaffRestaurantDetail />} />
          </Route>
        </Routes>
    </Router>
  );
}

export default App;
