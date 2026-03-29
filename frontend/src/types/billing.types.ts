export enum BillStatus {
  GENERATED = 'GENERATED',
  PARTIALLY_PAID = 'PARTIALLY_PAID',
  PAID = 'PAID',
  OVERDUE = 'OVERDUE',
  CANCELLED = 'CANCELLED',
  REFUNDED = 'REFUNDED'
}

export enum PaymentMethod {
  CASH = 'CASH',
  CREDIT_CARD = 'CREDIT_CARD',
  DEBIT_CARD = 'DEBIT_CARD',
  NET_BANKING = 'NET_BANKING',
  UPI = 'UPI',
  INSURANCE = 'INSURANCE',
  CHEQUE = 'CHEQUE',
  BANK_TRANSFER = 'BANK_TRANSFER'
}

export enum PaymentStatus {
  PENDING = 'PENDING',
  PROCESSING = 'PROCESSING',
  COMPLETED = 'COMPLETED',
  FAILED = 'FAILED',
  REFUNDED = 'REFUNDED',
  CANCELLED = 'CANCELLED'
}

export interface Bill {
  id: string;
  billId: string; // Hospital bill ID
  appointmentId: string;
  patientId: string;
  doctorId: string;
  invoiceNumber: string;
  amount: number;
  tax: number;
  discount: number;
  totalAmount: number;
  status: BillStatus;
  dueDate: string;
  generatedDate: string;
  description?: string;
  items: BillItem[];
  insuranceCovered: boolean;
  insuranceAmount: number;
  patientPayable: number;
  paidAmount: number;
  remainingAmount: number;
  createdAt: string;
  updatedAt: string;
}

export interface BillItem {
  id: string;
  description: string;
  quantity: number;
  unitPrice: number;
  total: number;
  category: 'CONSULTATION' | 'MEDICATION' | 'TEST' | 'PROCEDURE' | 'OTHER';
  taxRate: number;
  discount: number;
}

export interface Payment {
  id: string;
  paymentId: string; // Hospital payment ID
  billId: string;
  patientId: string;
  amount: number;
  paymentMethod: PaymentMethod;
  transactionId?: string;
  paymentDate: string;
  status: PaymentStatus;
  remarks?: string;
  gatewayResponse?: string;
  createdAt: string;
  updatedAt: string;
}

export interface Invoice {
  id: string;
  invoiceNumber: string;
  billId: string;
  patientId: string;
  generatedDate: string;
  dueDate: string;
  totalAmount: number;
  status: BillStatus;
  pdfUrl?: string;
  createdAt: string;
  updatedAt: string;
}

export interface Insurance {
  id: string;
  patientId: string;
  provider: string;
  policyNumber: string;
  groupNumber?: string;
  coverageType: string;
  coverageAmount: number;
  deductible: number;
  coPayment: number;
  effectiveDate: string;
  expirationDate: string;
  isActive: boolean;
  notes?: string;
  createdAt: string;
  updatedAt: string;
}

export interface BillRequest {
  appointmentId: string;
  patientId: string;
  doctorId: string;
  amount: number;
  tax?: number;
  discount?: number;
  description?: string;
  items: {
    description: string;
    quantity: number;
    unitPrice: number;
    category: 'CONSULTATION' | 'MEDICATION' | 'TEST' | 'PROCEDURE' | 'OTHER';
    taxRate?: number;
    discount?: number;
  }[];
  insuranceCovered?: boolean;
  insuranceAmount?: number;
  dueDate?: string;
}

export interface PaymentRequest {
  billId: string;
  patientId: string;
  amount: number;
  paymentMethod: PaymentMethod;
  transactionId?: string;
  remarks?: string;
}

export interface BillingState {
  bills: Bill[];
  payments: Payment[];
  currentBill: Bill | null;
  currentPayment: Payment | null;
  invoices: Invoice[];
  insurances: Insurance[];
  isLoading: boolean;
  error: string | null;
  totalBills: number;
  totalPayments: number;
  page: number;
  limit: number;
  filters: {
    status?: string;
    patientId?: string;
    doctorId?: string;
    dateFrom?: string;
    dateTo?: string;
  };
  statistics: {
    totalRevenue: number;
    pendingAmount: number;
    todayRevenue: number;
    monthlyRevenue: number;
    paymentMethods: Record<PaymentMethod, number>;
  };
}