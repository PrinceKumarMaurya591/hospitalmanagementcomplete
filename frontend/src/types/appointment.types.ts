export enum AppointmentStatus {
  SCHEDULED = 'SCHEDULED',
  CONFIRMED = 'CONFIRMED',
  IN_PROGRESS = 'IN_PROGRESS',
  COMPLETED = 'COMPLETED',
  CANCELLED = 'CANCELLED',
  NO_SHOW = 'NO_SHOW',
  RESCHEDULED = 'RESCHEDULED'
}

export enum AppointmentType {
  CONSULTATION = 'CONSULTATION',
  FOLLOW_UP = 'FOLLOW_UP',
  EMERGENCY = 'EMERGENCY',
  ROUTINE_CHECKUP = 'ROUTINE_CHECKUP',
  VACCINATION = 'VACCINATION',
  LAB_TEST = 'LAB_TEST',
  SURGERY = 'SURGERY',
  PHYSICAL_THERAPY = 'PHYSICAL_THERAPY',
  DENTAL = 'DENTAL',
  EYE_CHECKUP = 'EYE_CHECKUP'
}

export enum PaymentStatus {
  PENDING = 'PENDING',
  PARTIALLY_PAID = 'PARTIALLY_PAID',
  PAID = 'PAID',
  INSURANCE_CLAIMED = 'INSURANCE_CLAIMED',
  REFUNDED = 'REFUNDED'
}

export interface Appointment {
  id: string;
  appointmentId: string; // Hospital appointment ID
  patientId: string;
  doctorId: string;
  patientName: string;
  doctorName: string;
  appointmentDate: string; // YYYY-MM-DD
  appointmentTime: string; // HH:mm
  endTime: string; // HH:mm
  duration: number; // in minutes
  type: AppointmentType;
  status: AppointmentStatus;
  reason: string;
  symptoms?: string;
  diagnosis?: string;
  prescription?: string;
  notes?: string;
  consultationFee: number;
  paymentStatus: PaymentStatus;
  paidAmount: number;
  remainingAmount: number;
  isFollowUpRequired: boolean;
  followUpDate?: string;
  createdBy: string;
  cancelledBy?: string;
  cancellationReason?: string;
  createdAt: string;
  updatedAt: string;
}

export interface TimeSlot {
  startTime: string; // HH:mm
  endTime: string; // HH:mm
  isAvailable: boolean;
  appointmentId?: string;
}

export interface AvailableSlot {
  date: string; // YYYY-MM-DD
  dayOfWeek: string;
  slots: TimeSlot[];
}

export interface AppointmentRequest {
  patientId: string;
  doctorId: string;
  appointmentDate: string;
  appointmentTime: string;
  duration: number;
  type: AppointmentType;
  reason: string;
  symptoms?: string;
  isFollowUpRequired?: boolean;
  followUpDate?: string;
}

export interface AppointmentResponse {
  appointment: Appointment;
  patientDetails: {
    id: string;
    name: string;
    age: number;
    gender: string;
    phoneNumber: string;
  };
  doctorDetails: {
    id: string;
    name: string;
    specialization: string;
    department: string;
  };
}

export interface AppointmentState {
  appointments: Appointment[];
  currentAppointment: AppointmentResponse | null;
  availableSlots: AvailableSlot[];
  isLoading: boolean;
  error: string | null;
  total: number;
  page: number;
  limit: number;
  filters: {
    status?: string;
    type?: string;
    doctorId?: string;
    patientId?: string;
    dateFrom?: string;
    dateTo?: string;
  };
  calendarView: {
    selectedDate: string;
    view: 'day' | 'week' | 'month';
  };
}