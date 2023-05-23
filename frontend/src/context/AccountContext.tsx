import { createContext, ReactNode, useContext, useState } from "react";
import { AuthAccount } from "../hooks/useAccount";

interface AccountState {
  account: AuthAccount | null;
  setAccount: (item: AuthAccount | null) => void;
  isLoading: boolean;
  setIsLoading: (value: boolean) => void;
}

const AccountStateContext = createContext<AccountState | null>(null);

export const AccountStateContextProvider = ({
  children,
}: {
  children: ReactNode;
}) => {
  const [account, setAccount] = useState<AuthAccount | null>(null);
  const [isLoading, setIsLoading] = useState(true);

  // useEffect(() => {
  //   if (account?.token) {
  //     localStorage.setItem(TOKEN, JSON.stringify(account.token));
  //   }
  // }, [account]);

  return (
    <AccountStateContext.Provider
      value={{
        account,
        setAccount,
        isLoading,
        setIsLoading,
      }}
    >
      {children}
    </AccountStateContext.Provider>
  );
};

export const useAccountState = () => {
  const accountState = useContext(AccountStateContext);

  if (!accountState) {
    throw new Error("You forgot about AccountStateContextProvider!");
  }

  return accountState;
};
