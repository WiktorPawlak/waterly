import React, { useState } from "react";
import { DialogActions } from "@material-ui/core";
import {
  Button,
  Table,
  TableHead,
  TableRow,
  TableCell,
  TableBody,
  TextField,
} from "@mui/material";
import { Controller, useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { WaterMeterChecksDto, WaterMeterDto } from "../../../api/waterMeterApi";
import { z } from "zod";
import { useTranslation } from "react-i18next";
import { useAccount } from "../../../hooks/useAccount";
import { roles } from "../../../types/rolesEnum";
import { DatePicker, LocalizationProvider, plPL } from "@mui/x-date-pickers";
import dayjs, { Dayjs } from "dayjs";
import { AdapterDayjs } from "@mui/x-date-pickers/AdapterDayjs";

export const waterMeterChecksSchema = z.object({
  checkDate: z.string(),
  waterMeterChecks: z
    .array(
      z
        .object({
          waterMeterId: z.number().optional(),
          reading: z
            .string()
            .refine((value) => /^\d{1,5}\.\d{3}$/.test(value), {
              message: "validation.readingValue",
            }),
        })
        .optional()
    )
    .optional(),
});

export type WaterMeterChecksSchema = z.infer<typeof waterMeterChecksSchema>;

interface Props {
  waterMeters: WaterMeterDto[];
  onSubmit: (data: WaterMeterChecksDto) => Promise<void>;
}

export const WaterMeterCheckModal = ({ waterMeters, onSubmit }: Props) => {
  const { t } = useTranslation();
  const { account } = useAccount();
  const [checkDate, setCheckDate] = useState<Dayjs | null>(dayjs());
  const {
    register,
    handleSubmit,
    formState: { errors },
    setValue,
    control,
  } = useForm<WaterMeterChecksSchema>({
    resolver: zodResolver(waterMeterChecksSchema),
    mode: "onChange",
    reValidateMode: "onChange",
    defaultValues: {
      checkDate: "",
      // waterMeterChecks: waterMeters.map((waterMeter) => ({
      //   waterMeterId: waterMeter.id,
      //   reading: "",
      // })),
    },
  });

  const managerAuthored = account?.currentRole === roles.facilityManager;

  const handleReadingChange = (
    event: React.ChangeEvent<HTMLInputElement>,
    waterMeterId: number
  ) => {
    const { value } = event.target;
    setValue(`waterMeterChecks.${waterMeterId}.reading`, value);
  };
  const handleFormSubmit = async (formData: any) => {
    formData.waterMeterChecks = formData.waterMeterChecks.filter(
      (item: any) => item?.waterMeterId !== null && item?.reading !== null
    );
    formData.waterMeterChecks = formData.waterMeterChecks.filter(
      (item: any) =>
        item?.waterMeterId !== undefined && item?.reading !== undefined
    );

    if (managerAuthored) {
      formData.checkDate = checkDate?.format("YYYY-MM-DD");
    } else {
      formData.checkDate = dayjs().format("YYYY-MM-DD");
    }

    await onSubmit(formData);
  };

  const handleDateChange = (newValue: dayjs.Dayjs | null) => {
    setCheckDate(newValue);
  };

  return (
    <form onSubmit={handleSubmit(handleFormSubmit)}>
      {managerAuthored ? (
        <LocalizationProvider
          adapterLocale="pl"
          dateAdapter={AdapterDayjs}
          localeText={
            plPL.components.MuiLocalizationProvider.defaultProps.localeText
          }
        >
          <DatePicker
            label={t("WaterMeterListDialog.date")}
            format="YYYY-MM-DD"
            value={checkDate}
            disableFuture
            minDate={dayjs().startOf("month")}
            onChange={handleDateChange}
            views={["month", "year", "day"]}
            sx={{ my: 2 }}
          />
        </LocalizationProvider>
      ) : null}
      <Table>
        <TableHead>
          <TableRow>
            <TableCell>{t("WaterMeterListDialog.modal.id")}</TableCell>
            <TableCell>{t("WaterMeterListDialog.modal.type")}</TableCell>
            <TableCell>{t("WaterMeterListDialog.modal.reading")}</TableCell>
          </TableRow>
        </TableHead>
        <TableBody>
          {waterMeters.map((waterMeter) => (
            <TableRow key={waterMeter.id}>
              <TableCell>{waterMeter.id}</TableCell>
              <TableCell>
                {t("WaterMeterListDialog.modal." + waterMeter.type)}
              </TableCell>
              <TableCell>
                <Controller
                  name={`waterMeterChecks.${waterMeter.id}.waterMeterId`}
                  control={control}
                  defaultValue={waterMeter.id}
                  render={({ field }) => <input type="hidden" {...field} />}
                />
                <TextField
                  type="float"
                  {...register(`waterMeterChecks.${waterMeter.id}.reading`, {})}
                  error={!!errors?.waterMeterChecks?.[waterMeter.id]?.reading}
                  helperText={t(
                    errors?.waterMeterChecks?.[waterMeter.id]?.reading
                      ?.message!!
                  )}
                  onChange={(event: React.ChangeEvent<HTMLInputElement>) =>
                    handleReadingChange(event, waterMeter.id)
                  }
                />
              </TableCell>
            </TableRow>
          ))}
        </TableBody>
      </Table>
      <DialogActions>
        <Button variant="contained" type="submit">
          {t("WaterMeterListDialog.acceptButton")}
        </Button>
      </DialogActions>
    </form>
  );
};
