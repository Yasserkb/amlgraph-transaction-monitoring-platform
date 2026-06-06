import { Chip } from '@mui/material';
import type { RiskLevel, Severity } from '../../types/aml';
import { severityLabel, severityRank } from '../../utils/severity';

interface Props {
  value: Severity | RiskLevel;
}

export default function SeverityChip({ value }: Props) {
  const rank = severityRank(value);
  const color = rank >= 4 ? 'error' : rank === 3 ? 'warning' : rank === 2 ? 'info' : 'success';
  return <Chip size="small" color={color} label={severityLabel(value)} />;
}
