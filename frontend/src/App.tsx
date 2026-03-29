import React from 'react';
import { Routes, Route, Navigate } from 'react-router-dom';
import { useSelector } from 'react-redux';
import { RootState } from './store';
import Login from '@components/auth/Login';
import Register from '@components/auth/Register';
import ForgotPassword from '@components/auth/ForgotPassword';
import PrivateRoute from '@components/common/PrivateRoute';
import Navbar from '@components/common/Navbar';
import Sidebar from '@components/common/Sidebar';
import LoadingSpinner from '@components/common/LoadingSpinner';
import AdminDashboard from '@components/dashboard/AdminDashboard';
import DoctorDashboard from '@components/dashboard/DoctorDashboard';
import PatientDashboard from '@components/dashboard/PatientDashboard';
import PatientList from '@components/patients/PatientList';
import PatientForm from '@components/patients/PatientForm';
import PatientDetails from '@components/patients/PatientDetails';
import DoctorList from '@components/doctors/DoctorList';
import DoctorForm from '@components/doctors/DoctorForm';
import DoctorDetails from '@components/doctors/DoctorDetails';
import AppointmentCalendar from '@components/appointments/AppointmentCalendar';
import AppointmentForm from '@components/appointments/AppointmentForm';
import AppointmentList from '@components/appointments/AppointmentList';
import BillList from '@components/billing/BillList';
import GenerateBill from '@components/billing/GenerateBill';
import PaymentForm from '@components/billing/PaymentForm';
import ReportsDashboard from '@components/reports/ReportsDashboard';
import Profile from '@components/settings/Profile';
import ChangePassword from '@components/settings/ChangePassword';

function App() {
  const { isAuthenticated, isLoading, user } = useSelector((state: RootState) => state.auth);

  if (isLoading) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-gray-50">
        <LoadingSpinner size="large" />
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-50">
      {isAuthenticated && <Navbar />}
      
      <div className="flex">
        {isAuthenticated && <Sidebar />}
        
        <main className={`flex-1 ${isAuthenticated ? 'ml-64' : ''}`}>
          <div className="p-6">
            <Routes>
              {/* Public routes */}
              <Route path="/login" element={!isAuthenticated ? <Login /> : <Navigate to="/dashboard" />} />
              <Route path="/register" element={!isAuthenticated ? <Register /> : <Navigate to="/dashboard" />} />
              <Route path="/forgot-password" element={!isAuthenticated ? <ForgotPassword /> : <Navigate to="/dashboard" />} />
              
              {/* Protected routes */}
              <Route path="/" element={<Navigate to="/dashboard" />} />
              
              <Route path="/dashboard" element={
                <PrivateRoute>
                  {user?.role === 'ADMIN' ? <AdminDashboard /> : 
                   user?.role === 'DOCTOR' ? <DoctorDashboard /> : 
                   <PatientDashboard />}
                </PrivateRoute>
              } />
              
              {/* Patient routes */}
              <Route path="/patients" element={
                <PrivateRoute allowedRoles={['ADMIN', 'DOCTOR', 'RECEPTIONIST']}>
                  <PatientList />
                </PrivateRoute>
              } />
              <Route path="/patients/new" element={
                <PrivateRoute allowedRoles={['ADMIN', 'RECEPTIONIST']}>
                  <PatientForm />
                </PrivateRoute>
              } />
              <Route path="/patients/:id" element={
                <PrivateRoute allowedRoles={['ADMIN', 'DOCTOR', 'RECEPTIONIST']}>
                  <PatientDetails />
                </PrivateRoute>
              } />
              <Route path="/patients/:id/edit" element={
                <PrivateRoute allowedRoles={['ADMIN', 'RECEPTIONIST']}>
                  <PatientForm />
                </PrivateRoute>
              } />
              
              {/* Doctor routes */}
              <Route path="/doctors" element={
                <PrivateRoute allowedRoles={['ADMIN', 'RECEPTIONIST']}>
                  <DoctorList />
                </PrivateRoute>
              } />
              <Route path="/doctors/new" element={
                <PrivateRoute allowedRoles={['ADMIN']}>
                  <DoctorForm />
                </PrivateRoute>
              } />
              <Route path="/doctors/:id" element={
                <PrivateRoute allowedRoles={['ADMIN', 'DOCTOR', 'RECEPTIONIST']}>
                  <DoctorDetails />
                </PrivateRoute>
              } />
              <Route path="/doctors/:id/edit" element={
                <PrivateRoute allowedRoles={['ADMIN']}>
                  <DoctorForm />
                </PrivateRoute>
              } />
              
              {/* Appointment routes */}
              <Route path="/appointments" element={
                <PrivateRoute allowedRoles={['ADMIN', 'DOCTOR', 'RECEPTIONIST', 'PATIENT']}>
                  <AppointmentList />
                </PrivateRoute>
              } />
              <Route path="/appointments/calendar" element={
                <PrivateRoute allowedRoles={['ADMIN', 'DOCTOR', 'RECEPTIONIST']}>
                  <AppointmentCalendar />
                </PrivateRoute>
              } />
              <Route path="/appointments/new" element={
                <PrivateRoute allowedRoles={['ADMIN', 'RECEPTIONIST', 'PATIENT']}>
                  <AppointmentForm />
                </PrivateRoute>
              } />
              <Route path="/appointments/:id" element={
                <PrivateRoute allowedRoles={['ADMIN', 'DOCTOR', 'RECEPTIONIST', 'PATIENT']}>
                  <AppointmentList />
                </PrivateRoute>
              } />
              
              {/* Billing routes */}
              <Route path="/bills" element={
                <PrivateRoute allowedRoles={['ADMIN', 'RECEPTIONIST', 'PATIENT']}>
                  <BillList />
                </PrivateRoute>
              } />
              <Route path="/bills/generate" element={
                <PrivateRoute allowedRoles={['ADMIN', 'RECEPTIONIST']}>
                  <GenerateBill />
                </PrivateRoute>
              } />
              <Route path="/bills/:id/pay" element={
                <PrivateRoute allowedRoles={['ADMIN', 'RECEPTIONIST', 'PATIENT']}>
                  <PaymentForm />
                </PrivateRoute>
              } />
              
              {/* Reports routes */}
              <Route path="/reports" element={
                <PrivateRoute allowedRoles={['ADMIN']}>
                  <ReportsDashboard />
                </PrivateRoute>
              } />
              
              {/* Settings routes */}
              <Route path="/profile" element={
                <PrivateRoute>
                  <Profile />
                </PrivateRoute>
              } />
              <Route path="/change-password" element={
                <PrivateRoute>
                  <ChangePassword />
                </PrivateRoute>
              } />
              
              {/* Catch all route */}
              <Route path="*" element={<Navigate to="/dashboard" />} />
            </Routes>
          </div>
        </main>
      </div>
    </div>
  );
}

export default App;