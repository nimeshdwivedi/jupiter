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
public class ViewsBean {
    String viewName, text, viewType, editioningView, readOnly;

    public String getViewName() {
        return viewName;
    }

    public void setViewName(String viewName) {
        this.viewName = viewName;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;	
    }

    public String getViewType() {
        return viewType;
    }

    public void setViewType(String viewType) {
        this.viewType = viewType;
    }

    public String getEditioningView() {
        return editioningView;
    }

    public void setEditioningView(String editioningView) {
        this.editioningView = editioningView;
    }

    public String getReadOnly() {
        return readOnly;
    }

    public void setReadOnly(String readOnly) {
        this.readOnly = readOnly;
    }

    @Override
    public String toString() {
        String format = "%1$-30s | %2$-8s | %3$-15s | %4$-9s | %5$-500s";        
        return String.format(format, viewName, viewType, editioningView, readOnly, text);
    }
    
    
}
