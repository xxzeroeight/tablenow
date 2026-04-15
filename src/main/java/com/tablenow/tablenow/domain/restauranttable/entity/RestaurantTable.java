package com.tablenow.tablenow.domain.restauranttable.entity;

import com.tablenow.tablenow.domain.restaurant.entity.Restaurant;
import com.tablenow.tablenow.global.common.BaseUpdatableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "restaurant_tables")
public class RestaurantTable extends BaseUpdatableEntity
{
    @Column(name = "capacity", nullable = false)
    private int capacity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id", nullable = false, referencedColumnName = "id", foreignKey = @ForeignKey(name = "fk_restaurant_tables_restaurant_id"))
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Restaurant restaurant;

    @Builder
    public RestaurantTable(int capacity, Restaurant restaurant) {
        this.capacity = capacity;
        this.restaurant = restaurant;
    }
}
