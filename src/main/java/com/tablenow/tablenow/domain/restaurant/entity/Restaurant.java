package com.tablenow.tablenow.domain.restaurant.entity;

import com.tablenow.tablenow.domain.category.entity.Category;
import com.tablenow.tablenow.domain.user.entity.User;
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
@Table(name = "restaurants")
public class Restaurant extends BaseUpdatableEntity
{
    @Column(name = "name", length = 50, nullable = false)
    private String name;

    @Column(name = "description", length = 255, nullable = false)
    private String description;

    @Column(name = "address", length = 100, nullable = false)
    private String address;

    @Column(name = "address_detail", length = 100, nullable = false)
    private String addressDetail;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = true, referencedColumnName = "id", foreignKey = @ForeignKey(name = "fk_restaurants_user_id"))
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = true, referencedColumnName = "id", foreignKey = @ForeignKey(name = "fk_restaurants_category_id"))
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private Category category;

    @Builder
    protected Restaurant(String name, String description, String address, String addressDetail, User user, Category category) {
        this.name = name;
        this.description = description;
        this.address = address;
        this.addressDetail = addressDetail;
        this.user = user;
        this.category = category;
    }

    public void update(String name, String description, String address, String addressDetail, Category category) {
        if (name != null) this.name = name;
        if (description != null) this.description = description;
        if (address != null) this.address = address;
        if (addressDetail != null) this.addressDetail = addressDetail;
        if (category != null) this.category = category;
    }
}
