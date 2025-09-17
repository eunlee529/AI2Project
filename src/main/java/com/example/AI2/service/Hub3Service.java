package com.example.AI2.service;

import com.example.AI2.entity.Hub3Entity;
import com.example.AI2.repository.Hub3Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class Hub3Service {

    @Autowired
    private Hub3Repository hub3Repository;

    // Oracle 11g용 Native SQL 페이징
    public List<Hub3Entity> getHub3EntitiesWithPagination(String keyword, int page, int size) {
        int startRow = page * size + 1;  // 시작 ROW
        int endRow = (page + 1) * size;  // 끝 ROW

        if (keyword == null || keyword.isEmpty()) {
            return hub3Repository.findAllWithPagination(startRow, endRow);
        } else {
            return hub3Repository.findAllByKeywordWithPagination(keyword, startRow, endRow);
        }
    }

    public int countByKeyword(String keyword) {
        if (keyword == null || keyword.isEmpty()) {
            return hub3Repository.countAll();
        } else {
            return hub3Repository.countByKeyword(keyword);
        }
    }
}


