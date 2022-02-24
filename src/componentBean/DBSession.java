/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package componentBean;

import java.util.List;

/**
 *
 * @author nimeshd
 */
public class DBSession {

    List<SessionParameter> parameters;

    public List<SessionParameter> getParameters() {
        return parameters;
    }

    public void setParameters(List<SessionParameter> parameters) {
        this.parameters = parameters;
    }
}
