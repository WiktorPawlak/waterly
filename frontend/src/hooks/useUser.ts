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
      return response;
    }
    return null;
  };

  const logout = () => {
    localStorage.removeItem("jwtToken");
    localStorage.removeItem("user");
    setUser(null);
    navigate("/");
  };

  const registerUser = async (
    userData: NewUser,
    recaptchaResponse: string | null
  ) => {
    try {
      const response = await registerApi(userData, recaptchaResponse);

      if (response.status === 201) {
        navigate("/wait-for-verify", { state: response.data });
      }
    } catch (error) {
      return error;
    }
  };

  return { user, logInClient, logout, registerUser };
};
