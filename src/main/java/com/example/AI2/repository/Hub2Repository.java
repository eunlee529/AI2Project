package com.example.AI2.repository;

import com.example.AI2.entity.Hub2Entity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface Hub2Repository extends JpaRepository<Hub2Entity, Long> {

    // ✅ 키워드 검색 + 페이징 (Oracle 11g ROWNUM 방식)
    @Query(value = "SELECT * FROM (" +
            " SELECT hub2.*, ROWNUM rnum " +
            " FROM (SELECT * FROM hubdata2 " +
            "       WHERE question LIKE '%' || :keyword || '%' " +
            "       ORDER BY num DESC) hub2 " +
            " WHERE ROWNUM <= :endRow" +
            ") WHERE rnum >= :startRow",
            nativeQuery = true)
    List<Hub2Entity> findAllByKeywordWithPagination(@Param("keyword") String keyword,
                                                    @Param("startRow") int startRow,
                                                    @Param("endRow") int endRow);

    // ✅ 키워드 카운트
    @Query(value = "SELECT COUNT(*) FROM hubdata2 " +
            "WHERE question LIKE '%' || :keyword || '%'", nativeQuery = true)
    int countByKeyword(@Param("keyword") String keyword);
}

