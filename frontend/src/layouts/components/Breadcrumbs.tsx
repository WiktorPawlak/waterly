import { Link, useLocation } from "react-router-dom";
import { Breadcrumbs as MUIBreadcrumbs, Typography } from "@mui/material";
import NavigateNextIcon from "@mui/icons-material/NavigateNext";
import { useTranslation } from "react-i18next";
import { PATHS } from "../../routing/paths";

export default function Breadcrumbs() {
  const location = useLocation();
  const { t } = useTranslation();

  const breadcrumbsKeyMap: { [key: string]: string } = {
    accounts: "breadcrumbs.accounts",
    "verify-users": "breadcrumbs.verify-users",
    profile: "breadcrumbs.profile",
    tariffs: "breadcrumbs.tariffs",
    apartments: "breadcrumbs.apartments",
  };

  const excludedPaths = [PATHS.LOGIN, PATHS.REGISTER] as string[];

  let currentLink = "";

  const pathnames = location.pathname.split("/").filter((crumb) => crumb);

  const crumbs = pathnames.map((crumb, index) => {
    currentLink += `/${crumb}`;
    const isLast = index == pathnames.length - 1;
    return isLast ? (
      <Typography key={crumb}>
        {t(breadcrumbsKeyMap[crumb] ?? crumb)}
      </Typography>
    ) : (
      <Link to={currentLink} key={crumb}>
        {t(breadcrumbsKeyMap[crumb] ?? crumb)}
      </Link>
    );
  });

  const isFirstPage =
    pathnames.length < 1 ||
    pathnames.every((name) => excludedPaths.includes("/" + name));

  if (isFirstPage) {
    return null;
  }

  return (
    <MUIBreadcrumbs
      separator={<NavigateNextIcon fontSize="small" />}
      aria-label="breadcrumb"
      sx={{
        mx: { xs: 2, md: 4 },
        mb: { xs: 1, md: 2 },
      }}
    >
      <Link to={"/"}>{t("breadcrumbs.homePage")}</Link>
      {crumbs}
    </MUIBreadcrumbs>
  );
}
