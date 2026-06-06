import { ReactNode } from 'react';
import { Link as RouterLink, useLocation } from 'react-router-dom';
import { AppBar, Box, Button, Container, Stack, Toolbar, Typography } from '@mui/material';

const nav = [
  ['Dashboard', '/dashboard'],
  ['Transactions', '/transactions'],
  ['Alerts', '/alerts'],
  ['Cases', '/cases'],
  ['Customers', '/customers'],
  ['AI Assistant', '/ai-assistant']
];

export default function AppShell({ children }: { children: ReactNode }) {
  const location = useLocation();
  return (
    <Box minHeight="100vh">
      <AppBar position="sticky" color="transparent" elevation={0} sx={{ borderBottom: '1px solid rgba(255,255,255,0.08)', backdropFilter: 'blur(10px)' }}>
        <Toolbar>
          <Typography variant="h6" sx={{ fontWeight: 800, mr: 4 }}>AMLGraph</Typography>
          <Stack direction="row" spacing={1} sx={{ flexGrow: 1 }}>
            {nav.map(([label, href]) => (
              <Button
                key={href}
                component={RouterLink}
                to={href}
                color={location.pathname === href ? 'primary' : 'inherit'}
                variant={location.pathname === href ? 'outlined' : 'text'}
              >
                {label}
              </Button>
            ))}
          </Stack>
        </Toolbar>
      </AppBar>
      <Container maxWidth="xl" sx={{ py: 4 }}>
        {children}
      </Container>
    </Box>
  );
}
