package com.tablenow.tablenow.domain.alarm.entity;

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

import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "alarms")
public class Alarm extends BaseUpdatableEntity
{
    @Enumerated(EnumType.STRING)
    @Column(name = "type", length = 20, nullable = false)
    private AlarmType type;

    @Column(name = "type_id", nullable = false)
    private UUID typeId;

    @Column(name = "is_read", nullable = false)
    private boolean read = false;

    @Column(name = "content", length = 100, nullable = false)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, referencedColumnName = "id", foreignKey = @ForeignKey(name = "fk_alarms_user_id"))
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;

    @Builder
    protected Alarm(AlarmType type, UUID typeId, boolean read, String content, User user) {
        this.type = type;
        this.typeId = typeId;
        this.read = read;
        this.content = content;
        this.user = user;
    }
}
