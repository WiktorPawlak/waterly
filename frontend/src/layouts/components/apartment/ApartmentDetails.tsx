import { useTranslation } from "react-i18next";
import { Box, Typography } from "@mui/material";
import { ApartmentDto } from "../../../api/apartmentApi";
import { ShowBillModal } from "../bill/ShowBillModal";

interface Props {
  apartment: ApartmentDto;
}

export const ApartmentDetails = ({
  apartment,
}: Props) => {
  const { t } = useTranslation();

  return (
    <Box
      sx={{
        display: "flex",
        flexDirection: "column",
        rowGap: "20px",
        flex: '3',
        bgcolor: 'background.paper',
        height: '100%',
        minHeight: '160px',
        borderRadius: 1,
        boxShadow: 8,
        marginBottom: '25px'
      }}
    >
      <Typography
        variant="h5"
        sx={{
          fontWeight: "700",
          paddingLeft: '25px',
          paddingTop: '25px'
        }}
      >
        {t("apartmentDetailsPage.localeTitle")} {apartment.number}
      </Typography>
      <Box
        sx={{
          display: 'flex',
        }}
      >
        <Box>
          <Typography
            sx={{
              fontSize: '18px',
              fontWeight: '10',
              paddingLeft: '25px'
            }}
          >
            {t("apartmentDetailsPage.ownerTitle")}
          </Typography>
          <Typography
            sx={{
              fontSize: '15px',
              color: 'text.secondary',
              paddingLeft: '25px'
            }}
          >
            {apartment.ownerName}
          </Typography>
        </Box>
        <Box>
          <Typography
            sx={{
              fontSize: '18px',
              fontWeight: '10',
              paddingLeft: '25px'
            }}
          >
            {t("apartmentDetailsPage.areaTitle")}
          </Typography>
          <Typography
            sx={{
              fontSize: '15px',
              color: 'text.secondary',
              paddingLeft: '25px'
            }}
          >
            {apartment.area.toFixed(2)} m<sup>2</sup>
          </Typography>
        </Box>
      </Box>
      <Box
        sx={{
          mb: '25px'
        }}
      >
        <ShowBillModal apartmentId={apartment.id} />
      </Box>
    </Box>
  );
};
