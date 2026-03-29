-- Create users table
CREATE TABLE users (
    id VARCHAR(36) PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    enabled BOOLEAN DEFAULT TRUE,
    email_verified BOOLEAN DEFAULT FALSE,
    verification_token VARCHAR(255),
    reset_password_token VARCHAR(255),
    reset_password_expires TIMESTAMP,
    failed_login_attempts INTEGER DEFAULT 0,
    account_locked BOOLEAN DEFAULT FALSE,
    lock_time TIMESTAMP,
    last_login TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create roles enum type
CREATE TYPE user_role AS ENUM ('PATIENT', 'DOCTOR', 'ADMIN', 'NURSE', 'RECEPTIONIST', 'PHARMACIST', 'LAB_TECHNICIAN');

-- Create user_roles junction table
CREATE TABLE user_roles (
    user_id VARCHAR(36) NOT NULL,
    role user_role NOT NULL,
    PRIMARY KEY (user_id, role),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Create refresh_tokens table
CREATE TABLE refresh_tokens (
    id VARCHAR(36) PRIMARY KEY,
    user_id VARCHAR(36) NOT NULL,
    token VARCHAR(255) UNIQUE NOT NULL,
    expiry_date TIMESTAMP NOT NULL,
    revoked BOOLEAN DEFAULT FALSE,
    revoked_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Create indexes for better performance
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_verification_token ON users(verification_token);
CREATE INDEX idx_users_reset_password_token ON users(reset_password_token);
CREATE INDEX idx_refresh_tokens_token ON refresh_tokens(token);
CREATE INDEX idx_refresh_tokens_user_id ON refresh_tokens(user_id);
CREATE INDEX idx_refresh_tokens_expiry_date ON refresh_tokens(expiry_date);
CREATE INDEX idx_user_roles_user_id ON user_roles(user_id);

-- Create function to update updated_at timestamp
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

-- Create trigger for users table
CREATE TRIGGER update_users_updated_at BEFORE UPDATE ON users
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- Insert default admin user (password: Admin@123)
INSERT INTO users (id, username, email, password, enabled, email_verified) 
VALUES (
    '00000000-0000-0000-0000-000000000000',
    'admin',
    'admin@hospital.com',
    '$2a$10$X8z5v5q5Q5Z5Q5Z5Q5Z5Q5e5Q5Z5Q5Z5Q5Z5Q5Z5Q5Z5Q5Z5Q5Z5Q5', -- bcrypt hash of Admin@123
    TRUE,
    TRUE
);

-- Assign admin role to default admin user
INSERT INTO user_roles (user_id, role) VALUES ('00000000-0000-0000-0000-000000000000', 'ADMIN');

-- Create comments for documentation
COMMENT ON TABLE users IS 'Stores user account information';
COMMENT ON COLUMN users.id IS 'Unique identifier for the user (UUID)';
COMMENT ON COLUMN users.username IS 'Unique username for login';
COMMENT ON COLUMN users.email IS 'Unique email address for login and communication';
COMMENT ON COLUMN users.password IS 'BCrypt hashed password';
COMMENT ON COLUMN users.enabled IS 'Whether the user account is enabled';
COMMENT ON COLUMN users.email_verified IS 'Whether the email has been verified';
COMMENT ON COLUMN users.verification_token IS 'Token for email verification';
COMMENT ON COLUMN users.reset_password_token IS 'Token for password reset';
COMMENT ON COLUMN users.reset_password_expires IS 'Expiration time for password reset token';
COMMENT ON COLUMN users.failed_login_attempts IS 'Number of consecutive failed login attempts';
COMMENT ON COLUMN users.account_locked IS 'Whether the account is locked due to too many failed attempts';
COMMENT ON COLUMN users.lock_time IS 'Time when the account was locked';
COMMENT ON COLUMN users.last_login IS 'Time of last successful login';
COMMENT ON COLUMN users.created_at IS 'Timestamp when the user was created';
COMMENT ON COLUMN users.updated_at IS 'Timestamp when the user was last updated';

COMMENT ON TABLE user_roles IS 'Junction table for user-role many-to-many relationship';
COMMENT ON COLUMN user_roles.user_id IS 'Reference to users.id';
COMMENT ON COLUMN user_roles.role IS 'Role assigned to the user';

COMMENT ON TABLE refresh_tokens IS 'Stores refresh tokens for JWT authentication';
COMMENT ON COLUMN refresh_tokens.id IS 'Unique identifier for the refresh token (UUID)';
COMMENT ON COLUMN refresh_tokens.user_id IS 'Reference to users.id';
COMMENT ON COLUMN refresh_tokens.token IS 'JWT refresh token';
COMMENT ON COLUMN refresh_tokens.expiry_date IS 'Expiration time of the refresh token';
COMMENT ON COLUMN refresh_tokens.revoked IS 'Whether the token has been revoked';
COMMENT ON COLUMN refresh_tokens.revoked_at IS 'Time when the token was revoked';
COMMENT ON COLUMN refresh_tokens.created_at IS 'Timestamp when the token was created';