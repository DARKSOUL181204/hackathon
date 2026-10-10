CREATE TABLE user_accounts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    full_name VARCHAR(255) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(50) NULL,
    account_status VARCHAR(50) NOT NULL,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL
);

CREATE TABLE access_requests (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_account_id BIGINT NOT NULL,
    requested_role VARCHAR(50) NOT NULL,
    request_details VARCHAR(500) NULL,
    status VARCHAR(50) NOT NULL,
    submitted_at DATETIME NOT NULL,
    decided_at DATETIME NULL,
    decided_by VARCHAR(100) NULL,
    rejection_reason VARCHAR(500) NULL,
    CONSTRAINT fk_access_requests_user FOREIGN KEY (user_account_id) REFERENCES user_accounts(id),
    INDEX idx_access_requests_status (status),
    INDEX idx_access_requests_user (user_account_id)
);
