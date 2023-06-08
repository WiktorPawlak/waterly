package pl.lodz.p.it.ssbd2023.ssbd06.persistence.config;

import java.sql.Date;
import java.time.Instant;
import java.time.YearMonth;
import java.time.ZoneId;

import jakarta.persistence.AttributeConverter;

public class YearMonthDateAttributeConverter implements AttributeConverter<YearMonth, Date> {

    @Override
    public Date convertToDatabaseColumn(final YearMonth attribute) {
        return Date.valueOf(attribute.atDay(1));
    }

    @Override
    public YearMonth convertToEntityAttribute(final Date dbData) {
        return YearMonth.from(Instant.ofEpochMilli(dbData.getTime())
                .atZone(ZoneId.systemDefault())
                .toLocalDate());
    }
}