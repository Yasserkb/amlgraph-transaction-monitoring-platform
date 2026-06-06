import { useQuery } from '@tanstack/react-query';
import { Card, CardContent, LinearProgress, Stack, Table, TableBody, TableCell, TableHead, TableRow, Typography } from '@mui/material';
import { fetchCustomers } from '../api/amlApi';
import SeverityChip from '../components/shared/SeverityChip';
import { ErrorBlock, LoadingBlock } from '../components/shared/StateBlock';

export default function CustomersPage() {
  const query = useQuery({ queryKey: ['customers'], queryFn: fetchCustomers });
  if (query.isLoading) return <LoadingBlock />;
  if (query.isError) return <ErrorBlock message={(query.error as Error).message} />;

  return (
    <Stack spacing={3}>
      <Typography variant="h4" fontWeight={800}>Customer Risk</Typography>
      <Card>
        <CardContent>
          <Table>
            <TableHead>
              <TableRow>
                <TableCell>Name</TableCell>
                <TableCell>Country</TableCell>
                <TableCell>Risk</TableCell>
                <TableCell>Score</TableCell>
                <TableCell>KYC</TableCell>
                <TableCell>PEP</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {(query.data?.data ?? []).map(customer => (
                <TableRow key={customer.id} hover>
                  <TableCell>{customer.fullName}</TableCell>
                  <TableCell>{customer.countryOfResidence}</TableCell>
                  <TableCell><SeverityChip value={customer.riskLevel} /></TableCell>
                  <TableCell sx={{ minWidth: 180 }}>
                    <Stack spacing={1}>
                      <Typography variant="body2">{customer.riskScore}/100</Typography>
                      <LinearProgress variant="determinate" value={customer.riskScore} />
                    </Stack>
                  </TableCell>
                  <TableCell>{customer.kycStatus}</TableCell>
                  <TableCell>{customer.pep ? 'Yes' : 'No'}</TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        </CardContent>
      </Card>
    </Stack>
  );
}
