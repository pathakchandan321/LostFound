CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    phone VARCHAR(255),
    password VARCHAR(255) NOT NULL,
    role VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS lost_item (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    item_name VARCHAR(255),
    description VARCHAR(1000),
    location VARCHAR(255),
    date_lost DATE,
    image_path VARCHAR(255),
    status VARCHAR(255),
    reporter_id BIGINT,
    CONSTRAINT fk_lost_reporter FOREIGN KEY (reporter_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS found_item (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    item_name VARCHAR(255),
    description VARCHAR(1000),
    location VARCHAR(255),
    date_found DATE,
    image_path VARCHAR(255),
    status VARCHAR(255),
    reporter_id BIGINT,
    CONSTRAINT fk_found_reporter FOREIGN KEY (reporter_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS claim (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    lost_item_id BIGINT,
    found_item_id BIGINT,
    claimant_id BIGINT,
    message VARCHAR(1000),
    status VARCHAR(255),
    claimed_at DATETIME,
    CONSTRAINT fk_claim_lost FOREIGN KEY (lost_item_id) REFERENCES lost_item(id),
    CONSTRAINT fk_claim_found FOREIGN KEY (found_item_id) REFERENCES found_item(id),
    CONSTRAINT fk_claim_user FOREIGN KEY (claimant_id) REFERENCES users(id)
);
