package com.example.AI2.repository;

import com.example.AI2.entity.HubEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HubRepository extends JpaRepository<HubEntity, String> {

    // ✅ 전체 데이터 개수
    @Query(value = "SELECT COUNT(*) FROM hubdata", nativeQuery = true)
    int countAll();

    // ✅ 키워드 검색 개수
    @Query(value = "SELECT COUNT(*) FROM hubdata WHERE content LIKE '%' || :keyword || '%'", nativeQuery = true)
    int countByKeyword(@Param("keyword") String keyword);

    // ✅ 전체 페이징 (Oracle 11g ROWNUM 방식)
    @Query(value = "SELECT * FROM (" +
            " SELECT a.*, ROWNUM rnum " +
            " FROM (SELECT * FROM hubdata ORDER BY c_id DESC) a " +
            " WHERE ROWNUM <= :endRow" +
            ") WHERE rnum >= :startRow",
            nativeQuery = true)
    List<HubEntity> findAllWithPagination(@Param("startRow") int startRow,
                                          @Param("endRow") int endRow);

    // ✅ 키워드 검색 + 페이징 (Oracle 11g ROWNUM 방식)
    @Query(value = "SELECT * FROM (" +
            " SELECT hub.*, ROWNUM rnum " +
            " FROM (SELECT * FROM hubdata " +
            "       WHERE content LIKE '%' || :keyword || '%' " +
            "       ORDER BY c_id DESC) hub " +
            " WHERE ROWNUM <= :endRow" +
            ") WHERE rnum >= :startRow",
            nativeQuery = true)
    List<HubEntity> findAllByKeywordWithPagination(@Param("keyword") String keyword,
                                                   @Param("startRow") int startRow,
                                                   @Param("endRow") int endRow);

    // ✅ 키워드 검색 (전체 결과, 페이징 X)
    @Query(value = "SELECT * FROM hubdata WHERE content LIKE '%' || :keyword || '%'", nativeQuery = true)
    List<HubEntity> findAllByKeyword(@Param("keyword") String keyword);
}
