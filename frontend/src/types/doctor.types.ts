export enum DoctorStatus {
  ACTIVE = 'ACTIVE',
  ON_LEAVE = 'ON_LEAVE',
  INACTIVE = 'INACTIVE',
  RETIRED = 'RETIRED'
}

export enum Department {
  CARDIOLOGY = 'CARDIOLOGY',
  DERMATOLOGY = 'DERMATOLOGY',
  ENDOCRINOLOGY = 'ENDOCRINOLOGY',
  GASTROENTEROLOGY = 'GASTROENTEROLOGY',
  GENERAL_SURGERY = 'GENERAL_SURGERY',
  HEMATOLOGY = 'HEMATOLOGY',
  NEUROLOGY = 'NEUROLOGY',
  OBSTETRICS_GYNECOLOGY = 'OBSTETRICS_GYNECOLOGY',
  ONCOLOGY = 'ONCOLOGY',
  OPHTHALMOLOGY = 'OPHTHALMOLOGY',
  ORTHOPEDICS = 'ORTHOPEDICS',
  OTOLARYNGOLOGY = 'OTOLARYNGOLOGY',
  PEDIATRICS = 'PEDIATRICS',
  PSYCHIATRY = 'PSYCHIATRY',
  RADIOLOGY = 'RADIOLOGY',
  UROLOGY = 'UROLOGY',
  EMERGENCY_MEDICINE = 'EMERGENCY_MEDICINE',
  FAMILY_MEDICINE = 'FAMILY_MEDICINE',
  INTERNAL_MEDICINE = 'INTERNAL_MEDICINE',
  PATHOLOGY = 'PATHOLOGY',
  PHYSICAL_MEDICINE = 'PHYSICAL_MEDICINE',
  PULMONOLOGY = 'PULMONOLOGY',
  NEPHROLOGY = 'NEPHROLOGY',
  RHEUMATOLOGY = 'RHEUMATOLOGY'
}

export interface Doctor {
  id: string;
  userId: string;
  doctorId: string; // Hospital doctor ID
  firstName: string;
  lastName: string;
  email: string;
  phoneNumber: string;
  specialization: string;
  department: Department;
  licenseNumber: string;
  yearsOfExperience: number;
  qualification: string;
  bio?: string;
  consultationFee: number;
  followUpFee?: number;
  address?: string;
  city?: string;
  state?: string;
  country?: string;
  postalCode?: string;
  profileImage?: string;
  status: DoctorStatus;
  availableDays: string[]; // e.g., ['MONDAY', 'TUESDAY', 'WEDNESDAY']
  workingHours: {
    start: string; // HH:mm format
    end: string; // HH:mm format
  };
  maxPatientsPerDay: number;
  averageConsultationTime: number; // in minutes
  rating?: number;
  totalReviews?: number;
  createdAt: string;
  updatedAt: string;
}

export interface DoctorSchedule {
  id: string;
  doctorId: string;
  date: string; // YYYY-MM-DD
  dayOfWeek: string;
  startTime: string; // HH:mm
  endTime: string; // HH:mm
  isAvailable: boolean;
  maxAppointments: number;
  bookedAppointments: number;
  notes?: string;
  createdAt: string;
  updatedAt: string;
}

export interface DoctorLeave {
  id: string;
  doctorId: string;
  startDate: string;
  endDate: string;
  reason: string;
  status: 'PENDING' | 'APPROVED' | 'REJECTED';
  notes?: string;
  createdAt: string;
  updatedAt: string;
}

export interface DoctorRequest {
  firstName: string;
  lastName: string;
  email: string;
  phoneNumber: string;
  specialization: string;
  department: Department;
  licenseNumber: string;
  yearsOfExperience: number;
  qualification: string;
  bio?: string;
  consultationFee: number;
  followUpFee?: number;
  address?: string;
  city?: string;
  state?: string;
  country?: string;
  postalCode?: string;
  availableDays: string[];
  workingHours: {
    start: string;
    end: string;
  };
  maxPatientsPerDay: number;
  averageConsultationTime: number;
}

export interface DoctorResponse {
  doctor: Doctor;
  schedule: DoctorSchedule[];
  upcomingLeaves: DoctorLeave[];
}

export interface DoctorState {
  doctors: Doctor[];
  currentDoctor: DoctorResponse | null;
  isLoading: boolean;
  error: string | null;
  total: number;
  page: number;
  limit: number;
  search: string;
  filters: {
    department?: string;
    status?: string;
    specialization?: string;
  };
}