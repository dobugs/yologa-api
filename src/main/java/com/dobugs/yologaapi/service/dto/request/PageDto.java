package com.dobugs.yologaapi.service.dto.request;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class PageDto {

    private int page;
    private int size;

    public PageDto(final int page, final int size) {
        this.page = page;
        this.size = size;
    }
}
