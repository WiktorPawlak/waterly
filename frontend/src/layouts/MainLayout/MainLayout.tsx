import { Container, styled } from "@mui/material";
import { ReactNode } from "react";
import { Nav } from "../components";
import { PATHS } from "../../routing/paths";
import { roles } from "../../types/rolesEnum";
import Breadcrumbs from "../components/Breadcrumbs";

export interface MainLayoutProps {
  hideMenuEntries?: boolean;
  children: ReactNode;
  isOverflowHidden?: boolean;
}

export const MainLayout = ({
  children,
  hideMenuEntries,
  isOverflowHidden = true,
}: MainLayoutProps) => {
  const routes = [
    {
      path: PATHS.MANAGE_USERS,
      name: "Manage users",
      role: [roles.administrator],
    },
    {
      path: PATHS.VERIFY_USERS,
      name: "Verify users",
      role: [roles.facilityManager],
    },
    {
      path: PATHS.LOGIN,
      name: "Login",
      role: undefined,
    },
    {
      path: PATHS.REGISTER,
      name: "REGISTER",
      role: undefined,
    },
  ];

  const user = localStorage.getItem("user")
    ? JSON.parse(localStorage.getItem("user") || "{}")
    : {};

  const userRoles = user?.roles;

  const filteredRoutes = routes.filter((route) =>
    route.role?.some((allowedRole) => userRoles?.includes(allowedRole))
  );

  return (
    <>
      <LayoutWrapper>
        <Nav hideMenuEntries={hideMenuEntries} routes={filteredRoutes} />
        <Container
          sx={{
            flex: "1 0 auto",
            marginTop: { xs: 12, sm: 20, md: 16 },
            justifyContent: "center",
            alignItems: "center",
            backgroundColor: "background.default",
            overflow: {
              xs: "visible",
              md: isOverflowHidden ? "hidden" : "visible",
            },
            px: { xs: 2, md: 15 },
          }}
        >
          <Breadcrumbs/>
          {children}
        </Container>
      </LayoutWrapper>
    </>
  );
};

const LayoutWrapper = styled("div")(({ theme }) => ({
  minHeight: "100vh",
  width: "100%",
  display: "flex",
  flexDirection: "column",
  backgroundColor: theme.palette.background.default,
}));
