package com.dobugs.yologaapi.support;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
public class PagingGenerator {

    public Pageable from(final Integer page, final Integer size) {
        if (page == null && size != null) {
            throw new IllegalArgumentException("page 값을 입력해주세요.");
        }
        if (page != null && size == null) {
            throw new IllegalArgumentException("size 값을 입력해주세요.");
        }
        if (page != null) {
            return PageRequest.of(page, size);
        }
        return Pageable.unpaged();
    }
}
