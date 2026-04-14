CREATE TABLE users
(
    id             UUID        PRIMARY KEY NOT NULL,
    name           VARCHAR(20)             NOT NULL,
    email          VARCHAR(50)             NOT NULL UNIQUE,
    username       VARCHAR(20)             NOT NULL UNIQUE,
    password       VARCHAR(255)            NOT NULL,
    phone_number   VARCHAR(15)             NOT NULL UNIQUE,
    role           VARCHAR(20)             NOT NULL DEFAULT 'USER',
    created_at     TIMESTAMPTZ             NOT NULL DEFAULT NOW(),
    updated_at     TIMESTAMPTZ             NOT NULL DEFAULT NOW(),

    CONSTRAINT ck_users_role CHECK (role IN ('USER', 'OWNER', 'ADMIN'))
);

CREATE TABLE categories
(
    id         UUID        PRIMARY KEY NOT NULL,
    name       VARCHAR(20)             NOT NULL UNIQUE,
    created_at TIMESTAMPTZ             NOT NULL DEFAULT NOW()
);

CREATE TABLE restaurants
(
    id             UUID        PRIMARY KEY NOT NULL,
    name           VARCHAR(50)             NOT NULL,
    description    VARCHAR(255)            NOT NULL,
    address        VARCHAR(100)            NOT NULL,
    address_detail VARCHAR(100)            NOT NULL,
    created_at     TIMESTAMPTZ             NOT NULL DEFAULT NOW(),
    updated_at     TIMESTAMPTZ             NOT NULL DEFAULT NOW(),
    user_id        UUID                    NULL,
    category_id    UUID                    NULL,

    CONSTRAINT fk_restaurants_user_id     FOREIGN KEY (user_id)     REFERENCES users      (id) ON DELETE SET NULL,
    CONSTRAINT fk_restaurants_category_id FOREIGN KEY (category_id) REFERENCES categories (id) ON DELETE SET NULL
);

CREATE TABLE reservations
(
    id               UUID        PRIMARY KEY NOT NULL,
    reservation_date DATE                    NOT NULL,
    reservation_time TIME                    NOT NULL,
    guest_count      INTEGER                 NOT NULL,
    status           VARCHAR(20)             NOT NULL,
    created_at       TIMESTAMPTZ             NOT NULL DEFAULT NOW(),
    updated_at       TIMESTAMPTZ             NOT NULL DEFAULT NOW(),
    user_id          UUID                    NULL,
    restaurant_id    UUID                    NOT NULL,

    CONSTRAINT uq_reservations_restaurant_date_time UNIQUE (restaurant_id, reservation_date, reservation_time),
    CONSTRAINT ck_reservations_status               CHECK  (status IN ('CONFIRMED', 'CANCELLED', 'NO_SHOW')),

    CONSTRAINT fk_reservations_user_id       FOREIGN KEY (user_id)       REFERENCES users       (id) ON DELETE SET NULL,
    CONSTRAINT fk_reservations_restaurant_id FOREIGN KEY (restaurant_id) REFERENCES restaurants (id) ON DELETE CASCADE
);

CREATE TABLE menus
(
    id            UUID        PRIMARY KEY NOT NULL,
    name          VARCHAR(30)             NOT NULL,
    price         INTEGER                 NOT NULL,
    description   VARCHAR(100)            NOT NULL,
    s3_key        VARCHAR(50)             NOT NULL,
    status        VARCHAR(10)             NOT NULL,
    created_at    TIMESTAMPTZ             NOT NULL DEFAULT NOW(),
    updated_at    TIMESTAMPTZ             NOT NULL DEFAULT NOW(),
    restaurant_id UUID                    NOT NULL,

    CONSTRAINT ck_menus_status        CHECK (status IN ('PREPARING', 'ON_SALE', 'SOLD_OUT')),

    CONSTRAINT fk_menus_restaurant_id FOREIGN KEY (restaurant_id) REFERENCES restaurants (id) ON DELETE CASCADE
);

CREATE TABLE reviews
(
    id             UUID        PRIMARY KEY NOT NULL,
    rating         INTEGER                 NOT NULL,
    content        VARCHAR(100)            NOT NULL,
    created_at     TIMESTAMPTZ             NOT NULL DEFAULT NOW(),
    updated_at     TIMESTAMPTZ             NOT NULL DEFAULT NOW(),
    user_id        UUID                    NULL,
    reservation_id UUID                    NULL,
    restaurant_id  UUID                    NOT NULL,

    CONSTRAINT ck_reviews_rating             CHECK (rating >= 1 AND rating <= 5),

    CONSTRAINT fk_reviews_user_id        FOREIGN KEY (user_id)        REFERENCES users        (id) ON DELETE SET NULL,
    CONSTRAINT fk_reviews_reservation_id FOREIGN KEY (reservation_id) REFERENCES reservations (id) ON DELETE SET NULL,
    CONSTRAINT fk_reviews_restaurant_id  FOREIGN KEY (restaurant_id)  REFERENCES restaurants  (id) ON DELETE CASCADE
);

