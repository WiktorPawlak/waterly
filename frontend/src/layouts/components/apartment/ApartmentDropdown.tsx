import { Trans, useTranslation } from "react-i18next";
import { Box, FormControl, MenuItem, Select } from "@mui/material";
import { ApartmentDto, getAllAprtmentsList } from "../../../api/apartmentApi";
import { useEffect, useState } from "react";
import { GetPagedListDto, PaginatedList } from "../../../api/accountApi";
import { resolveApiError } from "../../../api/apiErrors";
import { enqueueSnackbar } from "notistack";

interface Props {
  setApartmentId: (id: number) => void;
  apartmentId: number;
}

export const ApartmentDropdown = ({ setApartmentId, apartmentId }: Props) => {
  const { t } = useTranslation();

  const [pageState, setPageState] = useState<PaginatedList<ApartmentDto>>({
    data: [],
    pageNumber: 1,
    itemsInPage: 0,
    totalPages: 0,
  });

  const [listRequest, setListRequest] = useState<GetPagedListDto>({
    page: 1,
    pageSize: 100,
    order: "asc",
    orderBy: "number",
  });

  const fetchApartments = async () => {
    const response = await getAllAprtmentsList(listRequest, '');

    if (response.status === 200) {
      setPageState(response.data!);
    } else {
      enqueueSnackbar(t(resolveApiError(response.error)), {
        variant: "error",
      });
    }
  };

  useEffect(() => {
    fetchApartments();
  }, []);

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
              value={apartmentId}
              sx={{ width: "100% !important" }}
              onChange={(newValue) => setApartmentId(parseInt(newValue.target.value as string))}
          >
            {pageState.data.map((obj) => (
              <MenuItem value={obj.id}>
                {t("apartmentDropdown.localeTitle")} {obj.number}
              </MenuItem>
            ))}
          </Select>
      </FormControl>
    </Box>
  );
};

