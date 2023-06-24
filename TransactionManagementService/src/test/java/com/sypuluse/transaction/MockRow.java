package com.sypuluse.transaction;

import io.confluent.ksql.api.client.ColumnType;
import io.confluent.ksql.api.client.KsqlArray;
import io.confluent.ksql.api.client.KsqlObject;
import io.confluent.ksql.api.client.Row;

import java.math.BigDecimal;
import java.util.List;

public class MockRow implements Row {
    private final String id;
    private final String userId;
    private final BigDecimal amount;
    private final String currency;
    private final String accountIban;
    private final String valueDate;
    private final String description;

    public MockRow(String id, String userId, BigDecimal amount, String currency, String accountIban,
                   String valueDate, String description) {
        this.id = id;
        this.userId = userId;
        this.amount = amount;
        this.currency = currency;
        this.accountIban = accountIban;
        this.valueDate = valueDate;
        this.description = description;
    }

    @Override
    public List<String> columnNames() {
        return null;
    }

    @Override
    public List<ColumnType> columnTypes() {
        return null;
    }

    @Override
    public KsqlArray values() {
        return null;
    }

    @Override
    public KsqlObject asObject() {
        return null;
    }

    @Override
    public boolean isNull(int columnIndex) {
        return false;
    }

    @Override
    public boolean isNull(String columnName) {
        return false;
    }

    @Override
    public Object getValue(int columnIndex) {
        return null;
    }

    @Override
    public Object getValue(String columnName) {
        return null;
    }

    @Override
    public String getString(int columnIndex) {
        return null;
    }

    @Override
    public String getString(String columnLabel) {
        switch (columnLabel) {
            case "id":
                return id;
            case "userId":
                return userId;
            case "currency":
                return currency;
            case "accountIban":
                return accountIban;
            case "valueDate":
                return valueDate;
            case "description":
                return description;
            default:
                throw new IllegalArgumentException("Invalid column label: " + columnLabel);
        }
    }

    @Override
    public Integer getInteger(int columnIndex) {
        return null;
    }

    @Override
    public Integer getInteger(String columnName) {
        return null;
    }

    @Override
    public Long getLong(int columnIndex) {
        return null;
    }

    @Override
    public Long getLong(String columnName) {
        return null;
    }

    @Override
    public Double getDouble(int columnIndex) {
        return null;
    }

    @Override
    public Double getDouble(String columnName) {
        return null;
    }

    @Override
    public Boolean getBoolean(int columnIndex) {
        return null;
    }

    @Override
    public Boolean getBoolean(String columnName) {
        return null;
    }

    @Override
    public BigDecimal getDecimal(int columnIndex) {
        return null;
    }

    @Override
    public BigDecimal getDecimal(String columnLabel) {
        if ("amount".equals(columnLabel)) {
            return amount;
        } else {
            throw new IllegalArgumentException("Invalid column label: " + columnLabel);
        }
    }

    @Override
    public byte[] getBytes(int columnIndex) {
        return new byte[0];
    }

    @Override
    public byte[] getBytes(String columnName) {
        return new byte[0];
    }

    @Override
    public KsqlObject getKsqlObject(int columnIndex) {
        return null;
    }

    @Override
    public KsqlObject getKsqlObject(String columnName) {
        return null;
    }

    @Override
    public KsqlArray getKsqlArray(int columnIndex) {
        return null;
    }

    @Override
    public KsqlArray getKsqlArray(String columnName) {
        return null;
    }
}