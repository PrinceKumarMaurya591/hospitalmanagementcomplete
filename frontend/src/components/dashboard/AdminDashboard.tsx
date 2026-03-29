import React from 'react';
import { useSelector } from 'react-redux';
import { RootState } from '@store';
import {
  Users,
  Stethoscope,
  Calendar,
  DollarSign,
  TrendingUp,
  Activity,
} from 'lucide-react';

const AdminDashboard: React.FC = () => {
  const { user } = useSelector((state: RootState) => state.auth);

  const stats = [
    {
      name: 'Total Patients',
      value: '1,234',
      icon: Users,
      color: 'bg-blue-500',
    },
    {
      name: 'Active Doctors',
      value: '56',
      icon: Stethoscope,
      color: 'bg-green-500',
    },
    {
      name: 'Today\'s Appointments',
      value: '89',
      icon: Calendar,
      color: 'bg-purple-500',
    },
    {
      name: 'Monthly Revenue',
      value: '$45,231',
      icon: DollarSign,
      color: 'bg-yellow-500',
    },
  ];

  return (
    <div className="space-y-8">
      <div className="bg-gradient-to-r from-blue-600 to-indigo-600 rounded-xl p-6 text-white">
        <h1 className="text-3xl font-bold">
          Welcome back, {user?.username}!
        </h1>
        <p className="text-blue-100 mt-2">
          Administrator Dashboard
        </p>
      </div>

      {/* Stats Grid */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
        {stats.map((stat) => {
          const Icon = stat.icon;
          return (
            <div key={stat.name} className="bg-white rounded-xl shadow-lg hover:shadow-xl transition-shadow duration-200 p-6 border border-gray-100">
              <div className="flex items-center">
                <div className={`p-3 rounded-xl bg-gradient-to-r ${stat.color} to-opacity-80 shadow-md`}>
                  <Icon className="h-7 w-7 text-white" />
                </div>
                <div className="ml-4">
                  <p className="text-sm font-medium text-gray-600">{stat.name}</p>
                  <p className="text-3xl font-bold text-gray-900">{stat.value}</p>
                </div>
              </div>
            </div>
          );
        })}
      </div>

      {/* Recent Activity */}
      <div className="bg-white rounded-xl shadow-lg border border-gray-100">
        <div className="px-6 py-4 border-b border-gray-200">
          <h3 className="text-lg font-semibold text-gray-900">Recent Activity</h3>
        </div>
        <div className="p-6">
          <div className="space-y-4">
            <div className="flex items-center space-x-4 p-3 rounded-lg hover:bg-gray-50 transition-colors">
              <div className="flex-shrink-0">
                <div className="p-2 bg-green-100 rounded-full">
                  <Activity className="h-4 w-4 text-green-600" />
                </div>
              </div>
              <div className="flex-1 min-w-0">
                <p className="text-sm font-medium text-gray-900">
                  New patient registered: John Doe
                </p>
                <p className="text-xs text-gray-500">2 minutes ago</p>
              </div>
            </div>
            <div className="flex items-center space-x-4 p-3 rounded-lg hover:bg-gray-50 transition-colors">
              <div className="flex-shrink-0">
                <div className="p-2 bg-blue-100 rounded-full">
                  <Calendar className="h-4 w-4 text-blue-600" />
                </div>
              </div>
              <div className="flex-1 min-w-0">
                <p className="text-sm font-medium text-gray-900">
                  Appointment scheduled with Dr. Smith
                </p>
                <p className="text-xs text-gray-500">15 minutes ago</p>
              </div>
            </div>
            <div className="flex items-center space-x-4 p-3 rounded-lg hover:bg-gray-50 transition-colors">
              <div className="flex-shrink-0">
                <div className="p-2 bg-purple-100 rounded-full">
                  <TrendingUp className="h-4 w-4 text-purple-600" />
                </div>
              </div>
              <div className="flex-1 min-w-0">
                <p className="text-sm font-medium text-gray-900">
                  Monthly revenue target achieved
                </p>
                <p className="text-xs text-gray-500">1 hour ago</p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default AdminDashboard;