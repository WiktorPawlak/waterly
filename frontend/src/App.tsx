import { createBrowserRouter, RouterProvider } from "react-router-dom";
import LogInPage from "./pages/LogInPage";
import RegisterPage from "./pages/RegisterPage";
import WaitForVerifyPage from "./pages/WaitForVerifyPage";
import EditAccountDetailsPage from "./pages/EditAccountDetailsPage";
import { ManageUsersAdminPage } from "./pages/admin";
import { VerifyUsersFMPage } from "./pages/facilityManager";
import VerifyAccountPage from "./pages/VerifyAccountPage";
import ResetPasswordPage from "./pages/ResetPasswordPage";
import AccountDetailsPage from "./pages/admin/AccountDetailsPage/AccountDetailsPage";
import { AcceptEmailPage } from "./pages/AcceptEmailPage";

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
    path: "/accept-email",
    element: <AcceptEmailPage />,
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
  {
    path: "/password/reset",
    element: <ResetPasswordPage />,
  },
]);

export default function App() {
  return (
    <>
      <RouterProvider router={Routing} />
    </>
  );
}
