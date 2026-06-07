import { useMutation } from '@tanstack/react-query';
import {
  Button,
  Card,
  CardContent,
  Chip,
  Stack,
  TextField,
  Typography,
} from '@mui/material';
import { useState } from 'react';
import { explainCase, type ExplainCaseRequest } from '../api/amlApi';

export default function AiAssistantPage() {
  const [caseId, setCaseId] = useState('case-001');
  const [amount, setAmount] = useState(25000);
  const [currency, setCurrency] = useState('EUR');
  const [originCountry, setOriginCountry] = useState('FR');
  const [destinationCountry, setDestinationCountry] = useState('AE');
  const [alertSeverity, setAlertSeverity] = useState('CRITICAL');
  const [customerRiskLevel, setCustomerRiskLevel] = useState('HIGH');
  const [ruleNames, setRuleNames] = useState('LARGE_AMOUNT,HIGH_RISK_COUNTRY');

  const explain = useMutation({
    mutationFn: explainCase,
  });

  const handleExplain = () => {
    const payload: ExplainCaseRequest = {
      caseId,
      amount,
      currency,
      originCountry,
      destinationCountry,
      alertSeverity,
      customerRiskLevel,
      ruleNames: ruleNames
        .split(',')
        .map(rule => rule.trim())
        .filter(Boolean),
    };

    explain.mutate(payload);
  };

  return (
    <Stack spacing={3}>
      <Typography variant="h4" fontWeight={800}>
        AI Investigation Assistant
      </Typography>

      <Card>
        <CardContent>
          <Stack spacing={2}>
            <TextField
              label="Case ID"
              value={caseId}
              onChange={event => setCaseId(event.target.value)}
            />

            <Stack direction={{ xs: 'column', md: 'row' }} spacing={2}>
              <TextField
                fullWidth
                label="Amount"
                type="number"
                value={amount}
                onChange={event => setAmount(Number(event.target.value))}
              />

              <TextField
                fullWidth
                label="Currency"
                value={currency}
                onChange={event => setCurrency(event.target.value)}
              />
            </Stack>

            <Stack direction={{ xs: 'column', md: 'row' }} spacing={2}>
              <TextField
                fullWidth
                label="Origin country"
                value={originCountry}
                onChange={event => setOriginCountry(event.target.value)}
              />

              <TextField
                fullWidth
                label="Destination country"
                value={destinationCountry}
                onChange={event => setDestinationCountry(event.target.value)}
              />
            </Stack>

            <Stack direction={{ xs: 'column', md: 'row' }} spacing={2}>
              <TextField
                fullWidth
                label="Alert severity"
                value={alertSeverity}
                onChange={event => setAlertSeverity(event.target.value)}
              />

              <TextField
                fullWidth
                label="Customer risk level"
                value={customerRiskLevel}
                onChange={event => setCustomerRiskLevel(event.target.value)}
              />
            </Stack>

            <TextField
              label="Triggered rules"
              helperText="Comma-separated rules"
              value={ruleNames}
              onChange={event => setRuleNames(event.target.value)}
            />

            <Button variant="contained" onClick={handleExplain} disabled={explain.isPending}>
              {explain.isPending ? 'Analyzing...' : 'Explain Case'}
            </Button>
          </Stack>
        </CardContent>
      </Card>

      {explain.data && (
        <Card>
          <CardContent>
            <Stack spacing={2}>
              {explain.data.provider && (
                <Stack direction="row" spacing={1} alignItems="center">
                  <Typography variant="h6">Provider</Typography>
                  <Chip label={explain.data.provider} variant="outlined" color="primary" />
                </Stack>
              )}

              <Typography variant="h6">Summary</Typography>
              <Typography>{explain.data.summary}</Typography>

              <Typography variant="h6">Why the alert fired</Typography>
              <ul>
                {explain.data.why_alert_fired.map(item => (
                  <li key={item}>{item}</li>
                ))}
              </ul>

              <Typography variant="h6">Risk indicators</Typography>
              <ul>
                {explain.data.risk_indicators.map(item => (
                  <li key={item}>{item}</li>
                ))}
              </ul>

              <Typography variant="h6">Recommended actions</Typography>
              <ul>
                {explain.data.recommended_actions.map(item => (
                  <li key={item}>{item}</li>
                ))}
              </ul>
            </Stack>
          </CardContent>
        </Card>
      )}
    </Stack>
  );
}