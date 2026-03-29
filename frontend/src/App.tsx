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

/**
 * Main Application Component
 * 
 * This is the root component of the Hospital Management System frontend.
 * It handles routing, authentication state, and renders the appropriate
 * UI components based on the user's authentication status and role.
 * 
 * Key Features:
 * - Manages application routing using React Router
 * - Implements role-based access control (RBAC)
 * - Displays loading state during authentication checks
 * - Renders different layouts for authenticated vs non-authenticated users
 * - Provides navigation structure with Navbar and Sidebar
 * 
 * Routing Structure:
 * - Public routes: Login, Register, Forgot Password (accessible without authentication)
 * - Protected routes: All other routes (require authentication)
 * - Role-based routes: Different components based on user role (Admin, Doctor, Patient, Receptionist)
 * 
 * @component
 * @returns {JSX.Element} The main application component
 */
function App() {
  // Get authentication state from Redux store
  const { isAuthenticated, isLoading, user } = useSelector((state: RootState) => state.auth);

  // Show loading spinner while checking authentication status
  if (isLoading) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-gray-50">
        <LoadingSpinner size="large" />
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-50">
      {/* Show navigation bar only for authenticated users */}
      {isAuthenticated && <Navbar />}
      
      <div className="flex">
        {/* Show sidebar only for authenticated users */}
        {isAuthenticated && <Sidebar />}
        
        {/* Main content area with conditional margin based on sidebar presence */}
        <main className={`flex-1 ${isAuthenticated ? 'ml-64' : ''}`}>
          <div className="p-6">
            {/* Application Routes */}
            <Routes>
              {/* ========== PUBLIC ROUTES (No authentication required) ========== */}
              
              {/* Login page - redirects to dashboard if already authenticated */}
              <Route path="/login" element={!isAuthenticated ? <Login /> : <Navigate to="/dashboard" />} />
              
              {/* Registration page - redirects to dashboard if already authenticated */}
              <Route path="/register" element={!isAuthenticated ? <Register /> : <Navigate to="/dashboard" />} />
              
              {/* Password recovery page - redirects to dashboard if already authenticated */}
              <Route path="/forgot-password" element={!isAuthenticated ? <ForgotPassword /> : <Navigate to="/dashboard" />} />
              
              {/* ========== PROTECTED ROUTES (Authentication required) ========== */}
              
              {/* Root path redirects to dashboard */}
              <Route path="/" element={<Navigate to="/dashboard" />} />
              
              {/* Dashboard - shows different dashboard based on user role */}
              <Route path="/dashboard" element={
                <PrivateRoute>
                  {user?.role === 'ADMIN' ? <AdminDashboard /> : 
                   user?.role === 'DOCTOR' ? <DoctorDashboard /> : 
                   <PatientDashboard />}
                </PrivateRoute>
              } />
              
              {/* ========== PATIENT MANAGEMENT ROUTES ========== */}
              
              {/* Patient list - accessible by Admin, Doctor, and Receptionist */}
              <Route path="/patients" element={
                <PrivateRoute allowedRoles={['ADMIN', 'DOCTOR', 'RECEPTIONIST']}>
                  <PatientList />
                </PrivateRoute>
              } />
              
              {/* Create new patient - accessible by Admin and Receptionist */}
              <Route path="/patients/new" element={
                <PrivateRoute allowedRoles={['ADMIN', 'RECEPTIONIST']}>
                  <PatientForm />
                </PrivateRoute>
              } />
              
              {/* View patient details - accessible by Admin, Doctor, and Receptionist */}
              <Route path="/patients/:id" element={
                <PrivateRoute allowedRoles={['ADMIN', 'DOCTOR', 'RECEPTIONIST']}>
                  <PatientDetails />
                </PrivateRoute>
              } />
              
              {/* Edit patient - accessible by Admin and Receptionist */}
              <Route path="/patients/:id/edit" element={
                <PrivateRoute allowedRoles={['ADMIN', 'RECEPTIONIST']}>
                  <PatientForm />
                </PrivateRoute>
              } />
              
              {/* ========== DOCTOR MANAGEMENT ROUTES ========== */}
              
              {/* Doctor list - accessible by Admin and Receptionist */}
              <Route path="/doctors" element={
                <PrivateRoute allowedRoles={['ADMIN', 'RECEPTIONIST']}>
                  <DoctorList />
                </PrivateRoute>
              } />
              
              {/* Create new doctor - accessible only by Admin */}
              <Route path="/doctors/new" element={
                <PrivateRoute allowedRoles={['ADMIN']}>
                  <DoctorForm />
                </PrivateRoute>
              } />
              
              {/* View doctor details - accessible by Admin, Doctor, and Receptionist */}
              <Route path="/doctors/:id" element={
                <PrivateRoute allowedRoles={['ADMIN', 'DOCTOR', 'RECEPTIONIST']}>
                  <DoctorDetails />
                </PrivateRoute>
              } />
              
              {/* Edit doctor - accessible only by Admin */}
              <Route path="/doctors/:id/edit" element={
                <PrivateRoute allowedRoles={['ADMIN']}>
                  <DoctorForm />
                </PrivateRoute>
              } />
              
              {/* ========== APPOINTMENT MANAGEMENT ROUTES ========== */}
              
              {/* Appointment list - accessible by Admin, Doctor, Receptionist, and Patient */}
              <Route path="/appointments" element={
                <PrivateRoute allowedRoles={['ADMIN', 'DOCTOR', 'RECEPTIONIST', 'PATIENT']}>
                  <AppointmentList />
                </PrivateRoute>
              } />
              
              {/* Appointment calendar view - accessible by Admin, Doctor, and Receptionist */}
              <Route path="/appointments/calendar" element={
                <PrivateRoute allowedRoles={['ADMIN', 'DOCTOR', 'RECEPTIONIST']}>
                  <AppointmentCalendar />
                </PrivateRoute>
              } />
              
              {/* Create new appointment - accessible by Admin, Receptionist, and Patient */}
              <Route path="/appointments/new" element={
                <PrivateRoute allowedRoles={['ADMIN', 'RECEPTIONIST', 'PATIENT']}>
                  <AppointmentForm />
                </PrivateRoute>
              } />
              
              {/* View specific appointment - accessible by Admin, Doctor, Receptionist, and Patient */}
              <Route path="/appointments/:id" element={
                <PrivateRoute allowedRoles={['ADMIN', 'DOCTOR', 'RECEPTIONIST', 'PATIENT']}>
                  <AppointmentList />
                </PrivateRoute>
              } />
              
              {/* ========== BILLING MANAGEMENT ROUTES ========== */}
              
              {/* Bill list - accessible by Admin, Receptionist, and Patient */}
              <Route path="/bills" element={
                <PrivateRoute allowedRoles={['ADMIN', 'RECEPTIONIST', 'PATIENT']}>
                  <BillList />
                </PrivateRoute>
              } />
              
              {/* Generate new bill - accessible by Admin and Receptionist */}
              <Route path="/bills/generate" element={
                <PrivateRoute allowedRoles={['ADMIN', 'RECEPTIONIST']}>
                  <GenerateBill />
                </PrivateRoute>
              } />
              
              {/* Make payment for a bill - accessible by Admin, Receptionist, and Patient */}
              <Route path="/bills/:id/pay" element={
                <PrivateRoute allowedRoles={['ADMIN', 'RECEPTIONIST', 'PATIENT']}>
                  <PaymentForm />
                </PrivateRoute>
              } />
              
              {/* ========== REPORTS ROUTES ========== */}
              
              {/* Reports dashboard - accessible only by Admin */}
              <Route path="/reports" element={
                <PrivateRoute allowedRoles={['ADMIN']}>
                  <ReportsDashboard />
                </PrivateRoute>
              } />
              
              {/* ========== SETTINGS ROUTES ========== */}
              
              {/* User profile - accessible by all authenticated users */}
              <Route path="/profile" element={
                <PrivateRoute>
                  <Profile />
                </PrivateRoute>
              } />
              
              {/* Change password - accessible by all authenticated users */}
              <Route path="/change-password" element={
                <PrivateRoute>
                  <ChangePassword />
                </PrivateRoute>
              } />
              
              {/* ========== CATCH-ALL ROUTE ========== */}
              
              {/* Redirect any unknown routes to dashboard */}
              <Route path="*" element={<Navigate to="/dashboard" />} />
            </Routes>
          </div>
        </main>
      </div>
    </div>
  );
}

export default App;
