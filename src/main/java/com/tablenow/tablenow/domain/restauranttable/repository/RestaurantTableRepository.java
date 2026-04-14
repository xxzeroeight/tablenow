package com.tablenow.tablenow.domain.restauranttable.repository;

import com.tablenow.tablenow.domain.restauranttable.entity.RestaurantTable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface RestaurantTableRepository extends JpaRepository<RestaurantTable, UUID>
{
}
