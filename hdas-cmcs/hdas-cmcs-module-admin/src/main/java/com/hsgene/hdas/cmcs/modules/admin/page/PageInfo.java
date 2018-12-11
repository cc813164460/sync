package com.hsgene.hdas.cmcs.modules.admin.page;

import com.github.pagehelper.Page;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * @description: 分页数据实体类
 * @projectName: hdas-cmcs
 * @package: com.hsgene.hdas.cmcs.modules.admin.query
 * @author: maodi
 * @createDate: 2018/5/30 16:41
 * @version: 1.0
 * Copyright: Copyright (c) 2018
 */
public class PageInfo<T> implements Serializable {

    private static final long serialVersionUID = 5156200761123727139L;

    /**
     * 结果集
     */
    private List<T> data;
    /**
     * 页数
     */
    private long pageNum;
    /**
     * 渲染的计数
     */
    private long draw;
    /**
     * 每页数量
     */
    private int pageSize;
    /**
     * 过滤后记录总数
     */
    private long recordsFiltered;
    /**
     * 记录总数
     */
    private long recordsTotal;

    public PageInfo() {
    }

    /**
     * @param list 数据链表
     * @return
     * @description 包装Page对象
     * @Auth2018/6/13 16:26 @* @param null13 16:13
     */
    public PageInfo(List<T> list) {
        if (list instanceof Page) {
            Page page = (Page) list;
            this.data = page;
            this.pageSize = page.getPageSize();
            this.recordsTotal = page.getTotal();
            this.recordsFiltered = page.getTotal();
        } else if (list instanceof Collection) {
            this.recordsFiltered = list.size();
            this.data = list;
            this.recordsTotal = list.size();
            this.pageSize = list.size();
        }
    }

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }

    public long getDraw() {
        return draw;
    }

    public void setDraw(long draw) {
        this.draw = draw;
    }

    public long getPageNum() {
        return pageNum;
    }

    public void setPageNum(long pageNum) {
        this.pageNum = pageNum;
    }

    public long getRecordsFiltered() {
        return recordsFiltered;
    }

    public void setRecordsFiltered(long recordsFiltered) {
        this.recordsFiltered = recordsFiltered;
    }

    public long getRecordsTotal() {
        return recordsTotal;
    }

    public void setRecordsTotal(long recordsTotal) {
        this.recordsTotal = recordsTotal;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("PageInfo{");
        sb.append(", data=").append(data);
        sb.append(", draw=").append(draw);
        sb.append(", recordsFiltered=").append(recordsFiltered);
        sb.append(", recordsTotal=").append(recordsTotal);
        sb.append(", pageSize=").append(pageSize);
        sb.append('}');
        return sb.toString();
    }

}
