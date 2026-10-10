CREATE TABLE logistics_requests (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    request_number VARCHAR(100) NOT NULL UNIQUE,
    requesting_unit_id BIGINT NOT NULL,
    requested_by VARCHAR(255) NOT NULL,
    purpose TEXT NOT NULL,
    priority VARCHAR(50) NOT NULL,
    required_by_date DATETIME,
    delivery_location VARCHAR(255),
    status VARCHAR(50) NOT NULL,
    created_at DATETIME,
    updated_at DATETIME
);

CREATE TABLE request_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    request_id BIGINT NOT NULL,
    resource_type VARCHAR(50) NOT NULL,
    description VARCHAR(255) NOT NULL,
    quantity_requested DECIMAL(19, 2) NOT NULL,
    unit_of_measure VARCHAR(50),
    specifications TEXT,
    FOREIGN KEY (request_id) REFERENCES logistics_requests(id)
);

CREATE TABLE approval_records (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    request_id BIGINT NOT NULL,
    approver_id VARCHAR(100) NOT NULL,
    decision VARCHAR(50) NOT NULL,
    comments TEXT,
    decided_at DATETIME,
    FOREIGN KEY (request_id) REFERENCES logistics_requests(id)
);

CREATE TABLE resource_allocations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    request_item_id BIGINT NOT NULL,
    resource_type VARCHAR(50) NOT NULL,
    resource_id BIGINT,
    supply_item_id BIGINT,
    quantity_allocated DECIMAL(19, 2) NOT NULL,
    status VARCHAR(50) NOT NULL,
    allocated_at DATETIME,
    released_at DATETIME,
    allocated_by VARCHAR(100),
    FOREIGN KEY (request_item_id) REFERENCES request_items(id),
    CONSTRAINT chk_allocation_ref CHECK ( (resource_id IS NOT NULL AND supply_item_id IS NULL) OR (resource_id IS NULL AND supply_item_id IS NOT NULL) )
);

CREATE TABLE request_status_history (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    request_id BIGINT NOT NULL,
    previous_status VARCHAR(50),
    new_status VARCHAR(50) NOT NULL,
    changed_by VARCHAR(100) NOT NULL,
    reason TEXT,
    changed_at DATETIME,
    FOREIGN KEY (request_id) REFERENCES logistics_requests(id)
);
