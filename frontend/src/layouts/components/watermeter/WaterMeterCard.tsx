import { useTranslation } from "react-i18next";
import { Box, Typography } from "@mui/material";
import { useNavigate } from "react-router-dom";
import { WaterMeterDto } from "../../../api/waterMeterApi";
import React from "react";

interface Props {
  waterMeter: WaterMeterDto;
}

export const WaterMeterCard = ({ waterMeter }: Props) => {
  const { t } = useTranslation();
  const navigate = useNavigate();

  const handleClick = (id: number) => {
    navigate("/apartments/" + id);
  };

  let waterType = "";
  if (waterMeter.type === "HOT_WATER") {
    waterType = t("assignWaterMeterDialog.hotWater");
  } else if (waterMeter.type === "COLD_WATER") {
    waterType = t("assignWaterMeterDialog.coldWater");
  }

  return (
    <Box
      sx={{
        display: "flex",
        flexDirection: "row",
        justifyContent: "space-between",
        alignItems: "center",
        width: "fit-content",
        height: "fit-content",
        bgcolor: "background.paper",
        borderRadius: 1,
        boxShadow: 8,
        padding: "25px",
      }}
    >
      <Box sx={{ display: "flex", flexDirection: "column" }}>
        <Typography
          variant="h5"
          sx={{
            fontSize: "15px",
            marginBottom: "10px",
          }}
        >
          {t("waterMeterDetails.waterMeter")}
        </Typography>
        <Typography
          variant="h5"
          sx={{
            fontSize: "15px",
            color: "text.secondary",
            marginBottom: "10px",
          }}
        >
          {t("waterMeterDetails.expiryDate")}
        </Typography>
        <Typography
          variant="h5"
          sx={{
            fontSize: "15px",
            color: "text.secondary",
            marginBottom: "10px",
          }}
        >
          {t("waterMeterDetails.expectedDailyUsage")}
        </Typography>
        <Typography
          variant="h5"
          sx={{
            fontSize: "15px",
            color: "text.secondary",
            marginBottom: "10px",
          }}
        >
          {t("waterMeterDetails.type")}
        </Typography>
        <Typography
          variant="h5"
          sx={{ fontSize: "15px", color: "text.secondary" }}
        >
          {t("waterMeterDetails.startingValue")}
        </Typography>
      </Box>
      <Box sx={{ display: "flex", flexDirection: "column" }}>
        <Typography
          variant="h5"
          sx={{
            fontSize: "15px",
            color: "text.secondary",
            marginBottom: "10px",
          }}
        >
          {waterMeter.id}
        </Typography>
        <Typography
          variant="h5"
          sx={{
            fontSize: "15px",
            color: "text.secondary",
            marginBottom: "10px",
          }}
        >
          {waterMeter.expiryDate}
        </Typography>
        <Typography
          variant="h5"
          sx={{
            fontSize: "15px",
            color: "text.secondary",
            marginBottom: "10px",
          }}
        >
          {waterMeter.expectedDailyUsage}
        </Typography>
        <Typography
          variant="h5"
          sx={{
            fontSize: "15px",
            color: "text.secondary",
            marginBottom: "10px",
          }}
        >
          {waterType}
        </Typography>
        <Typography
          variant="h5"
          sx={{
            fontSize: "15px",
            color: "text.secondary",
          }}
        >
          {waterMeter.startingValue}
        </Typography>
      </Box>
    </Box>
  );
};
