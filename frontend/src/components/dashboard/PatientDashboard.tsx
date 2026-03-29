import React from 'react';
import { useSelector } from 'react-redux';
import { RootState } from '@store';
import {
  Calendar,
  FileText,
  CreditCard,
  User,
  Clock,
  CheckCircle,
} from 'lucide-react';

const PatientDashboard: React.FC = () => {
  const { user } = useSelector((state: RootState) => state.auth);

  const stats = [
    {
      name: 'Upcoming Appointments',
      value: '2',
      icon: Calendar,
      color: 'bg-blue-500',
    },
    {
      name: 'Medical Records',
      value: '12',
      icon: FileText,
      color: 'bg-green-500',
    },
    {
      name: 'Pending Bills',
      value: '1',
      icon: CreditCard,
      color: 'bg-yellow-500',
    },
    {
      name: 'Profile Completion',
      value: '85%',
      icon: User,
      color: 'bg-purple-500',
    },
  ];

  const upcomingAppointments = [
    {
      id: '1',
      date: '2024-01-15',
      time: '10:00 AM',
      doctor: 'Dr. Sarah Johnson',
      type: 'General Check-up',
      status: 'confirmed',
    },
    {
      id: '2',
      date: '2024-01-22',
      time: '2:30 PM',
      doctor: 'Dr. Michael Chen',
      type: 'Follow-up',
      status: 'confirmed',
    },
  ];

  const recentBills = [
    {
      id: '1',
      date: '2024-01-10',
      amount: '$150.00',
      status: 'paid',
      description: 'General Consultation',
    },
    {
      id: '2',
      date: '2024-01-05',
      amount: '$75.00',
      status: 'pending',
      description: 'Lab Tests',
    },
  ];

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <h1 className="text-2xl font-bold text-gray-900">
          Welcome back, {user?.username}!
        </h1>
        <div className="text-sm text-gray-500">
          Patient Dashboard
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

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {/* Upcoming Appointments */}
        <div className="bg-white rounded-lg shadow">
          <div className="px-6 py-4 border-b border-gray-200">
            <h3 className="text-lg font-medium text-gray-900">Upcoming Appointments</h3>
          </div>
          <div className="divide-y divide-gray-200">
            {upcomingAppointments.map((appointment) => (
              <div key={appointment.id} className="px-6 py-4">
                <div className="flex items-center justify-between">
                  <div>
                    <div className="text-sm font-medium text-gray-900">
                      {appointment.doctor}
                    </div>
                    <div className="text-sm text-gray-500">
                      {appointment.date} at {appointment.time}
                    </div>
                    <div className="text-xs text-gray-400">
                      {appointment.type}
                    </div>
                  </div>
                  <CheckCircle className="h-5 w-5 text-green-500" />
                </div>
              </div>
            ))}
          </div>
          <div className="px-6 py-3 bg-gray-50">
            <button className="text-sm text-blue-600 hover:text-blue-500 font-medium">
              View all appointments →
            </button>
          </div>
        </div>

        {/* Recent Bills */}
        <div className="bg-white rounded-lg shadow">
          <div className="px-6 py-4 border-b border-gray-200">
            <h3 className="text-lg font-medium text-gray-900">Recent Bills</h3>
          </div>
          <div className="divide-y divide-gray-200">
            {recentBills.map((bill) => (
              <div key={bill.id} className="px-6 py-4">
                <div className="flex items-center justify-between">
                  <div>
                    <div className="text-sm font-medium text-gray-900">
                      {bill.description}
                    </div>
                    <div className="text-sm text-gray-500">
                      {bill.date}
                    </div>
                  </div>
                  <div className="text-right">
                    <div className="text-sm font-medium text-gray-900">
                      {bill.amount}
                    </div>
                    <span className={`inline-flex px-2 py-1 text-xs font-semibold rounded-full ${
                      bill.status === 'paid'
                        ? 'bg-green-100 text-green-800'
                        : 'bg-yellow-100 text-yellow-800'
                    }`}>
                      {bill.status}
                    </span>
                  </div>
                </div>
              </div>
            ))}
          </div>
          <div className="px-6 py-3 bg-gray-50">
            <button className="text-sm text-blue-600 hover:text-blue-500 font-medium">
              View all bills →
            </button>
          </div>
        </div>
      </div>

      {/* Quick Actions */}
      <div className="bg-white rounded-lg shadow p-6">
        <h3 className="text-lg font-medium text-gray-900 mb-4">Quick Actions</h3>
        <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
          <button className="flex items-center justify-center px-4 py-2 border border-transparent text-sm font-medium rounded-md text-white bg-blue-600 hover:bg-blue-700">
            <Calendar className="h-4 w-4 mr-2" />
            Book Appointment
          </button>
          <button className="flex items-center justify-center px-4 py-2 border border-gray-300 text-sm font-medium rounded-md text-gray-700 bg-white hover:bg-gray-50">
            <FileText className="h-4 w-4 mr-2" />
            View Records
          </button>
          <button className="flex items-center justify-center px-4 py-2 border border-gray-300 text-sm font-medium rounded-md text-gray-700 bg-white hover:bg-gray-50">
            <CreditCard className="h-4 w-4 mr-2" />
            Pay Bills
          </button>
        </div>
      </div>
    </div>
  );
};

export default PatientDashboard;