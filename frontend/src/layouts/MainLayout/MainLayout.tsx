import { Container, styled, Theme } from "@mui/material";
import React, { ReactNode } from "react";
import { Footer, Nav } from "../components";

export interface MainLayoutProps {
  hideMenuEntries?: boolean;
  children: ReactNode;
}

export const MainLayout = ({ children, hideMenuEntries }: MainLayoutProps) => {
  return (
    <>
      <LayoutWrapper>
        <Nav hideMenuEntries={hideMenuEntries} />
        <Container
          sx={{
            flex: "1 0 auto",
            marginTop: { xs: 12, sm: 20, md: 16 }, //md 22
            justifyContent: "center",
            alignItems: "center",
            backgroundColor: "background.default",
            overflow: { xs: "visible", md: "hidden" },
            px: { xs: 2, md: 15 },
          }}
        >
          {children}
        </Container>
        {/* <Footer /> */}
      </LayoutWrapper>
    </>
  );
};

const LayoutWrapper = styled("div")(({ theme }) => ({
  minHeight: "100vh",
  width: "100%",
  display: "flex",
  flexDirection: "column",
  backgroundColor: theme.palette.background.default,
}));
