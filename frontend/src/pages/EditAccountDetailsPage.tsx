import {MainLayout} from "../layouts/MainLayout";
import {Box, Typography} from "@mui/material";
import loginPose from "../assets/loginPose.svg";
import {useTranslation} from "react-i18next";
import {useEffect, useState} from "react";
import {AccountDto, getSelfAccountDetails} from "../api/accountApi";
import {resolveApiError} from "../api/apiErrors";
import {useSnackbar} from "notistack";
import {EditAccountDetailsForm} from "../layouts/components/account/EditAccountDetailsForm";
import {Loading} from "../layouts/components/Loading";

const EditAccountDetailsPage = () => {
    const [accountDetails, setAccountDetails] = useState<AccountDto>();
    const {enqueueSnackbar} = useSnackbar();
    const [etag, setEtag] = useState("");
    const {t} = useTranslation();

    const fetchAccountDetails = async () => {
        const response = await getSelfAccountDetails();
        if (response.status === 200) {
            setAccountDetails(response.data);
            setEtag(response.headers['etag']);
      } else {
        enqueueSnackbar(t(resolveApiError(response.error)), {
                variant: "error",
            });
        }
    };

    useEffect(() => {
        fetchAccountDetails();
    }, []);

    if (!accountDetails) {
        return <Loading/>;
    }

    return (
        <MainLayout>
            <Box
                sx={{
                    position: "relative",
                    height: "100vh",
                    mx: {xs: 2, md: 4},
                }}
            >
                <Typography variant="h4" sx={{fontWeight: "700", mb: 2}}>
                    {t("editAccountDetailsPage.header")}
                </Typography>

                <Typography sx={{mb: {xs: 5, md: 5}, color: "text.secondary"}}>
                    {t("editAccountDetailsPage.description")}
                </Typography>
                <EditAccountDetailsForm
                    account={accountDetails}
                    fetchAccountDetails={fetchAccountDetails}
                    etag = {etag} 
                />
                <Box
                    sx={{
                        width: {xs: "500px", md: "800px"},
                        height: {xs: "500px", md: "800px"},
                        position: "absolute",
                        bottom: 0,
                        right: {xs: "50%", md: -300},
                        transform: {xs: "translateX(-50%)", md: "translateX(0)"},
                        top: {xs: "50%", md: 50},
                    }}
                >
                    <img
                        src={loginPose}
                        alt="loginPose"
                        style={{
                            width: "100%",
                            height: "100%",
                            objectFit: "cover",
                        }}
                    />
                </Box>
            </Box>
        </MainLayout>
    );
};

export default EditAccountDetailsPage;
