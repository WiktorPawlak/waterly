import React from "react";
import { createBrowserRouter, RouterProvider } from "react-router-dom";
import LogInPage from "./pages/LogInPage";
import RegisterPage from "./pages/RegisterPage";
import WaitForVerifyPage from "./pages/WaitForVerifyPage";
import EditAccountDetailsPage from "./pages/EditAccountDetailsPage";
import { ManageUsersAdminPage } from "./pages/admin";
import { VerifyUsersFMPage } from "./pages/facilityManager";
import VerifyAccountPage from "./pages/VerifyAccountPage";
import AccountDetailsPage from "./pages/AccountDetailsPage";

const Routing = createBrowserRouter([
  {
    path: "/",
    element: <LogInPage />,
  },
  {
    path: "/register",
    element: <RegisterPage />,
  },
  {
    path: "/wait-for-verify",
    element: <WaitForVerifyPage />,
  },
  {
    path: "/verify-account",
    element: <VerifyAccountPage />,
  },
  {
    path: "/edit-profile",
    element: <EditAccountDetailsPage />,
  },
  {
    path: "/manage-users",
    element: <ManageUsersAdminPage />,
  },
  {
    path: "/verify-users",
    element: <VerifyUsersFMPage />,
  },
  {
    path: "/accounts/:id/details",
    element: <AccountDetailsPage />,
  },
]);

export default function App() {
  return (
      <RouterProvider router={Routing} />
  );
}
