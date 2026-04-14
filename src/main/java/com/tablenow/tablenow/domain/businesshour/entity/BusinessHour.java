package com.tablenow.tablenow.domain.businesshour.entity;

import com.tablenow.tablenow.domain.restaurant.entity.Restaurant;
import com.tablenow.tablenow.global.common.BaseUpdatableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "business_hours",
        uniqueConstraints = @UniqueConstraint(
                name = "uq_business_hours_restaurant_day_slot",
                columnNames = {"restaurant_id", "day_of_week", "slot_order"}
        )
)
public class BusinessHour extends BaseUpdatableEntity
{
    @Enumerated(EnumType.STRING)
    @Column(name = "day_of_week", length = 10, nullable = false)
    private DayOfWeek dayOfWeek;

    @Column(name = "slot_order", nullable = false)
    private short slotOrder = 1;

    @Column(name = "open_time", nullable = false)
    private LocalTime openTime;

    @Column(name = "close_time", nullable = false)
    private LocalTime closeTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id", nullable = false, referencedColumnName = "id", foreignKey = @ForeignKey(name = "fk_business_hours_restaurant_id"))
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Restaurant restaurant;

    @Builder
    protected BusinessHour(DayOfWeek dayOfWeek, short slotOrder, LocalTime openTime, LocalTime closeTime, Restaurant restaurant) {
        this.dayOfWeek = dayOfWeek;
        this.slotOrder = slotOrder;
        this.openTime = openTime;
        this.closeTime = closeTime;
        this.restaurant = restaurant;
    }
}
