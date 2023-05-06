import React from "react";
import { createBrowserRouter, RouterProvider } from "react-router-dom";
import LogInPage from "./pages/LogInPage";
import RegisterPage from "./pages/RegisterPage";
import WaitForVerifyPage from "./pages/WaitForVerifyPage";

const Routing = createBrowserRouter([
  {
    path: "/",
    element: <LogInPage />,
  },
  {
    path: "/register",
    element: <RegisterPage />,
  },
  {
    path: "/wait-for-verify",
    element: <WaitForVerifyPage />,
  },
]);

export default function App() {
  return (
    <React.StrictMode>
      <RouterProvider router={Routing} />
    </React.StrictMode>
  );
}
