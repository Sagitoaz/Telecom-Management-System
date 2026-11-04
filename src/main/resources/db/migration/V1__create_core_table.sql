CREATE TABLE customer (
    id CHAR(36) CHARACTER SET ascii COLLATE ascii_bin NOT NULL,
    full_name VARCHAR(200) NOT NULL,
    birth_date DATE NOT NULL,
    address TEXT NULL,
    occupation VARCHAR(120) NULL,
    contact VARCHAR(120) NULL,
    created_at DATETIME(6) NOT NULL,
    CONSTRAINT pk_customer PRIMARY KEY (id)
);

CREATE TABLE verified_document (
    id CHAR(36) CHARACTER SET ascii COLLATE ascii_bin NOT NULL,
    customer_id CHAR(36) CHARACTER SET ascii COLLATE ascii_bin NOT NULL,
    document_type VARCHAR(12) NOT NULL,
    normalized_number VARCHAR(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_bin NOT NULL,
    verification_ref VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_bin NOT NULL,
    verified_at DATETIME(6) NOT NULL,
    created_at DATETIME(6) NOT NULL,
    CONSTRAINT pk_verified_document PRIMARY KEY (id),
    CONSTRAINT fk_verified_document_customer FOREIGN KEY (customer_id) REFERENCES customer (id) ON DELETE RESTRICT ON UPDATE RESTRICT,
    CONSTRAINT ck_verified_document_type CHECK ( document_type in ('CCCD', 'CMND', 'PASSPORT') ),
    INDEX ix_verified_document_customer (customer_id)
);

CREATE TABLE phone_number_inventory (
    id CHAR(36) CHARACTER SET ascii COLLATE ascii_bin NOT NULL,
    msisdn VARCHAR(16) CHARACTER SET ascii COLLATE ascii_bin NOT NULL,
    status VARCHAR(12) CHARACTER SET ascii COLLATE ascii_bin NOT NULL,
    random_key DOUBLE NOT NULL,
    created_at DATETIME(6) NOT NULL,
    assigned_at DATETIME(6) NULL,
    CONSTRAINT pk_phone_number_inventory PRIMARY KEY (id),
    CONSTRAINT uq_phone_number_inventory_msisdn UNIQUE (msisdn),
    CONSTRAINT ck_phone_number_inventory_status CHECK (status IN ('AVAILABLE','ASSIGNED')),
    CONSTRAINT ck_phone_number_inventory_random_key CHECK (random_key >= 0 AND random_key < 1),
    INDEX ix_phone_number_inventory_available (status, random_key, id)
);

CREATE TABLE subscriber (
    id CHAR(36) CHARACTER SET ascii COLLATE ascii_bin NOT NULL,
    customer_id CHAR(36) CHARACTER SET ascii COLLATE ascii_bin NOT NULL,
    phone_number_id CHAR(36) CHARACTER SET ascii COLLATE ascii_bin NOT NULL,
    status VARCHAR(16) CHARACTER SET ascii COLLATE ascii_bin NOT NULL,
    activated_at DATETIME(6) NOT NULL,
    CONSTRAINT pk_subscriber PRIMARY KEY (id),
    CONSTRAINT uq_subscriber_phone_number UNIQUE (phone_number_id),
    CONSTRAINT fk_subscriber_customer FOREIGN KEY (customer_id) REFERENCES customer (id) ON DELETE RESTRICT ON UPDATE RESTRICT,
    CONSTRAINT fk_subscriber_phone_number FOREIGN KEY (phone_number_id) REFERENCES phone_number_inventory (id) ON DELETE RESTRICT ON UPDATE RESTRICT,
    CONSTRAINT ck_subscriber_status CHECK (status IN ('ACTIVE','EXPIRED')),
    INDEX ix_subscriber_customer (customer_id, id)
);

CREATE TABLE service_account (
    id CHAR(36) CHARACTER SET ascii COLLATE ascii_bin NOT NULL,
    subscriber_id CHAR(36) CHARACTER SET ascii COLLATE ascii_bin NOT NULL,
    account_type VARCHAR(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_bin NOT NULL,
    balance_vnd BIGINT NOT NULL DEFAULT 0,
    expires_at DATETIME(6) NULL,
    status VARCHAR(16) CHARACTER SET ascii COLLATE ascii_bin NOT NULL,
    version BIGINT NOT NULL DEFAULT 0,
    created_at DATETIME(6) NOT NULL,
    CONSTRAINT pk_service_account PRIMARY KEY (id),
    CONSTRAINT uq_service_account_subscriber_type UNIQUE (subscriber_id, account_type),
    CONSTRAINT fk_service_account_subscriber FOREIGN KEY (subscriber_id) REFERENCES subscriber (id) ON DELETE RESTRICT ON UPDATE RESTRICT,
    CONSTRAINT ck_service_account_status CHECK (status IN ('ACTIVE','EXPIRED')),
    CONSTRAINT ck_service_account_balance CHECK (balance_vnd >= 0),
    CONSTRAINT ck_service_account_version CHECK (version >= 0),
    INDEX ix_service_account_expiry (status, expires_at, id)
);

CREATE TABLE service_package (
    id CHAR(36) CHARACTER SET ascii COLLATE ascii_bin NOT NULL,
    code VARCHAR(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_bin NOT NULL,
    name VARCHAR(200) NOT NULL,
    catalog_version BIGINT NOT NULL,
    price_vnd BIGINT NOT NULL,
    duration_days INT NOT NULL,
    sale_status VARCHAR(16) CHARACTER SET ascii COLLATE ascii_bin NOT NULL,
    grandfather_renewal BOOLEAN NOT NULL,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    CONSTRAINT pk_service_package PRIMARY KEY (id),
    CONSTRAINT uq_service_package_code UNIQUE (code),
    CONSTRAINT ck_service_package_price CHECK (price_vnd > 0),
    CONSTRAINT ck_service_package_duration CHECK (duration_days > 0),
    CONSTRAINT ck_service_package_catalog_version CHECK (catalog_version > 0),
    CONSTRAINT ck_service_package_status CHECK ( sale_status IN ('ON_SALE','DISCONTINUED') )
);

CREATE TABLE package_subscription (
    id CHAR(36) CHARACTER SET ascii COLLATE ascii_bin NOT NULL,
    subscriber_id CHAR(36) CHARACTER SET ascii COLLATE ascii_bin NOT NULL,
    package_id CHAR(36) CHARACTER SET ascii COLLATE ascii_bin NOT NULL,
    version BIGINT NOT NULL DEFAULT 0,
    state VARCHAR(20) CHARACTER SET ascii COLLATE ascii_bin NOT NULL,
    coverage_end DATETIME(6) NULL,
    auto_renew BOOLEAN NOT NULL DEFAULT TRUE,
    cancel_effective_at DATETIME(6) NULL,
    cancel_accepted_at DATETIME(6) NULL,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    CONSTRAINT pk_package_subscription PRIMARY KEY (id),
    CONSTRAINT uq_package_subscription_subscriber_package UNIQUE (subscriber_id, package_id),
    CONSTRAINT fk_package_subscription_subscriber FOREIGN KEY (subscriber_id) REFERENCES subscriber (id) ON DELETE RESTRICT ON UPDATE RESTRICT,
    CONSTRAINT fk_package_subscription_package FOREIGN KEY (package_id) REFERENCES service_package (id) ON DELETE RESTRICT ON UPDATE RESTRICT,
    CONSTRAINT ck_package_subscription_version CHECK (version >= 0),
    CONSTRAINT ck_package_subscription_state CHECK ( state IN ('EMPTY','ACTIVE','PENDING_CANCEL','CANCELLED','EXPIRED') ),
    INDEX ix_package_subscription_coverage (state, coverage_end, id)
);

CREATE TABLE operation_request (
    id CHAR(36) CHARACTER SET ascii COLLATE ascii_bin NOT NULL,
    caller_id VARCHAR(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_bin NOT NULL,
    request_id VARCHAR(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_bin NOT NULL,
    operation VARCHAR(24) CHARACTER SET ascii COLLATE ascii_bin NOT NULL,
    fingerprint CHAR(64) CHARACTER SET ascii COLLATE ascii_bin NOT NULL,
    canonical_payload JSON NULL,
    status VARCHAR(12) CHARACTER SET ascii COLLATE ascii_bin NOT NULL,
    received_at DATETIME(6) NOT NULL,
    eligibility_checked_at DATETIME(6) NULL,
    completed_at DATETIME(6) NULL,
    replay_until DATETIME(6) NULL,
    result_code VARCHAR(64) NULL,
    result_summary JSON NULL,
    response_payload JSON NULL,
    lease_owner VARCHAR(128) NULL,
    lease_until DATETIME(6) NULL,
    retry_at DATETIME(6) NULL,
    fence BIGINT NOT NULL DEFAULT 0,
    attempt_count INT NOT NULL DEFAULT 0,
    CONSTRAINT pk_operation_request PRIMARY KEY (id),
    CONSTRAINT uq_operation_request_caller_op_request UNIQUE (caller_id, operation, request_id),
    CONSTRAINT ck_operation_request_operation CHECK ( operation IN ('REGISTER_PACKAGE','CANCEL_PACKAGE','CREDIT','DEBIT') ),
    CONSTRAINT ck_operation_request_status CHECK (status IN ('PROCESSING','SUCCEEDED','FAILED')),
    CONSTRAINT ck_operation_request_fence CHECK (fence >= 0),
    CONSTRAINT ck_operation_request_attempt_count CHECK (attempt_count >= 0),
    INDEX ix_operation_request_recovery (status, retry_at, lease_until, id)
);

CREATE TABLE financial_transaction (
    id CHAR(36) CHARACTER SET ascii COLLATE ascii_bin NOT NULL,
    operation_request_id CHAR(36) CHARACTER SET ascii COLLATE ascii_bin NULL,
    account_id CHAR(36) CHARACTER SET ascii COLLATE ascii_bin NOT NULL,
    subscriber_id CHAR(36) CHARACTER SET ascii COLLATE ascii_bin NOT NULL,
    subscription_id CHAR(36) CHARACTER SET ascii COLLATE ascii_bin NULL,
    subscription_cycle_id CHAR(36) CHARACTER SET ascii COLLATE ascii_bin NULL,
    source_type VARCHAR(24) CHARACTER SET ascii COLLATE ascii_bin NOT NULL,
    direction VARCHAR(6) CHARACTER SET ascii COLLATE ascii_bin NOT NULL,
    amount_vnd BIGINT NOT NULL,
    balance_before BIGINT NOT NULL,
    balance_after BIGINT NOT NULL,
    recorded_at DATETIME(6) NOT NULL,
    eligibility_checked_at DATETIME(6) NOT NULL,
    logical_key VARCHAR(160) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_bin NOT NULL,
    CONSTRAINT pk_financial_transaction PRIMARY KEY (id),
    CONSTRAINT uq_financial_transaction_operation UNIQUE (operation_request_id),
    CONSTRAINT uq_financial_transaction_logical_key UNIQUE (logical_key),
    CONSTRAINT fk_financial_transaction_operation FOREIGN KEY (operation_request_id) REFERENCES operation_request (id) ON DELETE RESTRICT ON UPDATE RESTRICT,
    CONSTRAINT fk_financial_transaction_account FOREIGN KEY (account_id) REFERENCES service_account (id) ON DELETE RESTRICT ON UPDATE RESTRICT,
    CONSTRAINT fk_financial_transaction_subscriber FOREIGN KEY (subscriber_id) REFERENCES subscriber (id) ON DELETE RESTRICT ON UPDATE RESTRICT,
    CONSTRAINT fk_financial_transaction_subscription FOREIGN KEY (subscription_id) REFERENCES package_subscription (id) ON DELETE RESTRICT ON UPDATE RESTRICT,
    CONSTRAINT ck_financial_transaction_source CHECK (source_type IN ('CREDIT_TOPUP','SERVICE_CONSUMPTION','PACKAGE_FEE','AUTO_RENEWAL_FEE')),
    CONSTRAINT ck_financial_transaction_direction CHECK (direction IN ('CREDIT','DEBIT')),
    CONSTRAINT ck_financial_transaction_amount CHECK (amount_vnd > 0),
    CONSTRAINT ck_financial_transaction_balances CHECK (balance_before >= 0 AND balance_after >= 0),
    CONSTRAINT ck_financial_transaction_arithmetic CHECK (
        (direction = 'CREDIT' AND balance_after = balance_before + amount_vnd)
        OR (direction = 'DEBIT' AND balance_after = balance_before - amount_vnd)
    ),
    CONSTRAINT ck_financial_transaction_source_shape CHECK (
        (source_type = 'CREDIT_TOPUP' AND direction = 'CREDIT' AND subscription_id IS NULL AND subscription_cycle_id IS NULL)
        OR (source_type = 'SERVICE_CONSUMPTION' AND direction = 'DEBIT' AND subscription_id IS NOT NULL AND subscription_cycle_id IS NOT NULL)
        OR (source_type IN ('PACKAGE_FEE','AUTO_RENEWAL_FEE') AND direction = 'DEBIT' AND subscription_id IS NOT NULL AND subscription_cycle_id IS NULL)
    ),
    INDEX ix_financial_transaction_subscriber_time (subscriber_id, recorded_at, id),
    INDEX ix_financial_transaction_account_time (account_id, recorded_at, id),
    INDEX ix_financial_transaction_time (recorded_at, id),
    INDEX ix_financial_transaction_cycle (subscription_cycle_id)
);

CREATE TABLE subscription_cycle (
    id CHAR(36) CHARACTER SET ascii COLLATE ascii_bin NOT NULL,
    subscription_id CHAR(36) CHARACTER SET ascii COLLATE ascii_bin NOT NULL,
    cycle_key VARCHAR(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_bin NOT NULL,
    starts_at DATETIME(6) NOT NULL,
    ends_at DATETIME(6) NOT NULL,
    price_vnd BIGINT NOT NULL,
    duration_days INT NOT NULL,
    catalog_version BIGINT NOT NULL,
    fee_transaction_id CHAR(36) CHARACTER SET ascii COLLATE ascii_bin NOT NULL,
    created_at DATETIME(6) NOT NULL,
    CONSTRAINT pk_subscription_cycle PRIMARY KEY (id),
    CONSTRAINT uq_subscription_cycle_key UNIQUE (subscription_id, cycle_key),
    CONSTRAINT uq_subscription_cycle_fee UNIQUE (fee_transaction_id),
    CONSTRAINT fk_subscription_cycle_subscription FOREIGN KEY (subscription_id) REFERENCES package_subscription (id) ON DELETE RESTRICT ON UPDATE RESTRICT,
    CONSTRAINT fk_subscription_cycle_fee FOREIGN KEY (fee_transaction_id) REFERENCES financial_transaction (id) ON DELETE RESTRICT ON UPDATE RESTRICT,
    CONSTRAINT ck_subscription_cycle_period CHECK (ends_at > starts_at),
    CONSTRAINT ck_subscription_cycle_price CHECK (price_vnd > 0),
    CONSTRAINT ck_subscription_cycle_duration CHECK (duration_days > 0),
    CONSTRAINT ck_subscription_cycle_catalog_version CHECK (catalog_version > 0),
    INDEX ix_subscription_cycle_period (subscription_id, starts_at, ends_at)
);

ALTER TABLE financial_transaction
    ADD CONSTRAINT fk_financial_transaction_cycle FOREIGN KEY (subscription_cycle_id) REFERENCES subscription_cycle (id) ON DELETE RESTRICT ON UPDATE RESTRICT;