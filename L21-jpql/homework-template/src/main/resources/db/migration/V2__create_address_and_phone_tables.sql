CREATE TABLE address
(
    id        BIGSERIAL PRIMARY KEY,
    street    VARCHAR(255),
    client_id BIGINT UNIQUE,
    CONSTRAINT fk_address_client FOREIGN KEY (client_id) REFERENCES client (id) ON DELETE CASCADE
);

CREATE TABLE phone
(
    id        BIGSERIAL PRIMARY KEY,
    number    VARCHAR(50),
    client_id BIGINT,
    CONSTRAINT fk_phone_client FOREIGN KEY (client_id) REFERENCES client (id) ON DELETE CASCADE
);