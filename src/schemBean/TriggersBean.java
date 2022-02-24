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
public class TriggersBean {
    String triggerName, triggerType, triggeringEvent, baseObjectType, tableName, ColumnName, referencingName, 
            whenClause, status, description, actionType, triggerBody, fireOnce, applyServerOnly, beforeStatement, beforeRow, afterRow, afterStatement, insteadOfRow;

    public String getTriggerName() {
        return triggerName;
    }

    public void setTriggerName(String triggerName) {
        this.triggerName = triggerName;
    }

    public String getTriggerType() {
        return triggerType;
    }

    public void setTriggerType(String triggerType) {
        this.triggerType = triggerType;
    }

    public String getTriggeringEvent() {
        return triggeringEvent;
    }

    public void setTriggeringEvent(String triggeringEvent) {
        this.triggeringEvent = triggeringEvent;
    }

    public String getBaseObjectType() {
        return baseObjectType;
    }

    public void setBaseObjectType(String baseObjectType) {
        this.baseObjectType = baseObjectType;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getColumnName() {
        return ColumnName;
    }

    public void setColumnName(String ColumnName) {
        this.ColumnName = ColumnName;
    }

    public String getReferencingName() {
        return referencingName;
    }

    public void setReferencingName(String referencingName) {
        this.referencingName = referencingName;
    }

    public String getWhenClause() {
        return whenClause;
    }

    public void setWhenClause(String whenClause) {
        this.whenClause = whenClause;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    public String getTriggerBody() {
        return triggerBody;
    }

    public void setTriggerBody(String triggerBody) {
        this.triggerBody = triggerBody;
    }

    public String getFireOnce() {
        return fireOnce;
    }

    public void setFireOnce(String fireOnce) {
        this.fireOnce = fireOnce;
    }

    public String getApplyServerOnly() {
        return applyServerOnly;
    }

    public void setApplyServerOnly(String applyServerOnly) {
        this.applyServerOnly = applyServerOnly;
    }

    public String getBeforeStatement() {
        return beforeStatement;
    }

    public void setBeforeStatement(String beforeStatement) {
        this.beforeStatement = beforeStatement;
    }

    public String getBeforeRow() {
        return beforeRow;
    }

    public void setBeforeRow(String beforeRow) {
        this.beforeRow = beforeRow;
    }

    public String getAfterRow() {
        return afterRow;
    }

    public void setAfterRow(String afterRow) {
        this.afterRow = afterRow;
    }

    public String getAfterStatement() {
        return afterStatement;
    }

    public void setAfterStatement(String afterStatement) {
        this.afterStatement = afterStatement;
    }

    public String getInsteadOfRow() {
        return insteadOfRow;
    }

    public void setInsteadOfRow(String insteadOfRow) {
        this.insteadOfRow = insteadOfRow;
    } 

    @Override
    public String toString() {
        String format = "%1$-25s | %2$-16s | %3$-27s | %4$-13s | %5$-20s | %6$-25s | %7$-34s | %8$-12s | %9$-10s | %10$-34s | %11$-8s | %12$-15s | %13$-15s | %14$-10s | %15$-10s | %16$-15s | %17$-12s |\n %18$-15s |\n %19$-12s ";        
        return String.format(format, triggerName, triggerType, triggeringEvent, baseObjectType, tableName, ColumnName, referencingName, whenClause, status, actionType,
                fireOnce, applyServerOnly, beforeStatement, beforeRow, afterRow, afterStatement, insteadOfRow, "@description = ' "+description+"'", "@triggerBody = ' "+triggerBody);
    }
}
