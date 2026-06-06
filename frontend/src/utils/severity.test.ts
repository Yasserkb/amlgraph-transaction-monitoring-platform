import { describe, expect, it } from 'vitest';
import { severityLabel, severityRank } from './severity';

describe('severity utilities', () => {
  it('formats severity labels', () => {
    expect(severityLabel('CRITICAL')).toBe('Critical');
  });

  it('orders severities by risk', () => {
    expect(severityRank('CRITICAL')).toBeGreaterThan(severityRank('LOW'));
  });
});
