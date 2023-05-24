import {useTranslation} from "react-i18next";
import {useEffect} from "react";
import {useSearchParams} from "react-router-dom";
import {postAcceptEmail} from "../api/accountApi";
import {useSnackbar} from "notistack";
import {MailUrlsLoading} from "../layouts/components/account/MailUrlsLoading";

export const AcceptEmailPage = () => {
    const {t} = useTranslation();
    const {enqueueSnackbar} = useSnackbar();

    const [searchParams] = useSearchParams();
    const token = searchParams.get("token") as string;

    const acceptEmail = async () => {
        const response = await postAcceptEmail(token);

        if (response.status === 204) {
            enqueueSnackbar(t("acceptEmailPage.toastSuccess"), {
                variant: "success",
            });
        } else {
            enqueueSnackbar(t("acceptEmailPage.toastError"), {
                variant: "error",
            });
        }
    };

    useEffect(() => {
        acceptEmail();
    }, []);

    return (
        <MailUrlsLoading
            header={t("acceptEmailPage.header")}
            description={t("acceptEmailPage.description")}
        />
    );
};
