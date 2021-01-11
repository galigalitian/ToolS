package com.tools.utils;

import java.io.File;
import java.io.FileFilter;

/**
 * @author TIAN WEI
 * @version 创建时间 2012-1-6 上午03:19:45
 * $Revision$ $Date$
 */
public class DirectoryFilter implements FileFilter {

    @Override
    public boolean accept(File pathname) {
        if (pathname.isDirectory()) return true;
        else return false;
    }

}
