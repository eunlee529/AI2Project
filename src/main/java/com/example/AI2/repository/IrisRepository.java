package com.example.AI2.repository;

import com.example.AI2.entity.IrisEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IrisRepository extends JpaRepository<IrisEntity, Long> {
}
