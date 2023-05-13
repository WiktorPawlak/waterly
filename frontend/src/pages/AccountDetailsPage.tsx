import { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import { AccountDto, getUserById } from '../api/accountApi';
import { MainLayout } from '../layouts/MainLayout';
import { Box, CircularProgress, TextField, Typography } from '@mui/material';

const AccountDetailsPage = () => {
    const { id } = useParams();
    const [account, setAccount] = useState<AccountDto>();

    useEffect(() => {
        getUserById(id)
            .then((response) => {
                console.log(response.status);
                if (response.data) {
                    setAccount(response.data!);
                } else {
                    console.error(response.error);
                }
            });
    }, []);

    if (!account) {
        return <CircularProgress />;
    }

    return (
        <MainLayout>
    <Box sx={{ p: 2 }}>
      <Typography variant="h6">Account Details</Typography>
      <TextField label="ID" value={account.id} fullWidth disabled sx={{ mb: 2 }} />
      <TextField label="Login" value={account.login} fullWidth disabled sx={{ mb: 2 }} />
      <TextField label="Email" value={account.email} fullWidth disabled sx={{ mb: 2 }} />
      <TextField label="First Name" value={account.firstName} fullWidth disabled sx={{ mb: 2 }} />
      <TextField label="Last Name" value={account.lastName} fullWidth disabled sx={{ mb: 2 }} />
      <TextField label="Phone Number" value={account.phoneNumber} fullWidth disabled sx={{ mb: 2 }} />
      <TextField label="Language Tag" value={account.languageTag} fullWidth disabled sx={{ mb: 2 }} />
      <TextField label="Roles" value={account.roles.join(', ')} fullWidth disabled sx={{ mb: 2 }} />
      <TextField label="Active" value={account.active ? 'Yes' : 'No'} fullWidth disabled sx={{ mb: 2 }} />
      <TextField label="Created On" value={account.createdOn} fullWidth disabled sx={{ mb: 2 }} />
      <TextField label="Created By" value={account.createdBy} fullWidth disabled sx={{ mb: 2 }} />
      <TextField label="Updated On" value={account.updatedOn} fullWidth disabled sx={{ mb: 2 }} />
      <TextField label="Updated By" value={account.updatedBy} fullWidth disabled sx={{ mb: 2 }} />
      <TextField label="Last Success Auth" value={account.lastSuccessAuth} fullWidth disabled sx={{ mb: 2 }} />
      <TextField label="Last Incorrect Auth" value={account.lastIncorrectAuth} fullWidth disabled sx={{ mb: 2 }} />
      <TextField label="Last IP Address" value={account.lastIpAddress} fullWidth disabled sx={{ mb: 2 }} />
      <TextField label="Incorrect Auth Count" value={account.incorrectAuthCount} fullWidth disabled sx={{ mb: 2 }} />
    </Box>
        </MainLayout>
    );
};

export default AccountDetailsPage;