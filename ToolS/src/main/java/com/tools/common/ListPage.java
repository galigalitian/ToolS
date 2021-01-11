package com.tools.common;

import java.util.List;

/**
 * @author TIAN WEI
 * @version 创建时间：Jul 20, 2009 4:26:41 AM
 * $Revision$ $Date$
 * @param <T>
 *
 */
public class ListPage<T> {
    
    private List<T> listPage;
    
    private int firstRow;
    
    private int rowNum;
    
    private int currPage;
    
    private int nextPage;
    
    private int prePage;
    
    private int allRow;
    
    private int page;
    
    public int getFirstRow() {
        return firstRow;
    }

    public int getRowNum() {
        return rowNum;
    }

    public int getCurrPage() {
        return currPage;
    }

    public List<T> getListPage() {
        return listPage;
    }

    public int getNextPage() {
        return nextPage;
    }

    public int getAllRow() {
        return allRow;
    }

    public int getPrePage() {
        return prePage;
    }

    public int getPage() {
        return page;
    }

    public ListPage(List<T> listPage, int firstRow, int rowNum, int allRow) {
        this.firstRow = firstRow;
        this.rowNum= rowNum;
        this.listPage = listPage;
        this.allRow = allRow;
        this.nextPage = firstRow + rowNum;
        if (firstRow != 0 && firstRow >= rowNum) {
            this.prePage = firstRow - rowNum;
        } else {
            this.prePage = 0;
        }
        if (rowNum != 0) {
            this.page = (allRow % rowNum == 0) ? (allRow / rowNum) : (allRow / rowNum + 1);
            this.currPage = firstRow/rowNum + 1;
        }
    }
}
