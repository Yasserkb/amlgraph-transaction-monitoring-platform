import { api } from './client';
import type {
  AiExplainResponse,
  Alert,
  Customer,
  InvestigationCase,
  PagedResponse,
  Transaction,
} from '../types/aml';

const DEMO_SIZE = 500;

export type ExplainCaseRequest = {
  caseId: string;
  alertSeverity?: string;
  amount?: number;
  currency?: string;
  originCountry?: string;
  destinationCountry?: string;
  customerRiskLevel?: string;
  ruleNames?: string[];
};

export async function fetchTransactions(): Promise<PagedResponse<Transaction>> {
  const { data } = await api.get<PagedResponse<Transaction>>(`/transactions?size=${DEMO_SIZE}`);
  return data;
}

export async function fetchAlerts(): Promise<PagedResponse<Alert>> {
  const { data } = await api.get<PagedResponse<Alert>>(`/alerts?size=${DEMO_SIZE}`);
  return data;
}

export async function fetchCases(): Promise<PagedResponse<InvestigationCase>> {
  const { data } = await api.get<PagedResponse<InvestigationCase>>(`/cases?size=${DEMO_SIZE}`);
  return data;
}

export async function fetchCustomers(): Promise<PagedResponse<Customer>> {
  const { data } = await api.get<PagedResponse<Customer>>(`/customers?size=${DEMO_SIZE}`);
  return data;
}

export async function explainCase(input: string | ExplainCaseRequest): Promise<AiExplainResponse> {
  const payload: ExplainCaseRequest =
    typeof input === 'string'
      ? { caseId: input }
      : input;

  const { data } = await api.post<AiExplainResponse>('/ai/explain', payload);
  return data;
}