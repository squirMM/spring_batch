package com.example.SpringBatchTutorial.core.domain.order;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * OrderRepository
 *
 * @author squirMM
 * @date 2023/12/27
 */
public interface OrdersRepository extends JpaRepository<Orders, Integer> {
}
