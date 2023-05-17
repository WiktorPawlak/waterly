import LogInPage from "./../pages/LogInPage";
import RegisterPage from "./../pages/RegisterPage";
import WaitForVerifyPage from "./../pages/WaitForVerifyPage";
import { EditAccountDetailsPage } from "./../pages/EditAccountDetailsPage";
import { VerifyUsersFMPage } from "./../pages/facilityManager";
import VerifyAccountPage from "./../pages/VerifyAccountPage";
import ResetPasswordPage from "./../pages/ResetPasswordPage";
import AccountDetailsPage from "./../pages/admin/AccountDetailsPage/AccountDetailsPage";
import { AcceptEmailPage } from "./../pages/AcceptEmailPage";
import { ManageUsersAdminPage } from "./../pages/admin";
import NotFound from "./../pages/NotFound";

export const Pathnames = {
  public: {
    login: "/",
    register: "/register",
    notFound: "/not-found",
    editAccountDetails: "/edit-profile",
    waitForVerify: "/wait-for-verify",
    acceptMail: "/accept-email",
    resetPassword: "/reset-password",
    verifyAcc: "/verify",
  },
  owner: {
    editAccountDetails: "/edit-profile",
    notFound: "/not-found",
  },
  facilityManager: {
    editAccountDetails: "/edit-profile",
    notFound: "/not-found",
    verifyUsers: "/verify-users",
  },
  admin: {
    manageUsers: "/manage-users",
    editAccountDetails: "/edit-profile",
    notFound: "/not-found",
    editUserDetails: "/accounts/:id/details",
  },
};

export type RouteType = {
  Component: () => JSX.Element | null;
  path: string;
};

export const adminRoutes: RouteType[] = [
  {
    path: Pathnames.admin.manageUsers,
    Component: ManageUsersAdminPage,
  },
  {
    path: Pathnames.admin.editAccountDetails,
    Component: EditAccountDetailsPage,
  },
  {
    path: Pathnames.admin.notFound,
    Component: NotFound,
  },
  {
    path: Pathnames.admin.editUserDetails,
    Component: AccountDetailsPage,
  },
];

export const ownerRoutes: RouteType[] = [
  {
    path: Pathnames.owner.editAccountDetails,
    Component: EditAccountDetailsPage,
  },
  {
    path: Pathnames.owner.notFound,
    Component: NotFound,
  },
];

export const facilityManagerRoutes: RouteType[] = [
  {
    path: Pathnames.facilityManager.editAccountDetails,
    Component: EditAccountDetailsPage,
  },
  {
    path: Pathnames.facilityManager.notFound,
    Component: NotFound,
  },
  {
    path: Pathnames.facilityManager.verifyUsers,
    Component: VerifyUsersFMPage,
  },
];

export const publicRoutes: RouteType[] = [
  {
    path: Pathnames.public.login,
    Component: LogInPage,
  },
  {
    path: Pathnames.public.register,
    Component: RegisterPage,
  },
  {
    path: Pathnames.public.notFound,
    Component: NotFound,
  },
  {
    path: Pathnames.public.acceptMail,
    Component: AcceptEmailPage,
  },
  {
    path: Pathnames.public.waitForVerify,
    Component: WaitForVerifyPage,
  },
  {
    path: Pathnames.public.editAccountDetails,
    Component: EditAccountDetailsPage,
  },
  {
    path: Pathnames.public.resetPassword,
    Component: ResetPasswordPage,
  },
  {
    path: Pathnames.public.verifyAcc,
    Component: VerifyAccountPage,
  },
];
