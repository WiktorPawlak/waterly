import { Navigate, Route, Routes, BrowserRouter } from "react-router-dom";
import { roles } from "./types";
import { useEffect, useState } from "react";
import { Loading } from "./layouts/components/Loading";
import {
  publicRoutes,
  facilityManagerRoutes,
  adminRoutes,
  ownerRoutes,
  Pathnames,
} from "./routing/routes";

export const RoutesComponent = () => {
  const [userRoles, setUserRoles] = useState<string[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const user = localStorage.getItem("user")
      ? JSON.parse(localStorage.getItem("user") || "{}")
      : {};
    const roles = user?.roles || [];
    setUserRoles(roles);
    setLoading(false);
  }, []);

  const hasRole = (requiredRoles: string[]): boolean => {
    return requiredRoles.some((requiredRole) =>
      userRoles.includes(requiredRole)
    );
  };

  if (loading) {
    return <Loading />;
  }

  return (
    <Routes>
      {publicRoutes.map(({ path, Component }) => (
        <Route key={path} path={path} element={<Component />} />
      ))}

      {hasRole([roles.owner]) &&
        ownerRoutes.map(({ path, Component }) => (
          <Route key={path} path={path} element={<Component />} />
        ))}

      {hasRole([roles.facilityManager]) &&
        facilityManagerRoutes.map(({ path, Component }) => (
          <Route key={path} path={path} element={<Component />} />
        ))}

      {hasRole([roles.administrator]) &&
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

export default function App() {
  return (
    <BrowserRouter>
      <RoutesComponent />
    </BrowserRouter>
  );
}
