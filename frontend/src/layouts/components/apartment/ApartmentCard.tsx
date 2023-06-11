import { useTranslation } from "react-i18next";
import { Box, Button, Typography } from "@mui/material";
import { ApartmentDto } from "../../../api/apartmentApi";
import { useNavigate } from "react-router-dom";

interface Props {
  apartment: ApartmentDto;
}

export const ApartmentCard = ({
  apartment,
}: Props) => {
  const { t } = useTranslation();
  const navigate = useNavigate();

  const handleClick = (id: number) => {
    navigate('/apartments/' + id);
  };

  return (
      <Box
        sx={{
          display: "flex",
          flexDirection: "row",
          justifyContent: "space-between",
          alignItems: "center",
          width: 'fit-content',
          height: 'fit-content',
          bgcolor: 'background.paper',
          borderRadius: 1,
          boxShadow: 8,
          padding: '25px'
        }}
      >
        <Typography 
          variant="h5" 
          sx={{ 
            fontSize: "20px",
            fontWeight: "600"
          }}
        >
            {t("apartmentDetailsPage.localeTitle")} {apartment.number}
        </Typography>
        <Button
          variant="contained"
          sx={{
            textTransform: "none",
            fontWeight: "700",
            height: "fit-content",
            width: "fit-content",
            marginLeft: '20px'
          }}
          onClick={() => handleClick(apartment.id)}
        >
          {t("apartmentCard.checkButton")}
        </Button>
      </Box>
  );
};
