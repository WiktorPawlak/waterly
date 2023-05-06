import i18n from "i18next";
import { initReactI18next } from "react-i18next";
import translationPL from "./common/translation/locales/pl/translation.json";
import translationEN from "./common/translation/locales/en/translation.json";

export const resources = {
  pl: {
    translation: translationPL,
  },
  en: {
    translation: translationEN,
  },
} as const;

i18n.use(initReactI18next).init({
  resources,
  lng: "pl",

  returnNull: false,
  interpolation: {
    escapeValue: false,
  },
});

const preferredLanguage = localStorage.getItem("preferredLanguage") ?? "pl";

// Set i18n language to preferred language
i18n.changeLanguage(preferredLanguage);

export default i18n;
