package com.tablenow.tablenow.domain.category.entity;

import com.tablenow.tablenow.global.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "categories")
public class Category extends BaseEntity
{
    @Column(name = "name", length = 20, nullable = false, unique = true)
    private String name;

    protected Category(String name) {
        this.name = name;
    }
}
