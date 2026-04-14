package com.tablenow.tablenow.domain.review.entity;

import com.tablenow.tablenow.domain.reservation.entity.Reservation;
import com.tablenow.tablenow.domain.user.entity.User;
import com.tablenow.tablenow.global.common.BaseUpdatableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
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
@Table(name = "reviews")
public class Review extends BaseUpdatableEntity
{
    @Column(name = "rating", nullable = false)
    private int rating;

    @Column(name = "content", length = 100, nullable = false)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = true, referencedColumnName = "id", foreignKey = @ForeignKey(name = "fk_reviews_user_id"))
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private User user;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id", nullable = false, unique = true, referencedColumnName = "id", foreignKey = @ForeignKey(name = "fk_reviews_reservation_id"))
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Reservation reservation;

    @Builder
    private Review(int rating, String content, User user, Reservation reservation) {
        this.rating = rating;
        this.content = content;
        this.user = user;
        this.reservation = reservation;
    }
}
