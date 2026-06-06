import type { RiskLevel, Severity } from '../types/aml';

export function severityLabel(value: Severity | RiskLevel): string {
  return value.charAt(0) + value.slice(1).toLowerCase();
}

export function severityRank(value: Severity | RiskLevel): number {
  const ranks: Record<Severity | RiskLevel, number> = {
    LOW: 1,
    MEDIUM: 2,
    HIGH: 3,
    CRITICAL: 4
  };
  return ranks[value];
}
