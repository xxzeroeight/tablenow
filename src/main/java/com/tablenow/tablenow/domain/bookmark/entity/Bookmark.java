package com.tablenow.tablenow.domain.bookmark.entity;

import com.tablenow.tablenow.domain.restaurant.entity.Restaurant;
import com.tablenow.tablenow.domain.user.entity.User;
import com.tablenow.tablenow.global.common.BaseEntity;
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
@Table(name = "restaurant_bookmarks")
public class Bookmark extends BaseEntity
{
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, referencedColumnName = "id", foreignKey = @ForeignKey(name = "fk_restaurant_bookmarks_user_id"))
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id", nullable = false, referencedColumnName = "id", foreignKey = @ForeignKey(name = "fk_restaurant_bookmarks_restaurant_id"))
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Restaurant restaurant;

    @Builder
    protected Bookmark(User user, Restaurant restaurant) {
        this.user = user;
        this.restaurant = restaurant;
    }
}
