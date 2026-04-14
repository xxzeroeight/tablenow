package com.tablenow.tablenow.domain.waiting.entity;

import com.tablenow.tablenow.domain.restaurant.entity.Restaurant;
import com.tablenow.tablenow.domain.user.entity.User;
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
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "waitings")
public class Waiting extends BaseUpdatableEntity
{
    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20, nullable = false)
    private WaitingStatus status;

    @Column(name = "guest_count", nullable = false)
    private int guestCount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, referencedColumnName = "id", foreignKey = @ForeignKey(name = "fk_waitings_user_id"))
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id", nullable = false, referencedColumnName = "id", foreignKey = @ForeignKey(name = "fk_waitings_restaurant_id"))
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Restaurant restaurant;

    @Builder
    protected Waiting(WaitingStatus status, int guestCount, User user, Restaurant restaurant) {
        this.status = status;
        this.guestCount = guestCount;
        this.user = user;
        this.restaurant = restaurant;
    }
}
