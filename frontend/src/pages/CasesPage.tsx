import { useQuery } from '@tanstack/react-query';
import { Card, CardContent, Chip, Stack, Table, TableBody, TableCell, TableHead, TableRow, Typography } from '@mui/material';
import { fetchCases } from '../api/amlApi';
import { ErrorBlock, LoadingBlock } from '../components/shared/StateBlock';

export default function CasesPage() {
  const query = useQuery({ queryKey: ['cases'], queryFn: fetchCases });
  if (query.isLoading) return <LoadingBlock />;
  if (query.isError) return <ErrorBlock message={(query.error as Error).message} />;

  return (
    <Stack spacing={3}>
      <Typography variant="h4" fontWeight={800}>Investigation Cases</Typography>
      <Card>
        <CardContent>
          <Table>
            <TableHead>
              <TableRow>
                <TableCell>Case</TableCell>
                <TableCell>Status</TableCell>
                <TableCell>STR Required</TableCell>
                <TableCell>Opened</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {(query.data?.data ?? []).map(item => (
                <TableRow key={item.id} hover>
                  <TableCell>{item.id}</TableCell>
                  <TableCell>{item.status}</TableCell>
                  <TableCell>{item.strRequired ? <Chip color="error" label="Yes" /> : <Chip label="No" />}</TableCell>
                  <TableCell>{new Date(item.openedAt).toLocaleString()}</TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        </CardContent>
      </Card>
    </Stack>
  );
}
