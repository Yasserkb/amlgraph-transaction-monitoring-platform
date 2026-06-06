import { useQuery } from '@tanstack/react-query';
import { Card, CardContent, Stack, Typography } from '@mui/material';
import { DataGrid, GridColDef } from '@mui/x-data-grid';
import { fetchTransactions } from '../api/amlApi';
import { ErrorBlock, LoadingBlock } from '../components/shared/StateBlock';
import type { Transaction } from '../types/aml';

const columns: GridColDef<Transaction>[] = [
  { field: 'id', headerName: 'ID', flex: 1 },
  { field: 'amount', headerName: 'Amount', width: 130 },
  { field: 'currency', headerName: 'Currency', width: 110 },
  { field: 'channel', headerName: 'Channel', width: 110 },
  { field: 'status', headerName: 'Status', width: 130 },
  { field: 'originCountry', headerName: 'From', width: 90 },
  { field: 'destinationCountry', headerName: 'To', width: 90 }
];

export default function TransactionsPage() {
  const query = useQuery({ queryKey: ['transactions'], queryFn: fetchTransactions });
  if (query.isLoading) return <LoadingBlock />;
  if (query.isError) return <ErrorBlock message={(query.error as Error).message} />;

  return (
    <Stack spacing={3}>
      <Typography variant="h4" fontWeight={800}>Transactions</Typography>
      <Card>
        <CardContent sx={{ height: 620 }}>
          <DataGrid rows={query.data?.data ?? []} columns={columns} disableRowSelectionOnClick />
        </CardContent>
      </Card>
    </Stack>
  );
}
