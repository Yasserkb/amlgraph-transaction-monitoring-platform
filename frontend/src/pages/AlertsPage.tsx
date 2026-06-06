import { useQuery } from '@tanstack/react-query';
import { Card, CardContent, Stack, Table, TableBody, TableCell, TableHead, TableRow, Typography } from '@mui/material';
import { fetchAlerts } from '../api/amlApi';
import SeverityChip from '../components/shared/SeverityChip';
import { ErrorBlock, LoadingBlock } from '../components/shared/StateBlock';

export default function AlertsPage() {
  const query = useQuery({ queryKey: ['alerts'], queryFn: fetchAlerts });
  if (query.isLoading) return <LoadingBlock />;
  if (query.isError) return <ErrorBlock message={(query.error as Error).message} />;

  return (
    <Stack spacing={3}>
      <Typography variant="h4" fontWeight={800}>AML Alerts</Typography>
      <Card>
        <CardContent>
          <Table>
            <TableHead>
              <TableRow>
                <TableCell>Severity</TableCell>
                <TableCell>Status</TableCell>
                <TableCell>Rule</TableCell>
                <TableCell>Description</TableCell>
                <TableCell>Triggered</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {(query.data?.data ?? []).map(alert => (
                <TableRow key={alert.id} hover>
                  <TableCell><SeverityChip value={alert.severity} /></TableCell>
                  <TableCell>{alert.status}</TableCell>
                  <TableCell>{alert.ruleId}</TableCell>
                  <TableCell>{alert.description}</TableCell>
                  <TableCell>{new Date(alert.triggeredAt).toLocaleString()}</TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        </CardContent>
      </Card>
    </Stack>
  );
}
