import HomePage from "../pages/HomePage";
import LogInPage from "../pages/LogInPage";
import RegisterPage from "../pages/RegisterPage";
import WaitForVerifyPage from "../pages/WaitForVerifyPage";
import { EditAccountDetailsPage } from "../pages/EditAccountDetailsPage";
import { VerifyUsersFMPage } from "../pages/facilityManager";
import VerifyAccountPage from "../pages/VerifyAccountPage";
import ResetPasswordPage from "../pages/ResetPasswordPage";
import AccountDetailsPage from "../pages/admin/AccountDetailsPage/AccountDetailsPage";
import { AcceptEmailPage } from "../pages/AcceptEmailPage";
import { ManageUsersAdminPage } from "../pages/admin";
import NotFound from "../pages/NotFound";
import { PATHS } from "./paths";

export const Pathnames = {
  public: {
    home: PATHS.HOME,
    login: PATHS.LOGIN,
    register: PATHS.REGISTER,
    notFound: PATHS.NOT_FOUND,
    waitForVerify: PATHS.WAIT_FOR_VERIFY,
    acceptMail: PATHS.ACCEPT_EMAIL,
    resetPassword: PATHS.RESET_PASSWORD,
    verifyAcc: PATHS.VERIFY_ACCOUNT,
  },
  owner: {
    home: PATHS.HOME,
    editAccountDetails: PATHS.EDIT_PROFILE,
    notFound: PATHS.NOT_FOUND,
    acceptMail: PATHS.ACCEPT_EMAIL,
  },
  facilityManager: {
    home: PATHS.HOME,
    editAccountDetails: PATHS.EDIT_PROFILE,
    notFound: PATHS.NOT_FOUND,
    verifyUsers: PATHS.VERIFY_USERS,
    acceptMail: PATHS.ACCEPT_EMAIL,
  },
  admin: {
    home: PATHS.HOME,
    manageUsers: PATHS.MANAGE_USERS,
    editAccountDetails: PATHS.EDIT_PROFILE,
    notFound: PATHS.NOT_FOUND,
    editUserDetails: PATHS.ACCOUNT_DETAILS,
    acceptMail: PATHS.ACCEPT_EMAIL,
  },
};

export type RouteType = {
  Component: () => JSX.Element | null;
  path: string;
  name?: string | null;
};

export const adminRoutes: RouteType[] = [
  {
    path: Pathnames.public.home,
    Component: HomePage,
    name: "navigation.home",
  },
  {
    path: Pathnames.admin.manageUsers,
    Component: ManageUsersAdminPage,
    name: "navigation.manageUsers",
  },
  {
    path: Pathnames.admin.editAccountDetails,
    Component: EditAccountDetailsPage,
    name: "navigation.editAccountDetails",
  },
  {
    path: Pathnames.admin.notFound,
    Component: NotFound,
  },
  {
    path: Pathnames.admin.editUserDetails,
    Component: AccountDetailsPage,
  },
  {
    path: Pathnames.public.acceptMail,
    Component: AcceptEmailPage,
  },
];

export const ownerRoutes: RouteType[] = [
  {
    path: Pathnames.public.home,
    Component: HomePage,
    name: "navigation.home",
  },
  {
    path: Pathnames.owner.editAccountDetails,
    Component: EditAccountDetailsPage,
    name: "navigation.editAccountDetails",
  },
  {
    path: Pathnames.owner.notFound,
    Component: NotFound,
  },
  {
    path: Pathnames.public.acceptMail,
    Component: AcceptEmailPage,
  },
];

export const facilityManagerRoutes: RouteType[] = [
  {
    path: Pathnames.public.home,
    Component: HomePage,
    name: "navigation.home",
  },
  {
    path: Pathnames.facilityManager.editAccountDetails,
    Component: EditAccountDetailsPage,
    name: "navigation.editAccountDetails",
  },
  {
    path: Pathnames.facilityManager.notFound,
    Component: NotFound,
  },
  {
    path: Pathnames.facilityManager.verifyUsers,
    Component: VerifyUsersFMPage,
    name: "navigation.verifyUsers",
  },
  {
    path: Pathnames.public.acceptMail,
    Component: AcceptEmailPage,
  },
];

export const publicRoutes: RouteType[] = [
  {
    path: Pathnames.public.home,
    Component: HomePage,
  },
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
    path: Pathnames.public.resetPassword,
    Component: ResetPasswordPage,
  },
  {
    path: Pathnames.public.verifyAcc,
    Component: VerifyAccountPage,
  },
];
