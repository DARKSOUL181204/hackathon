CREATE TABLE units (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    code VARCHAR(100) NOT NULL UNIQUE,
    location VARCHAR(255) NOT NULL,
    status VARCHAR(50) NOT NULL,
    created_at DATETIME,
    updated_at DATETIME
);

CREATE TABLE personnel (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    service_number VARCHAR(100) NOT NULL UNIQUE,
    full_name VARCHAR(255) NOT NULL,
    role VARCHAR(100) NOT NULL,
    unit_id BIGINT NOT NULL,
    posting VARCHAR(255) NOT NULL,
    availability_status VARCHAR(50) NOT NULL,
    contact_details VARCHAR(255),
    created_at DATETIME,
    updated_at DATETIME,
    FOREIGN KEY (unit_id) REFERENCES units(id)
);

CREATE TABLE assets (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    asset_code VARCHAR(100) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    category VARCHAR(50) NOT NULL,
    unit_id BIGINT NOT NULL,
    location VARCHAR(255) NOT NULL,
    operational_status VARCHAR(50) NOT NULL,
    condition_status VARCHAR(50) NOT NULL,
    created_at DATETIME,
    updated_at DATETIME,
    FOREIGN KEY (unit_id) REFERENCES units(id)
);

CREATE TABLE vehicles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    registration_number VARCHAR(100) NOT NULL UNIQUE,
    vehicle_type VARCHAR(50) NOT NULL,
    unit_id BIGINT NOT NULL,
    location VARCHAR(255) NOT NULL,
    operational_status VARCHAR(50) NOT NULL,
    condition_status VARCHAR(50) NOT NULL,
    driver_personnel_id BIGINT,
    created_at DATETIME,
    updated_at DATETIME,
    FOREIGN KEY (unit_id) REFERENCES units(id),
    FOREIGN KEY (driver_personnel_id) REFERENCES personnel(id)
);

CREATE TABLE maintenance_orders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    maintenance_code VARCHAR(100) NOT NULL UNIQUE,
    target_type VARCHAR(50) NOT NULL,
    asset_id BIGINT,
    vehicle_id BIGINT,
    description TEXT NOT NULL,
    priority VARCHAR(50) NOT NULL,
    scheduled_date DATETIME,
    started_at DATETIME,
    completed_at DATETIME,
    status VARCHAR(50) NOT NULL,
    created_at DATETIME,
    updated_at DATETIME,
    FOREIGN KEY (asset_id) REFERENCES assets(id),
    FOREIGN KEY (vehicle_id) REFERENCES vehicles(id),
    CONSTRAINT chk_target CHECK ( (asset_id IS NOT NULL AND vehicle_id IS NULL) OR (asset_id IS NULL AND vehicle_id IS NOT NULL) )
);
