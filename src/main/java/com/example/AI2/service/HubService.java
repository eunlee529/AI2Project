package com.example.AI2.service;

import com.example.AI2.entity.HubEntity;
import com.example.AI2.repository.HubRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HubService {

    @Autowired
    private HubRepository hubRepository;

    // ✅ Oracle 11g용 페이징 처리
    public List<HubEntity> getHubEntitiesWithPagination(String keyword, int page, int size) {
        int startRow = page * size + 1;
        int endRow = (page + 1) * size;

        if (keyword == null || keyword.isEmpty()) {
            return hubRepository.findAllWithPagination(startRow, endRow);
        } else {
            return hubRepository.findAllByKeywordWithPagination(keyword, startRow, endRow);
        }
    }

    // ✅ 카운트
    public int countByKeyword(String keyword) {
        if (keyword == null || keyword.isEmpty()) {
            return hubRepository.countAll();
        } else {
            return hubRepository.countByKeyword(keyword);
        }
    }



}
