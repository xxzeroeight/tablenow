CREATE TABLE refresh_tokens
(
    id          UUID         PRIMARY KEY NOT NULL,
    token       TEXT                     NOT NULL UNIQUE,
    user_id     UUID                     NOT NULL UNIQUE,
    created_at  TIMESTAMPTZ              NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_refresh_tokens_user_id FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);