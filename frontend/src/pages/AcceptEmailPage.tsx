import {useTranslation} from "react-i18next";
import {useEffect} from "react";
import {useNavigate, useSearchParams} from "react-router-dom";
import {postAcceptEmail} from "../api/accountApi";
import {useSnackbar} from "notistack";
import {MailUrlsLoading} from "../layouts/components/account/MailUrlsLoading";

export const AcceptEmailPage = () => {
    const {t} = useTranslation();
    const navigate = useNavigate();
    const {enqueueSnackbar} = useSnackbar();

    const [searchParams, setSearchParams] = useSearchParams();
    const token = searchParams.get("token") as string;

    useEffect(() => {
        postAcceptEmail(token)
            .then((response) => {
                if (response.status === 204) {
                    enqueueSnackbar(t("acceptEmailPage.toastSuccess"), {
                        variant: "success",
                    });
                } else {
                    enqueueSnackbar(t("acceptEmailPage.toastError"), {
                        variant: "error",
                    });
                }
            })
            .finally(() => navigate("/"));
    }, []);

    return (
        <MailUrlsLoading
            header={t("acceptEmailPage.header")}
            description={t("acceptEmailPage.description")}
        />
    );
};