CREATE TABLE waitings
(
    id            UUID        PRIMARY KEY NOT NULL,
    status        VARCHAR(20)             NOT NULL,
    guest_count   INTEGER                 NOT NULL,
    created_at    TIMESTAMPTZ             NOT NULL DEFAULT NOW(),
    updated_at    TIMESTAMPTZ             NOT NULL DEFAULT NOW(),
    user_id       UUID                    NOT NULL,
    restaurant_id UUID                    NOT NULL,

    CONSTRAINT ck_waitings_status            CHECK (status IN ('WAITING', 'ENTERED', 'CANCELLED', 'EXPIRED')),

    CONSTRAINT fk_waitings_user_id       FOREIGN KEY (user_id)       REFERENCES users       (id) ON DELETE CASCADE,
    CONSTRAINT fk_waitings_restaurant_id FOREIGN KEY (restaurant_id) REFERENCES restaurants (id) ON DELETE CASCADE
);

CREATE TABLE restaurant_tables
(
    id            UUID        PRIMARY KEY NOT NULL,
    capacity      INTEGER                 NOT NULL,
    created_at    TIMESTAMPTZ             NOT NULL DEFAULT NOW(),
    updated_at    TIMESTAMPTZ             NOT NULL DEFAULT NOW(),
    restaurant_id UUID                    NOT NULL,

    CONSTRAINT fk_restaurant_tables_restaurant_id FOREIGN KEY (restaurant_id) REFERENCES restaurants (id) ON DELETE CASCADE
);

CREATE TABLE business_hours
(
    id            UUID        PRIMARY KEY NOT NULL,
    day_of_week   VARCHAR(10)             NOT NULL,
    slot_order    SMALLINT                NOT NULL DEFAULT 1,
    open_time     TIME                    NOT NULL,
    close_time    TIME                    NOT NULL,
    created_at    TIMESTAMPTZ             NOT NULL DEFAULT NOW(),
    updated_at    TIMESTAMPTZ             NOT NULL DEFAULT NOW(),
    restaurant_id UUID                    NOT NULL,

    CONSTRAINT uq_business_hours_restaurant_day_slot UNIQUE (restaurant_id, day_of_week, slot_order),
    CONSTRAINT ck_business_hours_time_range          CHECK  (close_time > open_time),

    CONSTRAINT fk_business_hours_restaurant_id FOREIGN KEY (restaurant_id) REFERENCES restaurants (id) ON DELETE CASCADE
);

CREATE TABLE restaurant_bookmarks
(
    id            UUID        PRIMARY KEY NOT NULL,
    created_at    TIMESTAMPTZ             NOT NULL DEFAULT NOW(),
    user_id       UUID                    NOT NULL,
    restaurant_id UUID                    NOT NULL,

    CONSTRAINT uq_restaurant_bookmarks_user_restaurant UNIQUE (user_id, restaurant_id),

    CONSTRAINT fk_restaurant_bookmarks_user_id       FOREIGN KEY (user_id)       REFERENCES users       (id) ON DELETE CASCADE,
    CONSTRAINT fk_restaurant_bookmarks_restaurant_id FOREIGN KEY (restaurant_id) REFERENCES restaurants (id) ON DELETE CASCADE
);

CREATE TABLE review_attachments
(
    id        UUID         PRIMARY KEY NOT NULL,
    s3_key    VARCHAR(50)              NOT NULL,
    created_at TIMESTAMPTZ             NOT NULL DEFAULT NOW(),
    review_id UUID                     NOT NULL,

    CONSTRAINT fk_review_attachments_review_id FOREIGN KEY (review_id) REFERENCES reviews (id) ON DELETE CASCADE
);

CREATE TABLE restaurant_attachments
(
    id            UUID        PRIMARY KEY NOT NULL,
    s3_key        VARCHAR(50)             NOT NULL,
    created_at    TIMESTAMPTZ             NOT NULL DEFAULT NOW(),
    restaurant_id UUID                    NOT NULL,

    CONSTRAINT fk_restaurant_attachments_restaurant_id FOREIGN KEY (restaurant_id) REFERENCES restaurants (id) ON DELETE CASCADE
);

CREATE TABLE alarms
(
    id         UUID        PRIMARY KEY NOT NULL,
    type       VARCHAR(20)             NOT NULL,
    type_id    UUID                    NOT NULL,
    is_read    BOOLEAN                 NOT NULL DEFAULT false,
    content    VARCHAR(100)            NOT NULL,
    created_at TIMESTAMPTZ             NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ             NOT NULL DEFAULT NOW(),
    user_id    UUID                    NOT NULL,

    CONSTRAINT fk_alarms_user_id FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);