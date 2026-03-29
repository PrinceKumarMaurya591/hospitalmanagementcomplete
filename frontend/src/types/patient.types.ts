export enum BloodType {
  A_POSITIVE = 'A_POSITIVE',
  A_NEGATIVE = 'A_NEGATIVE',
  B_POSITIVE = 'B_POSITIVE',
  B_NEGATIVE = 'B_NEGATIVE',
  AB_POSITIVE = 'AB_POSITIVE',
  AB_NEGATIVE = 'AB_NEGATIVE',
  O_POSITIVE = 'O_POSITIVE',
  O_NEGATIVE = 'O_NEGATIVE'
}

export enum MaritalStatus {
  SINGLE = 'SINGLE',
  MARRIED = 'MARRIED',
  DIVORCED = 'DIVORCED',
  WIDOWED = 'WIDOWED',
  SEPARATED = 'SEPARATED'
}

export interface Patient {
  id: string;
  userId: string;
  patientId: string; // Hospital patient ID
  firstName: string;
  lastName: string;
  dateOfBirth: string;
  age: number;
  gender: 'MALE' | 'FEMALE' | 'OTHER';
  phoneNumber: string;
  email: string;
  address: string;
  city: string;
  state: string;
  country: string;
  postalCode: string;
  emergencyContactName: string;
  emergencyContactPhone: string;
  emergencyContactRelationship: string;
  bloodType?: BloodType;
  height?: number; // in cm
  weight?: number; // in kg
  allergies?: string[];
  chronicConditions?: string[];
  currentMedications?: string[];
  insuranceProvider?: string;
  insurancePolicyNumber?: string;
  insuranceGroupNumber?: string;
  maritalStatus?: MaritalStatus;
  occupation?: string;
  primaryCarePhysician?: string;
  preferredPharmacy?: string;
  notes?: string;
  status: 'ACTIVE' | 'INACTIVE' | 'DECEASED';
  registeredDate: string;
  lastVisitDate?: string;
  createdAt: string;
  updatedAt: string;
}

export interface MedicalHistory {
  id: string;
  patientId: string;
  condition: string;
  diagnosisDate: string;
  status: 'ACTIVE' | 'RESOLVED' | 'CHRONIC';
  severity: 'MILD' | 'MODERATE' | 'SEVERE';
  description?: string;
  treatment?: string;
  notes?: string;
  createdAt: string;
  updatedAt: string;
}

export interface VitalSign {
  id: string;
  patientId: string;
  recordedBy: string;
  temperature: number; // Celsius
  heartRate: number; // BPM
  bloodPressureSystolic: number;
  bloodPressureDiastolic: number;
  respiratoryRate: number; // breaths per minute
  oxygenSaturation: number; // percentage
  height?: number; // cm
  weight?: number; // kg
  bmi?: number;
  painLevel?: number; // 0-10 scale
  notes?: string;
  recordedAt: string;
  createdAt: string;
}

export interface PatientRequest {
  firstName: string;
  lastName: string;
  dateOfBirth: string;
  gender: 'MALE' | 'FEMALE' | 'OTHER';
  phoneNumber: string;
  email: string;
  address: string;
  city: string;
  state: string;
  country: string;
  postalCode: string;
  emergencyContactName: string;
  emergencyContactPhone: string;
  emergencyContactRelationship: string;
  bloodType?: BloodType;
  height?: number;
  weight?: number;
  allergies?: string[];
  chronicConditions?: string[];
  currentMedications?: string[];
  insuranceProvider?: string;
  insurancePolicyNumber?: string;
  insuranceGroupNumber?: string;
  maritalStatus?: MaritalStatus;
  occupation?: string;
  primaryCarePhysician?: string;
  preferredPharmacy?: string;
  notes?: string;
}

export interface PatientResponse {
  patient: Patient;
  medicalHistory: MedicalHistory[];
  vitalSigns: VitalSign[];
}

export interface PatientState {
  patients: Patient[];
  currentPatient: PatientResponse | null;
  isLoading: boolean;
  error: string | null;
  total: number;
  page: number;
  limit: number;
  search: string;
  filters: {
    status?: string;
    gender?: string;
    bloodType?: string;
  };
}