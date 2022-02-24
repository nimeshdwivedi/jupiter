/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package componentBean;

/**
 *
 * @author iDEApAD
 */

public class DBConnection {
    private String connectionName;
    private String password;
    private String username;
    private String serviceName;
    private String serviceID;
    private String host;
    private String custURL;
    private String driverName;
    private Integer port;

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getConnectionName() {
        return connectionName;
    }

    public void setConnectionName(String connectionName) {
        this.connectionName = connectionName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getServiceID() {
        return serviceID;
    }

    public void setServiceID(String serviceID) {
        this.serviceID = serviceID;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getCustURL() {
        return custURL;
    }

    public void setCustURL(String custURL) {
        this.custURL = custURL;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "DBConnection{" + "connectionName=" + connectionName + ", password=" + password + ", username=" + username + ", serviceName=" + serviceName + ", serviceID=" + serviceID + ", hostname=" + host + ", custURL=" + custURL + ", driverName=" + driverName + ", port=" + port + '}';
    }
    
}
