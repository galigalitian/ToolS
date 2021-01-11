package com.tools.utils;

import java.io.Serializable;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

/**
 * @author TIAN WEI
 * @version 创建时间：2009-6-30 上午01:32:54
 * $Revision$ $Date$
 *
 */
@Component
@Scope(value = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class SessionManager implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = -2014476608067102587L;
    
    private String user_name;
    
    private String password;

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    
}
