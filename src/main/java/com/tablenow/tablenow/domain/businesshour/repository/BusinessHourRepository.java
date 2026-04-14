package com.tablenow.tablenow.domain.businesshour.repository;

import com.tablenow.tablenow.domain.businesshour.entity.BusinessHour;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface BusinessHourRepository extends JpaRepository<BusinessHour, UUID>
{
}
