import { useTranslation } from "react-i18next";
import { Box, Typography } from "@mui/material";
import { WaterMeterDto } from "../../../api/waterMeterApi";
import { ThreeDotWaterMeterMenu } from "./ThreeDotWaterMeterMenu";
import { roles } from "../../../types";
import { useAccount } from "../../../hooks/useAccount";

interface Props {
  waterMeter: WaterMeterDto;
  handleReplaceButtonClick: (id: number) => void;
  handleEditButtonClick: (id: number) => void;
}

export const WaterMeterCard = ({ 
  waterMeter, 
  handleEditButtonClick, 
  handleReplaceButtonClick 
}: Props) => {
  const { t } = useTranslation();
  const { account } = useAccount();

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
      {account?.currentRole === roles.facilityManager &&
      <Box
        sx={{
          color: "text.secondary",
          marginLeft: '10px'
        }}
      >
        <ThreeDotWaterMeterMenu 
          waterMeterId={waterMeter.id}
          handleReplaceButtonClick={handleReplaceButtonClick} 
          handleEditButtonClick={handleEditButtonClick}
        />
      </Box>
    }
    </Box>
  );
};
