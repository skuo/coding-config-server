package com.coding.configserver.rest;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HolaController {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    /**
     * A simple endpoint to print out host IP address.  It is useful in testing Kubernetes
     * @return
     */
    @RequestMapping(value = "/hola", method = RequestMethod.GET, produces = "application/json")
    public String hola() {
        String hostname = null;
        try {
            hostname = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException uhe) {
            hostname = "unknown";
        }
        String jsonStr = 
                "{"
                + "\"java.version\":\"" + System.getProperty("java.version") + "\"" + ","
                + "\"hostname\":\"" +  hostname + "\"" + ","
                + "\"suggestion\":\"try git bootRun --debug-jvm\""
                + "}";
        log.info(jsonStr);
        return jsonStr;
    }

}
