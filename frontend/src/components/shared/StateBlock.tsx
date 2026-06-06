import { Alert, CircularProgress, Stack } from '@mui/material';

export function LoadingBlock() {
  return <Stack alignItems="center" sx={{ py: 6 }}><CircularProgress /></Stack>;
}

export function ErrorBlock({ message }: { message: string }) {
  return <Alert severity="error">{message}</Alert>;
}
