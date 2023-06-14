import {Trans, useTranslation} from "react-i18next";
import {Box, FormControl, MenuItem, Select} from "@mui/material";
import {ApartmentDto} from "../../../api/apartmentApi";
import {PaginatedList} from "../../../api/accountApi";

interface Props {
    apartments: PaginatedList<ApartmentDto>;
    setApartmentId: (id: number) => void;
    apartmentId: number;
}

export const ApartmentDropdown = ({
                                      apartments,
                                      setApartmentId,
                                      apartmentId,
                                  }: Props) => {
    const {t} = useTranslation();

  return (
    <Box>
        <FormControl sx={{ width: '100%' }}>
          {t("apartmentDropdown.chooseApartment")}
          <Select
              autoFocus
              label={
                  <Trans
                  i18nKey={"apartmentDropdown.chooseApartment"}
                  components={{ sup: <sup /> }}
                  />
              }
              variant="standard"
              defaultValue={apartmentId}
              sx={{ width: "100% !important" }}
              onChange={(newValue) => setApartmentId(parseInt(newValue.target.value as string))}
          >
            {apartments.data.map((obj) => (
              <MenuItem value={obj.id}>
                {t("apartmentDropdown.localeTitle")} {obj.number}
              </MenuItem>
            ))}
          </Select>
      </FormControl>
    </Box>
  );
    return (
        <Box>
            <FormControl sx={{width: "100%"}}>
                {t("apartmentDropdown.chooseApartment")}
                <Select
                    autoFocus
                    label={
                        <Trans
                            i18nKey={"apartmentDropdown.chooseApartment"}
                            components={{sup: <sup/>}}
                        />
                    }
                    variant="standard"
                    value={apartmentId}
                    sx={{width: "100% !important"}}
                    onChange={(newValue) =>
                        setApartmentId(parseInt(newValue.target.value as string))
                    }
                >
                    {apartments.data.map((obj) => (
                        <MenuItem value={obj.id}>
                            {t("apartmentDropdown.localeTitle")} {obj.number}
                        </MenuItem>
                    ))}
                </Select>
            </FormControl>
        </Box>
    );
};
