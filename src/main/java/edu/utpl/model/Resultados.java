package edu.utpl.model;

import java.util.List;

public class Resultados {

    private Integer totalPages;
    private List<Titulo> data;
    private Integer page;
    private String paginator;
    private String status;

    public Resultados(Integer totalPages, List<Titulo> data, Integer page, String paginator, String status) {
        this.totalPages = totalPages;
        this.data = data;
        this.page = page;
        this.paginator = paginator;
        this.status = status;
    }


    public Resultados() {
    }

    public Integer getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(Integer totalPages) {
        this.totalPages = totalPages;
    }

    public List<Titulo> getData() {
        return data;
    }

    public void setData(List<Titulo> data) {
        this.data = data;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public String getPaginator() {
        return paginator;
    }

    public void setPaginator(String paginator) {
        this.paginator = paginator;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
