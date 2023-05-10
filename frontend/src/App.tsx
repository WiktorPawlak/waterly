import React from "react";
import { createBrowserRouter, RouterProvider } from "react-router-dom";
import LogInPage from "./pages/LogInPage";
import RegisterPage from "./pages/RegisterPage";
import WaitForVerifyPage from "./pages/WaitForVerifyPage";
import EditAccountDetailsPage from "./pages/EditAccountDetailsPage";
import { ManageUsersAdminPage } from "./pages/admin";

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
    path: "/edit-profile",
    element: <EditAccountDetailsPage />,
  },
  {
    path: "/manage-users",
    element: <ManageUsersAdminPage />,
  },
]);

export default function App() {
  return (
    <React.StrictMode>
      <RouterProvider router={Routing} />
    </React.StrictMode>
  );
}
