import { Outlet } from "react-router-dom";
import { useState, createContext } from "react";
import NavBar from "./NavBar";

// Create context for staff mode
export const StaffModeContext = createContext();

export default function Layout() {
    const [isStaffMode, setIsStaffMode] = useState(false);
    
    return (
        <StaffModeContext.Provider value={{ isStaffMode, setIsStaffMode }}>
            <div className="min-h-screen bg-base-200 p-4">
                <NavBar />
                <Outlet />
            </div>
        </StaffModeContext.Provider>
    )
}