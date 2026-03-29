import { configureStore } from '@reduxjs/toolkit';
import authReducer from './slices/authSlice';
// Import other reducers as they are created
// import patientReducer from './slices/patientSlice';
// import doctorReducer from './slices/doctorSlice';
// import appointmentReducer from './slices/appointmentSlice';
// import billingReducer from './slices/billingSlice';
// import uiReducer from './slices/uiSlice';

export const store = configureStore({
  reducer: {
    auth: authReducer,
    // patient: patientReducer,
    // doctor: doctorReducer,
    // appointment: appointmentReducer,
    // billing: billingReducer,
    // ui: uiReducer,
  },
  middleware: (getDefaultMiddleware) =>
    getDefaultMiddleware({
      serializableCheck: {
        ignoredActions: ['auth/login/fulfilled', 'auth/getCurrentUser/fulfilled'],
        ignoredPaths: ['auth.user', 'auth.token'],
      },
    }),
  devTools: process.env.NODE_ENV !== 'production',
});

export type RootState = ReturnType<typeof store.getState>;
export type AppDispatch = typeof store.dispatch;