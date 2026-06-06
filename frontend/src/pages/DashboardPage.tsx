import { useQuery } from '@tanstack/react-query';
import { Grid, Card, CardContent, Typography, Stack } from '@mui/material';
import { Bar, BarChart, CartesianGrid, ResponsiveContainer, Tooltip, XAxis, YAxis } from 'recharts';
import { fetchAlerts, fetchCases, fetchCustomers, fetchTransactions } from '../api/amlApi';
import { ErrorBlock, LoadingBlock } from '../components/shared/StateBlock';

export default function DashboardPage() {
  const transactions = useQuery({ queryKey: ['transactions'], queryFn: fetchTransactions });
  const alerts = useQuery({ queryKey: ['alerts'], queryFn: fetchAlerts });
  const cases = useQuery({ queryKey: ['cases'], queryFn: fetchCases });
  const customers = useQuery({ queryKey: ['customers'], queryFn: fetchCustomers });

  if ([transactions, alerts, cases, customers].some(q => q.isLoading)) return <LoadingBlock />;
  const error = [transactions, alerts, cases, customers].find(q => q.isError)?.error;
  if (error) return <ErrorBlock message={(error as Error).message} />;

  const alertData = alerts.data?.data.reduce<Record<string, number>>((acc, alert) => {
    acc[alert.severity] = (acc[alert.severity] ?? 0) + 1;
    return acc;
  }, {});
  const chartData = Object.entries(alertData ?? {}).map(([severity, count]) => ({ severity, count }));

  return (
    <Stack spacing={3}>
      <Typography variant="h4" fontWeight={800}>Analyst Dashboard</Typography>
      <Grid container spacing={2}>
        <Kpi title="Transactions" value={transactions.data?.pagination.totalElements ?? 0} />
        <Kpi title="Alerts" value={alerts.data?.pagination.totalElements ?? 0} />
        <Kpi title="Cases" value={cases.data?.pagination.totalElements ?? 0} />
        <Kpi title="Customers" value={customers.data?.pagination.totalElements ?? 0} />
      </Grid>
      <Card>
        <CardContent>
          <Typography variant="h6" mb={2}>Alerts by Severity</Typography>
          <ResponsiveContainer width="100%" height={320}>
            <BarChart data={chartData}>
              <CartesianGrid strokeDasharray="3 3" />
              <XAxis dataKey="severity" />
              <YAxis allowDecimals={false} />
              <Tooltip />
              <Bar dataKey="count" />
            </BarChart>
          </ResponsiveContainer>
        </CardContent>
      </Card>
    </Stack>
  );
}

function Kpi({ title, value }: { title: string; value: number }) {
  return (
    <Grid item xs={12} md={3}>
      <Card>
        <CardContent>
          <Typography color="text.secondary">{title}</Typography>
          <Typography variant="h3" fontWeight={900}>{value}</Typography>
        </CardContent>
      </Card>
    </Grid>
  );
}
