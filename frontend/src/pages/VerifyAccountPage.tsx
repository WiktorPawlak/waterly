import { useTranslation } from "react-i18next";
import { useEffect } from "react";
import { useNavigate, useSearchParams } from "react-router-dom";
import { putVerifyAccount } from "../api/accountApi";
import { useSnackbar } from "notistack";
import { MailUrlsLoading } from "../layouts/components/account/MailUrlsLoading";

const VerifyAccountPage = () => {
  const { t } = useTranslation();
  const navigate = useNavigate();
  const { enqueueSnackbar } = useSnackbar();

  const [searchParams, setSearchParams] = useSearchParams();
  const token = searchParams.get("token") as string;

  useEffect(() => {
    putVerifyAccount(token)
      .then((response) => {
        if (response.status === 200) {
          enqueueSnackbar(t("verifyAccountPage.toastSuccess"), {
            variant: "success",
          });
        } else {
          enqueueSnackbar(t("verifyAccountPage.toastError"), {
            variant: "error",
          });
        }
      })
      .finally(() => navigate("/"));
  }, []);

  return (
    <MailUrlsLoading
      header={t("verifyAccountPage.header")}
      description={t("verifyAccountPage.description")}
    />
  );
};

export default VerifyAccountPage;
