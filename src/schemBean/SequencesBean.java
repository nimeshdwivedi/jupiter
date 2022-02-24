/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package schemBean;

/**
 *
 * @author nimeshd
 */
public class SequencesBean {
    String sequenceName, minValue, maxValue, incrementBy, cycleFlag, orderFlag, cacheSize;

    public String getSequenceName() {
        return sequenceName;
    }

    public void setSequenceName(String sequenceName) {
        this.sequenceName = sequenceName;
    }

    public String getMinValue() {
        return minValue;
    }

    public void setMinValue(String minValue) {
        this.minValue = minValue;
    }

    public String getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(String maxValue) {
        this.maxValue = maxValue;
    }

    public String getIncrementBy() {
        return incrementBy;
    }

    public void setIncrementBy(String incrementBy) {
        this.incrementBy = incrementBy;
    }

    public String getCycleFlag() {
        return cycleFlag;
    }

    public void setCycleFlag(String cycleFlag) {
        this.cycleFlag = cycleFlag;
    }

    public String getOrderFlag() {
        return orderFlag;
    }

    public void setOrderFlag(String orderFlag) {
        this.orderFlag = orderFlag;
    }

    public String getCacheSize() {
        return cacheSize;
    }

    public void setCacheSize(String cacheSize) {
        this.cacheSize = cacheSize;
    }

    @Override
    public String toString() {
        String format = "%1$-20s | %2$-15s | %3$-35s | %4$-12s | %5$-10s | %6$-10s | %7$-10s";        
        return String.format(format, sequenceName, minValue, maxValue, incrementBy, cycleFlag, orderFlag, cacheSize);
    }
    
}
