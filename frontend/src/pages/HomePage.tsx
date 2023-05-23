import HelpIcon from "@mui/icons-material/Help";
import { useTranslation } from "react-i18next";
import { Box, Grid, Typography } from "@mui/material";
import verifyPose from "../assets/verifyPose.svg";
import { MainLayout } from "../layouts/MainLayout";

const NotFound = () => {
	const { t } = useTranslation();

	return (
		<MainLayout>
				<Grid
					sx={{
						mt: { xs: 10, md: 4 },
						display: "flex",
						flexDirection: "column",
						justifyContent: { xs: "flex-start", md: "center" },
						alignItems: "center",
						textAlign: "center",
					}}
					item
					xs={9}
					md={4}
				>
					<Typography
						variant="h2"
						sx={{
							mt: 4,
							fontSize: { xs: "50px", md: "100px" },
							fontWeight: "700",
						}}
					>
						Home Page
					</Typography>
					<Typography
						sx={{
							mt: 2,
							color: "text.secondary",
							mb: { xs: 10, md: 0 },
							fontSize: { xs: "20px", md: "30px" },
						}}
					>
						Build in progress...
					</Typography>
					<Box
						sx={{
							width: { xs: "500px", md: "600px" },
							height: { xs: "500px", md: "800px" },
							position: "absolute",
							bottom: 0,
							left: { xs: "50%", md: -200 },
							transform: { xs: "translateX(-50%)", md: "translateX(0)" },
							top: { xs: "50%", md: 200 },
						}}
					>
						<img
							src={verifyPose}
							alt="verifyPose"
							style={{
								width: "100%",
								height: "100%",
								objectFit: "cover",
							}}
						/>
					</Box>
				</Grid>
		</MainLayout>
	);
};

export default NotFound;
