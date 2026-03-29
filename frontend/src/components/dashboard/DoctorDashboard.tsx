import React from 'react';
import { useSelector } from 'react-redux';
import { RootState } from '@store';
import {
  Calendar,
  Users,
  Clock,
  CheckCircle,
  AlertCircle,
} from 'lucide-react';

const DoctorDashboard: React.FC = () => {
  const { user } = useSelector((state: RootState) => state.auth);

  const stats = [
    {
      name: 'Today\'s Appointments',
      value: '12',
      icon: Calendar,
      color: 'bg-blue-500',
    },
    {
      name: 'Total Patients',
      value: '156',
      icon: Users,
      color: 'bg-green-500',
    },
    {
      name: 'Pending Reviews',
      value: '8',
      icon: Clock,
      color: 'bg-yellow-500',
    },
    {
      name: 'Completed Today',
      value: '7',
      icon: CheckCircle,
      color: 'bg-purple-500',
    },
  ];

  const todaysAppointments = [
    {
      id: '1',
      time: '09:00',
      patient: 'John Doe',
      type: 'Consultation',
      status: 'confirmed',
    },
    {
      id: '2',
      time: '10:30',
      patient: 'Jane Smith',
      type: 'Follow-up',
      status: 'confirmed',
    },
    {
      id: '3',
      time: '11:00',
      patient: 'Bob Johnson',
      type: 'Check-up',
      status: 'pending',
    },
  ];

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <h1 className="text-2xl font-bold text-gray-900">
          Good morning, Dr. {user?.username}!
        </h1>
        <div className="text-sm text-gray-500">
          Doctor Dashboard
        </div>
      </div>

      {/* Stats Grid */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
        {stats.map((stat) => {
          const Icon = stat.icon;
          return (
            <div key={stat.name} className="bg-white rounded-lg shadow p-6">
              <div className="flex items-center">
                <div className={`p-2 rounded-md ${stat.color}`}>
                  <Icon className="h-6 w-6 text-white" />
                </div>
                <div className="ml-4">
                  <p className="text-sm font-medium text-gray-600">{stat.name}</p>
                  <p className="text-2xl font-semibold text-gray-900">{stat.value}</p>
                </div>
              </div>
            </div>
          );
        })}
      </div>

      {/* Today's Schedule */}
      <div className="bg-white rounded-lg shadow">
        <div className="px-6 py-4 border-b border-gray-200">
          <h3 className="text-lg font-medium text-gray-900">Today's Schedule</h3>
        </div>
        <div className="divide-y divide-gray-200">
          {todaysAppointments.map((appointment) => (
            <div key={appointment.id} className="px-6 py-4">
              <div className="flex items-center justify-between">
                <div className="flex items-center space-x-4">
                  <div className="text-sm font-medium text-gray-900">
                    {appointment.time}
                  </div>
                  <div>
                    <div className="text-sm font-medium text-gray-900">
                      {appointment.patient}
                    </div>
                    <div className="text-sm text-gray-500">
                      {appointment.type}
                    </div>
                  </div>
                </div>
                <div className="flex items-center space-x-2">
                  {appointment.status === 'confirmed' ? (
                    <CheckCircle className="h-5 w-5 text-green-500" />
                  ) : (
                    <AlertCircle className="h-5 w-5 text-yellow-500" />
                  )}
                  <span className={`inline-flex px-2 py-1 text-xs font-semibold rounded-full ${
                    appointment.status === 'confirmed'
                      ? 'bg-green-100 text-green-800'
                      : 'bg-yellow-100 text-yellow-800'
                  }`}>
                    {appointment.status}
                  </span>
                </div>
              </div>
            </div>
          ))}
        </div>
      </div>

      {/* Quick Actions */}
      <div className="bg-white rounded-lg shadow p-6">
        <h3 className="text-lg font-medium text-gray-900 mb-4">Quick Actions</h3>
        <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
          <button className="flex items-center justify-center px-4 py-2 border border-transparent text-sm font-medium rounded-md text-white bg-blue-600 hover:bg-blue-700">
            <Calendar className="h-4 w-4 mr-2" />
            Schedule Appointment
          </button>
          <button className="flex items-center justify-center px-4 py-2 border border-gray-300 text-sm font-medium rounded-md text-gray-700 bg-white hover:bg-gray-50">
            <Users className="h-4 w-4 mr-2" />
            View Patients
          </button>
          <button className="flex items-center justify-center px-4 py-2 border border-gray-300 text-sm font-medium rounded-md text-gray-700 bg-white hover:bg-gray-50">
            <Clock className="h-4 w-4 mr-2" />
            Update Availability
          </button>
        </div>
      </div>
    </div>
  );
};

export default DoctorDashboard;