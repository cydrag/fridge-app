CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE "user" (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE "fridge" (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(255) NOT NULL,
    owner_id UUID NOT NULL REFERENCES "user"(id),
    type VARCHAR(50) NOT NULL DEFAULT 'PRIVATE',
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE "fridge_membership" (
    fridge_id UUID NOT NULL REFERENCES "fridge"(id) ON DELETE CASCADE,
    user_id UUID NOT NULL REFERENCES "user"(id) ON DELETE CASCADE,
    joined_at TIMESTAMP NOT NULL DEFAULT NOW(),
    PRIMARY KEY (fridge_id, user_id)
);

CREATE TABLE "fridge_item" (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    fridge_id UUID NOT NULL REFERENCES "fridge"(id) ON DELETE CASCADE,
    product_name VARCHAR(255) NOT NULL,
    stored_at TIMESTAMP NOT NULL DEFAULT NOW(),
    best_before DATE NOT NULL,
    version BIGINT NOT NULL DEFAULT 1
);

CREATE TABLE "refresh_token" (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL REFERENCES "user"(id) ON DELETE CASCADE,
    token VARCHAR(255) NOT NULL UNIQUE,
    expiry_date TIMESTAMP NOT NULL,
    created_at TIMESTAMP DEFAULT NOW()
);
