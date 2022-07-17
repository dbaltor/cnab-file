CREATE TABLE IF NOT EXISTS shop_db (
    id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(19) NOT NULL UNIQUE,
    owner VARCHAR(14) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS transaction_type_db (
    id BIGINT NOT NULL AUTO_INCREMENT,
    type INT NOT NULL UNIQUE,
    description VARCHAR(50) NOT NULL,
    nature VARCHAR(5) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS transaction_db (
    id BIGINT NOT NULL AUTO_INCREMENT,
    type_id BIGINT NOT NULL,
    value NUMERIC(10, 2) NOT NULL,
    recipient_cpf VARCHAR(11) NOT NULL,
    card_number VARCHAR(16) NOT NULL,
    date DATE NOT NULL,
    time VARCHAR(20) NOT NULL,
    shop_id BIGINT NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (type_id)
        REFERENCES transaction_type_db(id)
        ON UPDATE CASCADE,
    FOREIGN KEY (shop_id)
        REFERENCES shop_db(id)
        ON UPDATE CASCADE
);
