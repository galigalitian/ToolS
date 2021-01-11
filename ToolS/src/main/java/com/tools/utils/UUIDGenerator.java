package com.tools.utils;

import java.util.UUID;

/**
 * @author TIAN WEI
 * @version 创建时间 2012-4-8 下午09:57:03
 * $Revision$ $Date$
 */
public class UUIDGenerator {
    public static String getUUID() {
        String uuid = UUID.randomUUID().toString();
        String[] uuids = uuid.split("-");
        return uuids[0] + uuids[1];
    }
    
}
