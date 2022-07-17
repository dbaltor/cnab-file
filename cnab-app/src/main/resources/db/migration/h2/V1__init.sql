CREATE TABLE shop_db (
    id BIGINT IDENTITY PRIMARY KEY,
    name VARCHAR(19) NOT NULL UNIQUE,
    owner VARCHAR(14) NOT NULL
);

CREATE TABLE transaction_type_db (
    id BIGINT IDENTITY PRIMARY KEY,
    type INT NOT NULL UNIQUE,
    description VARCHAR(50) NOT NULL,
    nature VARCHAR(5) NOT NULL
);

CREATE TABLE transaction_db (
    id BIGINT IDENTITY PRIMARY KEY,
    type_id BIGINT NOT NULL,
    foreign key(type_id) references transaction_type_db(id),
    value NUMERIC(10, 2) NOT NULL,
    recipient_cpf VARCHAR(11) NOT NULL,
    card_number VARCHAR(16) NOT NULL,
    date DATE NOT NULL,
    time VARCHAR(20) NOT NULL,
    shop_id BIGINT NOT NULL,
    foreign key(shop_id) references shop_db(id)
);
