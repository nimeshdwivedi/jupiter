/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package schemBean;

import java.util.List;
import java.util.Objects;

/**
 *
 * @author nimeshd
 */
public class TableBean {
    String tableName;
    
    List<TableProperties> properties;

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public List<TableProperties> getProperties() {
        return properties;
    }

    public void setProperties(List<TableProperties> properties) {
        this.properties = properties;
    }

    public static class TableProperties {
        
        String columnName, dataType, dataLength, dataPrecision, dataScale, nullable, defaultLength, defaultData, lowValue, highValue, charLength, qualifiedColumnName;

        public String getColumnName() {
            return columnName;
        }

        public void setColumnName(String columnName) {
            this.columnName = columnName;
        }

        public String getDataType() {
            return dataType;
        }

        public void setDataType(String dataType) {
            this.dataType = dataType;
        }

        public String getDataLength() {
            return dataLength;
        }

        public void setDataLength(String dataLength) {
            this.dataLength = dataLength;
        }

        public String getDataPrecision() {
            return dataPrecision;
        }

        public void setDataPrecision(String dataPrecision) {
            this.dataPrecision = dataPrecision;
        }

        public String getDataScale() {
            return dataScale;
        }

        public void setDataScale(String dataScale) {
            this.dataScale = dataScale;
        }

        public String getNullable() {
            return nullable;
        }

        public void setNullable(String nullable) {
            this.nullable = nullable;
        }

        public String getDefaultLength() {
            return defaultLength;
        }

        public void setDefaultLength(String defaultLength) {
            this.defaultLength = defaultLength;
        }

        public String getDefaultData() {
            return defaultData;
        }

        public void setDefaultData(String defaultData) {
            this.defaultData = defaultData;
        }

        public String getLowValue() {
            return lowValue;
        }

        public void setLowValue(String lowValue) {
            this.lowValue = lowValue;
        }

        public String getHighValue() {
            return highValue;
        }

        public void setHighValue(String highValue) {
            this.highValue = highValue;
        }

        public String getCharLength() {
            return charLength;
        }

        public void setCharLength(String charLength) {
            this.charLength = charLength;
        }

        @Override
        public String toString() {
            String colFormat = " %1$-30s | %2$-10s | %3$-10s | %4$-14s | %5$-10s | %6$-10s | %7$-13s | %8$-12s | %9$-10s | %10$-10s | %11$-10s ";
            return String.format(colFormat, columnName, dataType, dataLength, dataPrecision, dataScale, nullable, defaultLength, defaultData, lowValue, highValue, charLength);
        }

        @Override
        public int hashCode() {
            int hash = 3;
            return hash;
        }
        
        public String compareWith(Object obj){
            StringBuilder mismatches = new StringBuilder();
            if (this == obj) {
                return null;
            }
            final TableProperties other = (TableProperties) obj;
            if (!Objects.equals(this.columnName, other.columnName)) {
                mismatches.append("Column Name @ ").append(this.columnName).append(" @ ").append(other.columnName).append("-AND-");
            }
            if (!Objects.equals(this.dataType, other.dataType)) {
                mismatches.append("Data Type @ ").append(this.dataType).append(" @ ").append(other.dataType).append("-AND-");
            }
            if (!Objects.equals(this.dataLength, other.dataLength)) {
                mismatches.append("Data Length @ ").append(this.dataLength).append(" @ ").append(other.dataLength).append("-AND-");
            }
            if (!Objects.equals(this.dataPrecision, other.dataPrecision)) {
                mismatches.append("Data Precision @ ").append(this.dataPrecision).append(" @ ").append(other.dataPrecision).append("-AND-");
            }
            if (!Objects.equals(this.dataScale, other.dataScale)) {
                mismatches.append("Data Scale @ ").append(this.dataScale).append(" @ ").append(other.dataScale).append("-AND-");
            }
            if (!Objects.equals(this.nullable, other.nullable)) {
                mismatches.append("Nullable @ ").append(this.nullable).append(" @ ").append(other.nullable).append("-AND-");
            }
            if (!Objects.equals(this.defaultLength, other.defaultLength)) {
                mismatches.append("Default Length @ ").append(this.defaultLength).append(" @ ").append(other.defaultLength).append("-AND-");
            }
            if (!Objects.equals(this.defaultData, other.defaultData)) {
                mismatches.append("Default Data @ ").append(this.defaultData).append(" @ ").append(other.defaultData).append("-AND-");
            }
            if (!Objects.equals(this.lowValue, other.lowValue)) {
                mismatches.append("Low Value @ ").append(this.lowValue).append(" @ ").append(other.lowValue).append("-AND-");
            }
            if (!Objects.equals(this.highValue, other.highValue)) {
                mismatches.append("High Value @ ").append(this.highValue).append(" @ ").append(other.highValue).append("-AND-");
            }
            if (!Objects.equals(this.charLength, other.charLength)) {
                mismatches.append("Char Length @ ").append(this.charLength).append(" @ ").append(other.charLength).append("-AND-");
            }
            if (!Objects.equals(this.qualifiedColumnName, other.qualifiedColumnName)) {
                mismatches.append("Qualified Column Name @ ").append(this.qualifiedColumnName).append(" @ ").append(other.qualifiedColumnName).append("-AND-");
            }
            return mismatches.toString();
        }

        
        public String getQualifiedColumnName() {
            return qualifiedColumnName;
        }

        public void setQualifiedColumnName(String qualifiedColumnName) {
            this.qualifiedColumnName = qualifiedColumnName;
        }
    }
}
