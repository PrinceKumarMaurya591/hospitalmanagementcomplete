import React from 'react';
import { Link, useLocation } from 'react-router-dom';
import { useSelector } from 'react-redux';
import { RootState } from '@store';
import {
  LayoutDashboard,
  Users,
  UserCheck,
  Calendar,
  FileText,
  BarChart3,
  Settings,
  Stethoscope,
  User,
} from 'lucide-react';

const Sidebar: React.FC = () => {
  const location = useLocation();
  const { user } = useSelector((state: RootState) => state.auth);

  const isActive = (path: string) => location.pathname === path;

  const menuItems = [
    {
      name: 'Dashboard',
      path: '/dashboard',
      icon: LayoutDashboard,
      roles: ['ADMIN', 'DOCTOR', 'PATIENT', 'RECEPTIONIST'],
    },
    {
      name: 'Patients',
      path: '/patients',
      icon: Users,
      roles: ['ADMIN', 'DOCTOR', 'RECEPTIONIST'],
    },
    {
      name: 'Doctors',
      path: '/doctors',
      icon: Stethoscope,
      roles: ['ADMIN', 'RECEPTIONIST'],
    },
    {
      name: 'Appointments',
      path: '/appointments',
      icon: Calendar,
      roles: ['ADMIN', 'DOCTOR', 'RECEPTIONIST', 'PATIENT'],
    },
    {
      name: 'Billing',
      path: '/billing',
      icon: FileText,
      roles: ['ADMIN', 'RECEPTIONIST'],
    },
    {
      name: 'Reports',
      path: '/reports',
      icon: BarChart3,
      roles: ['ADMIN'],
    },
    {
      name: 'Profile',
      path: '/profile',
      icon: User,
      roles: ['ADMIN', 'DOCTOR', 'PATIENT', 'RECEPTIONIST'],
    },
    {
      name: 'Settings',
      path: '/settings',
      icon: Settings,
      roles: ['ADMIN', 'DOCTOR', 'PATIENT', 'RECEPTIONIST'],
    },
  ];

  const filteredMenuItems = menuItems.filter(item =>
    user?.roles?.some(role => item.roles.includes(role))
  );

  return (
    <div className="bg-white shadow-sm border-r border-gray-200 w-64 h-full">
      <div className="flex flex-col h-full">
        <div className="flex-1 flex flex-col pt-5 pb-4 overflow-y-auto">
          <nav className="mt-5 flex-1 px-2 space-y-1">
            {filteredMenuItems.map((item) => {
              const Icon = item.icon;
              return (
                <Link
                  key={item.name}
                  to={item.path}
                  className={`group flex items-center px-2 py-2 text-sm font-medium rounded-md transition-colors duration-150 ${
                    isActive(item.path)
                      ? 'bg-blue-50 text-blue-700 border-r-2 border-blue-700'
                      : 'text-gray-600 hover:bg-gray-50 hover:text-gray-900'
                  }`}
                >
                  <Icon
                    className={`mr-3 flex-shrink-0 h-5 w-5 ${
                      isActive(item.path) ? 'text-blue-500' : 'text-gray-400 group-hover:text-gray-500'
                    }`}
                  />
                  {item.name}
                </Link>
              );
            })}
          </nav>
        </div>
      </div>
    </div>
  );
};

export default Sidebar;