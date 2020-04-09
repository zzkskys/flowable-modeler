package com.qunchuang.modeler.rest;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PageImpl<T> {

    private long total = 0;

    private long totalElements = 0;

    private int totalPages = 0;

    private boolean next = false;

    private boolean last = false;

    private List<T> content;


    public PageImpl(List<T> content) {
        this.content = content;
        this.total = content.size();
    }

}
