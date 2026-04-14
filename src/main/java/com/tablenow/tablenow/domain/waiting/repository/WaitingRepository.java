package com.tablenow.tablenow.domain.waiting.repository;

import com.tablenow.tablenow.domain.waiting.entity.Waiting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface WaitingRepository extends JpaRepository<Waiting, UUID>
{
}
