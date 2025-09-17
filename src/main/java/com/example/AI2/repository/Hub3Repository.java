package com.example.AI2.repository;

import com.example.AI2.entity.Hub3Entity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface Hub3Repository extends JpaRepository<Hub3Entity, String> {

    // DOC_ID 기준 단건 조회
    Optional<Hub3Entity> findByDocId(String docId);

    // 전체 페이징 (키워드 없는 경우)
    @Query(value = "SELECT * FROM (" +
            " SELECT hubdata4.*, ROWNUM rnum " +
            " FROM (SELECT * FROM hubdata4 ORDER BY doc_id DESC) hubdata4 " +
            " WHERE ROWNUM <= :endRow" +
            ") WHERE rnum >= :startRow",
            nativeQuery = true)
    List<Hub3Entity> findAllWithPagination(@Param("startRow") int startRow,
                                           @Param("endRow") int endRow);

    // 키워드 검색 + 페이징
    @Query(value = "SELECT * FROM (" +
            " SELECT hubdata4.*, ROWNUM rnum " +
            " FROM (SELECT * FROM hubdata4 " +
            "       WHERE casenames LIKE '%' || :keyword || '%' " +
            "       ORDER BY doc_id DESC) hubdata4 " +
            " WHERE ROWNUM <= :endRow" +
            ") WHERE rnum >= :startRow",
            nativeQuery = true)
    List<Hub3Entity> findAllByKeywordWithPagination(@Param("keyword") String keyword,
                                                    @Param("startRow") int startRow,
                                                    @Param("endRow") int endRow);

    // 전체 카운트
    @Query(value = "SELECT COUNT(*) FROM hubdata4", nativeQuery = true)
    int countAll();

    // 키워드 카운트
    @Query(value = "SELECT COUNT(*) FROM hubdata4 " +
            "WHERE casenames LIKE '%' || :keyword || '%'", nativeQuery = true)
    int countByKeyword(@Param("keyword") String keyword);

    // 키워드 검색 (전체 결과)
    @Query(value = "SELECT * FROM hubdata4 " +
            "WHERE casenames LIKE '%' || :keyword || '%'", nativeQuery = true)
    List<Hub3Entity> findAllByKeyword(@Param("keyword") String keyword);
}
