import { registerApi } from "../api/authApi";
import { useEffect } from "react";
import { useNavigate } from "react-router-dom";
import jwt_decode from "jwt-decode";
import { useAccountState } from "../context/AccountContext";
import { PATHS } from "../routing/paths";
import { RolesEnum, roles } from "../types";

export interface AuthAccount {
  username: string;
  roles: string[];
  currentRole: string;
  prevRole: string;
}

enum StorageName {
  USER = "user",
  TOKEN = "jwtToken",
}

interface NewUser {
  login: string;
  password: string;
  email: string;
  firstName: string;
  lastName: string;
  phoneNumber: string;
  languageTag: string;
}

export const useAccount = () => {
  const { account, setAccount, isLoading, setIsLoading } = useAccountState();
  const navigate = useNavigate();

  const anonmousRole = roles.anonymous;
  const prevRoleTimeout = 500;

  const hasRole = (requiredRoles: string[]): boolean => {
    return requiredRoles.some((requiredRole) =>
      account?.roles.includes(requiredRole)
    );
  };

  const clearPrevRole = (updatedAccount: AuthAccount) => {
    if (account) {
      updatedAccount.prevRole = '';
      setAccount(updatedAccount);
      console.log(account);
    }
  }

  const setCurrentRole = (role: string) => {
    if (account) {
      const updatedAccount = {
        ...account,
        currentRole: role,
        prevRole: account.currentRole
      };
      localStorage.setItem(StorageName.USER, JSON.stringify(updatedAccount));
      setAccount(updatedAccount);
      setTimeout(() => clearPrevRole(updatedAccount), prevRoleTimeout);
    }
  };

  const getCurrentAccount = () => {
    setIsLoading(true);
    const user = localStorage.getItem(StorageName.USER);
    if (user) {
      setAccount(JSON.parse(user));
    }
    setIsLoading(false);
  };

  useEffect(() => {
    if (!account) {
      getCurrentAccount();
    }
  }, []);

  const addUserToStorage = async (token: string) => {
    const decodedToken: any = jwt_decode(token);
    if (decodedToken) {
      const roles = decodedToken.roles;

      const sortedRoles = roles.sort(
        (a: string, b: string) =>
          RolesEnum[a as keyof typeof RolesEnum] -
          RolesEnum[b as keyof typeof RolesEnum]
      );
      const username = decodedToken.jti;

      const currentRole = sortedRoles[0];
      const prevRole = anonmousRole;

      const userData = {
        username,
        roles,
        currentRole,
        prevRole
      };

      localStorage.setItem(StorageName.TOKEN, token);
      localStorage.setItem(StorageName.USER, JSON.stringify(userData));
      setAccount(userData);
      navigate(PATHS.EDIT_PROFILE);
      setTimeout(() => clearPrevRole(userData), prevRoleTimeout);
    }
  };

  const setAnonymousPrevRole = () => {
    const username = account?.username!;
    const roles = account?.roles!;
    const currentRole = account?.currentRole!;
    const prevRole = anonmousRole;
    const userData = {
      username,
      roles,
      currentRole,
      prevRole
    };
    setAccount(userData);
  }

  const logout = () => {
    setIsLoading(true);
    setAnonymousPrevRole();
    localStorage.removeItem(StorageName.TOKEN);
    localStorage.removeItem(StorageName.USER);
    navigate(PATHS.LOGIN)
    setTimeout(() => setAccount(null), prevRoleTimeout);
    setIsLoading(false);
  };

  const registerUser = async (
    userData: NewUser,
    recaptchaResponse: string | null
  ) => {
    try {
      const response = await registerApi(userData, recaptchaResponse);

      if (response.status === 201) {
        navigate(PATHS.WAIT_FOR_VERIFY, { state: response.data });
      }
    } catch (error) {
      return error;
    }
  };

  return {
    account,
    isLoading,
    addUserToStorage,
    logout,
    registerUser,
    hasRole,
    setCurrentRole,
  };
};
