import {
  LoginRequestBody,
  postLogin,
  LoginResponse,
  registerApi,
} from "../api/authApi";
import { useState } from "react";
import { useNavigate } from "react-router-dom";
import jwt_decode from "jwt-decode";

interface User {
  username: string;
  roles: string[];
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

export const useUser = () => {
  const [user, setUser] = useState<User | null>(null);
  const navigate = useNavigate();

  const logInClient = async (credentials: LoginRequestBody) => {
    if (credentials) {
      const response: LoginResponse<string> = await postLogin(credentials);

      if (response.data) {
        const token = response.data;
        const decodedToken: any = jwt_decode(token);
        if (decodedToken) {
          const roles = decodedToken.roles;
          const username = decodedToken.jti;

          const user = {
            username,
            roles,
          };
          setUser(user);

          localStorage.setItem("jwtToken", token);
          localStorage.setItem("user", JSON.stringify(user));
          navigate("/edit-profile");
          return true;
        }
      }
    }

    return false;
  };

  const logout = () => {
    localStorage.removeItem("jwtToken");
    localStorage.removeItem("user");
    setUser(null);
    navigate("/login");
  };

  const registerUser = async (userData: NewUser) => {
    try {
      const response = await registerApi(userData);

      if (response.status === 201) {
        navigate("/wait-for-verify");
      }
    } catch (error) {
      return error;
    }
  };

  return { user, logInClient, logout, registerUser };
};
