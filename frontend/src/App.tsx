import { BrowserRouter } from "react-router-dom";
import { RoutesComponent } from "./routing/RoutesComponent";
import { AccountStateContextProvider } from "./context/AccountContext";

export default function App() {
  return (
    <AccountStateContextProvider>
      <BrowserRouter>
        <RoutesComponent />
      </BrowserRouter>
    </AccountStateContextProvider>
  );
}
