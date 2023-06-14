import HomePage from "../pages/HomePage";
import LogInPage from "../pages/LogInPage";
import RegisterPage from "../pages/RegisterPage";
import WaitForVerifyPage from "../pages/WaitForVerifyPage";
import { EditAccountDetailsPage } from "../pages/EditAccountDetailsPage";
import { VerifyUsersFMPage } from "../pages/facilityManager";
import { InvoicesListFMPage } from "../pages/facilityManager";
import { WaterMetersListFMPage } from "../pages/facilityManager";
import VerifyAccountPage from "../pages/VerifyAccountPage";
import ResetPasswordPage from "../pages/ResetPasswordPage";
import AccountDetailsPage from "../pages/admin/AccountDetailsPage/AccountDetailsPage";
import { AcceptEmailPage } from "../pages/AcceptEmailPage";
import { ManageUsersAdminPage } from "../pages/admin";
import NotFound from "../pages/NotFound";
import { PATHS } from "./paths";
import { ManageTariffsPage } from "../pages/facilityManager/ManageTariffsPage";
import { ApartmentListPage } from "../pages/facilityManager/ApartmentsListPage";
import { ApartmentDetailsPage } from "../pages/facilityManager/ApartmentDetailsPage";
import { ApartmentDashboardPage } from "../pages/owner/ApartmentDashboardPage";
import { BillsPage } from "../pages/facilityManager/BillsPage";
import { BillListPage } from "../pages/owner/BillListPage";

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
    manageTariffs: PATHS.MANAGE_TARIFFS,
  },
  owner: {
    home: PATHS.HOME,
    editAccountDetails: PATHS.EDIT_PROFILE,
    notFound: PATHS.NOT_FOUND,
    acceptMail: PATHS.ACCEPT_EMAIL,
    manageTariffs: PATHS.MANAGE_TARIFFS,
    manageApartments: PATHS.MANAGE_APARTMENTS,
    apartmentDetails: PATHS.APARTMENT_DETAILS,
    apartemtnBills: PATHS.APARTMENT_BILLS,
    bills: PATHS.BILLS,
  },
  facilityManager: {
    home: PATHS.HOME,
    editAccountDetails: PATHS.EDIT_PROFILE,
    notFound: PATHS.NOT_FOUND,
    verifyUsers: PATHS.VERIFY_USERS,
    invoices: PATHS.INVOICES,
    waterMeters: PATHS.WATER_METERS,
    acceptMail: PATHS.ACCEPT_EMAIL,
    manageTariffs: PATHS.MANAGE_TARIFFS,
    manageApartments: PATHS.MANAGE_APARTMENTS,
    apartmentDetails: PATHS.APARTMENT_DETAILS,
    apartemtnBills: PATHS.APARTMENT_BILLS,
  },
  admin: {
    home: PATHS.HOME,
    manageUsers: PATHS.MANAGE_USERS,
    editAccountDetails: PATHS.EDIT_PROFILE,
    notFound: PATHS.NOT_FOUND,
    editUserDetails: PATHS.ACCOUNT_DETAILS,
    acceptMail: PATHS.ACCEPT_EMAIL,
    manageTariffs: PATHS.MANAGE_TARIFFS,
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
    path: Pathnames.public.manageTariffs,
    Component: ManageTariffsPage,
    name: "navigation.tariffs",
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
    path: Pathnames.owner.apartemtnBills,
    Component: BillsPage,
  },
  {
    path: Pathnames.owner.manageApartments,
    Component: ApartmentDashboardPage,
    name: "navigation.apartments",
  },
  {
    path: Pathnames.owner.apartmentDetails,
    Component: ApartmentDashboardPage,
  },
  {
    path: Pathnames.public.manageTariffs,
    Component: ManageTariffsPage,
    name: "navigation.tariffs",
  },
  {
    path: Pathnames.owner.editAccountDetails,
    Component: EditAccountDetailsPage,
    name: "navigation.editAccountDetails",
  },
  {
    path: Pathnames.owner.bills,
    Component: BillListPage,
    name: "navigation.bills",
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
    path: Pathnames.facilityManager.manageApartments,
    Component: ApartmentListPage,
    name: "navigation.apartments",
  },
  {
    path: Pathnames.facilityManager.apartmentDetails,
    Component: ApartmentDetailsPage,
  },
  {
    path: Pathnames.public.manageTariffs,
    Component: ManageTariffsPage,
    name: "navigation.tariffs",
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
    path: Pathnames.facilityManager.apartemtnBills,
    Component: BillsPage,
  },
  {
    path: Pathnames.facilityManager.invoices,
    Component: InvoicesListFMPage,
    name: "navigation.invoices",
  },
  {
    path: Pathnames.facilityManager.waterMeters,
    Component: WaterMetersListFMPage,
    name: "navigation.waterMeters",
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
    path: Pathnames.public.manageTariffs,
    Component: ManageTariffsPage,
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
