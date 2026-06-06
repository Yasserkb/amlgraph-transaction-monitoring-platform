import { useMutation } from '@tanstack/react-query';
import { Button, Card, CardContent, Stack, TextField, Typography } from '@mui/material';
import { useState } from 'react';
import { explainCase } from '../api/amlApi';

export default function AiAssistantPage() {
  const [caseId, setCaseId] = useState('demo-case');
  const explain = useMutation({ mutationFn: explainCase });

  return (
    <Stack spacing={3}>
      <Typography variant="h4" fontWeight={800}>AI Investigation Assistant</Typography>
      <Card>
        <CardContent>
          <Stack spacing={2}>
            <TextField label="Case ID" value={caseId} onChange={event => setCaseId(event.target.value)} />
            <Button variant="contained" onClick={() => explain.mutate(caseId)} disabled={explain.isPending}>
              Explain Case
            </Button>
          </Stack>
        </CardContent>
      </Card>
      {explain.data && (
        <Card>
          <CardContent>
            <Stack spacing={2}>
              <Typography variant="h6">Summary</Typography>
              <Typography>{explain.data.summary}</Typography>
              <Typography variant="h6">Why the alert fired</Typography>
              <ul>{explain.data.why_alert_fired.map(item => <li key={item}>{item}</li>)}</ul>
              <Typography variant="h6">Risk indicators</Typography>
              <ul>{explain.data.risk_indicators.map(item => <li key={item}>{item}</li>)}</ul>
              <Typography variant="h6">Recommended actions</Typography>
              <ul>{explain.data.recommended_actions.map(item => <li key={item}>{item}</li>)}</ul>
            </Stack>
          </CardContent>
        </Card>
      )}
    </Stack>
  );
}
