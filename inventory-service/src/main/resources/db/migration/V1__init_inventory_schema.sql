CREATE TABLE supply_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    item_code VARCHAR(100) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    category VARCHAR(100) NOT NULL,
    unit_of_measure VARCHAR(50) NOT NULL,
    description TEXT,
    reorder_level DECIMAL(19, 2) NOT NULL DEFAULT 0.00,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at DATETIME,
    updated_at DATETIME
);

CREATE TABLE inventory_stock (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    supply_item_id BIGINT NOT NULL,
    location VARCHAR(255) NOT NULL,
    quantity_on_hand DECIMAL(19, 2) NOT NULL DEFAULT 0.00,
    version INT NOT NULL DEFAULT 0,
    updated_at DATETIME,
    FOREIGN KEY (supply_item_id) REFERENCES supply_items(id),
    UNIQUE KEY uk_stock_location (supply_item_id, location)
);

CREATE TABLE stock_movements (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    supply_item_id BIGINT NOT NULL,
    location VARCHAR(255) NOT NULL,
    movement_type VARCHAR(50) NOT NULL,
    quantity DECIMAL(19, 2) NOT NULL,
    reference VARCHAR(255),
    remarks TEXT,
    created_at DATETIME,
    FOREIGN KEY (supply_item_id) REFERENCES supply_items(id)
);

CREATE TABLE issue_records (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    supply_item_id BIGINT NOT NULL,
    location VARCHAR(255) NOT NULL,
    request_id BIGINT NOT NULL,
    quantity_issued DECIMAL(19, 2) NOT NULL,
    issued_to VARCHAR(255) NOT NULL,
    issued_at DATETIME,
    remarks TEXT,
    FOREIGN KEY (supply_item_id) REFERENCES supply_items(id)
);
