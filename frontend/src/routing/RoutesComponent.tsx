import { roles } from "../types";
import { Loading } from "../layouts/components/Loading";
import {
  adminRoutes,
  facilityManagerRoutes,
  ownerRoutes,
  Pathnames,
  publicRoutes,
} from "./routes";
import { useAccount } from "../hooks/useAccount";
import { Navigate, Route, Routes } from "react-router-dom";

export const RoutesComponent = () => {
  const { account, isLoading } = useAccount();

  if (isLoading) {
    return <Loading />;
  }

  return (
    <Routes>
      {!account &&
        publicRoutes.map(({ path, Component }) => (
          <Route key={path} path={path} element={<Component />} />
        ))}

      {account?.currentRole === roles.owner &&
        ownerRoutes.map(({ path, Component }) => (
          <Route key={path} path={path} element={<Component />} />
        ))}

      {account?.currentRole === roles.facilityManager &&
        facilityManagerRoutes.map(({ path, Component }) => (
          <Route key={path} path={path} element={<Component />} />
        ))}

      {account?.currentRole === roles.administrator &&
        adminRoutes.map(({ path, Component }) => (
          <Route key={path} path={path} element={<Component />} />
        ))}

      <Route
        path="*"
        element={<Navigate to={Pathnames.public.notFound} replace />}
      />
    </Routes>
  );
};
