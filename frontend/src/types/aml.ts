export type Severity = 'LOW' | 'MEDIUM' | 'HIGH' | 'CRITICAL';
export type RiskLevel = 'LOW' | 'MEDIUM' | 'HIGH' | 'CRITICAL';
export type AlertStatus = 'OPEN' | 'UNDER_REVIEW' | 'ESCALATED' | 'CLOSED' | 'FALSE_POSITIVE';
export type CaseStatus = 'NEW' | 'IN_PROGRESS' | 'ESCALATED' | 'SUBMITTED' | 'CLOSED';

export interface Pagination {
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
}

export interface PagedResponse<T> {
  data: T[];
  pagination: Pagination;
}

export interface ApiResponse<T> {
  data: T;
}

export interface Transaction {
  id: string;
  customerId: string;
  sourceAccountId: string;
  destinationAccountId: string;
  amount: number;
  currency: string;
  transactionType: string;
  channel: string;
  status: string;
  originCountry?: string;
  destinationCountry?: string;
  reference?: string;
  executedAt: string;
  createdAt: string;
}

export interface Alert {
  id: string;
  transactionId: string;
  customerId: string;
  ruleId: string;
  severity: Severity;
  status: AlertStatus;
  description: string;
  triggeredAt: string;
  closedAt?: string;
}

export interface CaseNote {
  id: string;
  authorId: string;
  content: string;
  createdAt: string;
}

export interface InvestigationCase {
  id: string;
  alertId: string;
  assignedAnalystId?: string;
  status: CaseStatus;
  strRequired: boolean;
  openedAt: string;
  closedAt?: string;
  notes: CaseNote[];
}

export interface Customer {
  id: string;
  fullName: string;
  nationality: string;
  countryOfResidence: string;
  riskScore: number;
  riskLevel: RiskLevel;
  kycStatus: string;
  pep: boolean;
  sanctioned: boolean;
  createdAt: string;
  updatedAt: string;
}

export interface AiExplainResponse {
  summary: string;
  why_alert_fired: string[];
  risk_indicators: string[];
  recommended_actions: string[];
  provider?: string;
}
