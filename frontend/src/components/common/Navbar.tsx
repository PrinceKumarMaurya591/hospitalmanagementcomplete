import React from 'react';
import { useSelector } from 'react-redux';
import { Link, useNavigate } from 'react-router-dom';
import { RootState } from '@store';
import { logout } from '@store/slices/authSlice';
import { useDispatch } from 'react-redux';
import { Bell, User, LogOut, Settings, Hospital } from 'lucide-react';

const Navbar: React.FC = () => {
  const { user } = useSelector((state: RootState) => state.auth);
  const dispatch = useDispatch();
  const navigate = useNavigate();

  const handleLogout = () => {
    dispatch(logout());
    navigate('/login');
  };

  return (
    <nav className="bg-white shadow-sm border-b border-gray-200">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex justify-between h-16">
          <div className="flex items-center">
            <div className="flex-shrink-0 flex items-center">
              <Hospital className="h-8 w-8 text-blue-600" />
              <span className="ml-2 text-xl font-bold text-gray-900">
                Hospital MS
              </span>
            </div>
          </div>

          <div className="flex items-center space-x-4">
            {/* Notifications */}
            <button className="p-2 rounded-md text-gray-400 hover:text-gray-500 hover:bg-gray-100">
              <Bell className="h-5 w-5" />
            </button>

            {/* User menu */}
            <div className="relative">
              <div className="flex items-center space-x-3">
                <div className="flex-shrink-0">
                  <div className="h-8 w-8 rounded-full bg-blue-500 flex items-center justify-center">
                    <User className="h-4 w-4 text-white" />
                  </div>
                </div>
                <div className="hidden md:block">
                  <div className="text-sm font-medium text-gray-900">
                    {user?.username || 'User'}
                  </div>
                  <div className="text-xs text-gray-500">
                    {user?.roles?.join(', ') || 'Role'}
                  </div>
                </div>
              </div>
            </div>

            {/* Settings */}
            <Link
              to="/settings"
              className="p-2 rounded-md text-gray-400 hover:text-gray-500 hover:bg-gray-100"
            >
              <Settings className="h-5 w-5" />
            </Link>

            {/* Logout */}
            <button
              onClick={handleLogout}
              className="p-2 rounded-md text-gray-400 hover:text-red-500 hover:bg-red-50"
            >
              <LogOut className="h-5 w-5" />
            </button>
          </div>
        </div>
      </div>
    </nav>
  );
};

export default Navbar;