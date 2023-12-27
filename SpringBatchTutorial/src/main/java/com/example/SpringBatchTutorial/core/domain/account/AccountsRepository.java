package com.example.SpringBatchTutorial.core.domain.account;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * AccountRepository
 *
 * @author squirMM
 * @date 2023/12/27
 */
public interface AccountsRepository extends JpaRepository<Accounts, Integer> {
}
