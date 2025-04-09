import { Link, useNavigate } from "react-router-dom";
import { useContext } from "react";
import { StaffModeContext } from "./Layout";

export default function NavBar() {
    const { isStaffMode, setIsStaffMode } = useContext(StaffModeContext);
    const navigate = useNavigate();

    const handleModeChange = () => {
      const newMode = !isStaffMode;
      setIsStaffMode(newMode);
      
      // Navigate to the appropriate page based on mode
      if (newMode) {
        navigate('/staff');
      } else {
        navigate('/');
      }
    };

    return (
        <div className="navbar bg-base-100 shadow-md mb-4 rounded-xl mx-auto">
        <div className="navbar-start">
          <Link to={isStaffMode ? "/staff" : "/"} className="btn btn-ghost">
            <img src="/ad1.png" alt="Campus Meals" className="h-8" />
          </Link>
        </div>
        <div className="navbar-center">
          <div className="form-control">
            <label className="label cursor-pointer">
              <span className="label-text mr-2">User</span> 
              <input 
                type="checkbox" 
                className="toggle toggle-primary" 
                checked={isStaffMode}
                onChange={handleModeChange}
              />
              <span className="label-text ml-2">Staff</span> 
            </label>
          </div>
        </div>
        <div className="navbar-end">
          {isStaffMode ? (
            <Link to="/staff" className="btn btn-primary text-white">
              Staff Dashboard
            </Link>
          ) : (
            <Link to="/check-reservation" className="btn btn-primary text-white">
              Check Reservation
            </Link>
          )}
        </div>
      </div>
    )
}