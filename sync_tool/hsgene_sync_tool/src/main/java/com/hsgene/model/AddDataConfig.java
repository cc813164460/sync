package com.hsgene.model;

/**
 * @author: maodi@hsgene.com
 * @Description:
 * @Date: Created in 13:55 2017/10/23
 * @Modified By:
 */
public class AddDataConfig {
    public String canalZkServers;
    public String canalDestination;
    public String canalHost;
    public int canalPort;

    public String getCanalZkServers() {
        return canalZkServers;
    }

    public void setCanalZkServers(String canalZkServers) {
        this.canalZkServers = canalZkServers;
    }

    public String getCanalDestination() {
        return canalDestination;
    }

    public void setCanalDestination(String canalDestination) {
        this.canalDestination = canalDestination;
    }

    public String getCanalHost() {
        return canalHost;
    }

    public void setCanalHost(String canalHost) {
        this.canalHost = canalHost;
    }

    public int getCanalPort() {
        return canalPort;
    }

    public void setCanalPort(int canalPort) {
        this.canalPort = canalPort;
    }
}
