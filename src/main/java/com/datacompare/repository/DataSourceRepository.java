package com.datacompare.repository;

import com.datacompare.entity.DataSource;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DataSourceRepository extends JpaRepository<DataSource, Long> {
    boolean existsByName(String name);
} 