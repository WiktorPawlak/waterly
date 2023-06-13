import {
  Box,
  CircularProgress,
  TextField,
  ToggleButton,
  ToggleButtonGroup,
  Typography,
} from "@mui/material";
import { AccountRow } from "./AccountRow";
import { useTranslation } from "react-i18next";
import { getOwnersAccounts, ListAccountDto } from "../../../api/accountApi";
import { useEffect, useState } from "react";
import { resolveApiError } from "../../../api/apiErrors";
import { enqueueSnackbar } from "notistack";

interface Props {
  ownerId: number | undefined;
  defaultOwnerId?: number;
  setOwnerId: (ownerId: number) => void;
}

export const OwnerAccountsSelect = ({
  setOwnerId,
  ownerId,
  defaultOwnerId,
}: Props) => {
  const [pattern, setPattern] = useState("");
  const [ownersAccounts, setOwnersAccounts] = useState<ListAccountDto[]>();
  const { t } = useTranslation();

  const fetchOwnerAccounts = async () => {
    const response = await getOwnersAccounts();

    if (response.status === 200) {
      setOwnersAccounts(response.data);
    } else {
      enqueueSnackbar(t(resolveApiError(response.error)), {
        variant: "error",
      });
    }
  };

  useEffect(() => {
    fetchOwnerAccounts();
  }, []);

  const filterAccount = (account: ListAccountDto) => {
    if (pattern === "") return true;

    const patternUpperCase = pattern.toLocaleUpperCase();
    const firstNameUpperCase = account.firstName.toLocaleUpperCase();
    const lastNameUpperCase = account.lastName.toLocaleUpperCase();

    return (
      account.login.toLocaleUpperCase().includes(patternUpperCase) ||
      firstNameUpperCase.includes(patternUpperCase) ||
      lastNameUpperCase.includes(patternUpperCase) ||
      `${firstNameUpperCase} ${lastNameUpperCase}`.includes(patternUpperCase) ||
      `${lastNameUpperCase} ${firstNameUpperCase}`.includes(patternUpperCase)
    );
  };

  const handleOwnerIdChange = (id: number) => {
    if (id) {
      setOwnerId(id);
    }
  };

  return (
    <>
      <Typography
        variant="h4"
        sx={{ fontSize: "16px", fontWeight: "700", mb: 2 }}
      >
        {t("apartmentPage.selectOwner")}
      </Typography>

      <TextField
        label={t("manageUsersPage.searchLabel")}
        onChange={(it) => setPattern(it.target.value)}
        sx={{
          color: "text.secondary",
          marginBottom: "15px",
          width: "100%",
        }}
      />
      {!ownersAccounts ? (
        <Box
          sx={{
            display: "flex",
            justifyContent: "center",
            alignItems: "center",
            textAlign: "center",
          }}
        >
          <CircularProgress />
        </Box>
      ) : (
        <ToggleButtonGroup
          color="primary"
          orientation="vertical"
          sx={{
            width: "100% !important",
          }}
          exclusive
          value={ownerId ?? defaultOwnerId}
          onChange={(_, value) => {
            handleOwnerIdChange(value);
          }}
          aria-label="Platform"
        >
          {ownersAccounts
            ?.filter((it) => filterAccount(it))
            .map((it) => (
              <ToggleButton value={it.id} key={it.id}>
                <AccountRow account={it} />
              </ToggleButton>
            ))}
        </ToggleButtonGroup>
      )}
    </>
  );
};
