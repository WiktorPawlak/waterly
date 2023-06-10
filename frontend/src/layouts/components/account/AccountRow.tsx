import { Box } from "@mui/system";
import { ListAccountDto } from "../../../api/accountApi";

interface Props {
  account: ListAccountDto;
}

export const AccountRow = ({ account }: Props) => {
  return (
    <Box sx={{ textTransform: "none" }}>
      <div>
        {account.firstName} {account.lastName}
      </div>

      <div>
        <strong>Login: </strong>
        {account.login}
      </div>
    </Box>
  );
};
