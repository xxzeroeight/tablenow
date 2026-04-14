package com.tablenow.tablenow.domain.alarm.repository;

import com.tablenow.tablenow.domain.alarm.entity.Alarm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AlarmRepository extends JpaRepository<Alarm, UUID>
{
}
