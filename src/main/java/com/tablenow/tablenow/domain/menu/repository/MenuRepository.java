package com.tablenow.tablenow.domain.menu.repository;

import com.tablenow.tablenow.domain.menu.entity.Menu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface MenuRepository extends JpaRepository<Menu, UUID>
{
}
