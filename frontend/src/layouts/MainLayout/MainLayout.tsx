import { Container, styled } from "@mui/material";
import { ReactNode } from "react";
import { Nav } from "../components";
import Breadcrumbs from "../components/Breadcrumbs";
import {
  adminRoutes,
  ownerRoutes,
  facilityManagerRoutes,
  publicRoutes,
  RouteType,
} from "../../routing/routes";
import { useAccount } from "../../hooks/useAccount";
import { useTranslation } from "react-i18next";

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
  const roleRoutesMap: { [key: string]: RouteType[] } = {
    administrator: adminRoutes,
    owner: ownerRoutes,
    facility_manager: facilityManagerRoutes,
    public: publicRoutes,
  };
  const { t } = useTranslation();

  const getRoutesForRole = (role: string) => {
    const routes = roleRoutesMap[role];

    const translatedRoutes = routes.map((route) => ({
      ...route,
      name: route.name ? t(route.name) : "",
    }));
    return translatedRoutes ? translatedRoutes : [];
  };

  const { account } = useAccount();

  const userRole = account?.currentRole.toLowerCase() || "public";
  const filteredRoutes = getRoutesForRole(userRole);

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
          <Breadcrumbs />
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
