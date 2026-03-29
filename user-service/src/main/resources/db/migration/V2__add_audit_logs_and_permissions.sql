-- Create permissions table
CREATE TABLE permissions (
    id VARCHAR(36) PRIMARY KEY,
    name VARCHAR(100) UNIQUE NOT NULL,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create role_permissions junction table
CREATE TABLE role_permissions (
    role user_role NOT NULL,
    permission_id VARCHAR(36) NOT NULL,
    PRIMARY KEY (role, permission_id),
    FOREIGN KEY (permission_id) REFERENCES permissions(id) ON DELETE CASCADE
);

-- Create audit_logs table for tracking user activities
CREATE TABLE audit_logs (
    id VARCHAR(36) PRIMARY KEY,
    user_id VARCHAR(36),
    action VARCHAR(100) NOT NULL,
    resource_type VARCHAR(100),
    resource_id VARCHAR(36),
    details JSONB,
    ip_address INET,
    user_agent TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL
);

-- Create user_sessions table for tracking active sessions
CREATE TABLE user_sessions (
    id VARCHAR(36) PRIMARY KEY,
    user_id VARCHAR(36) NOT NULL,
    session_token VARCHAR(255) UNIQUE NOT NULL,
    ip_address INET,
    user_agent TEXT,
    device_info JSONB,
    login_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_activity TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    logout_time TIMESTAMP,
    expired BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Insert default permissions
INSERT INTO permissions (id, name, description) VALUES
    ('11111111-1111-1111-1111-111111111111', 'USER_CREATE', 'Create new users'),
    ('22222222-2222-2222-2222-222222222222', 'USER_READ', 'Read user information'),
    ('33333333-3333-3333-3333-333333333333', 'USER_UPDATE', 'Update user information'),
    ('44444444-4444-4444-4444-444444444444', 'USER_DELETE', 'Delete users'),
    ('55555555-5555-5555-5555-555555555555', 'USER_MANAGE_ROLES', 'Manage user roles'),
    ('66666666-6666-6666-6666-666666666666', 'PATIENT_CREATE', 'Create patient records'),
    ('77777777-7777-7777-7777-777777777777', 'PATIENT_READ', 'Read patient records'),
    ('88888888-8888-8888-8888-888888888888', 'PATIENT_UPDATE', 'Update patient records'),
    ('99999999-9999-9999-9999-999999999999', 'PATIENT_DELETE', 'Delete patient records'),
    ('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'APPOINTMENT_CREATE', 'Create appointments'),
    ('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 'APPOINTMENT_READ', 'Read appointments'),
    ('cccccccc-cccc-cccc-cccc-cccccccccccc', 'APPOINTMENT_UPDATE', 'Update appointments'),
    ('dddddddd-dddd-dddd-dddd-dddddddddddd', 'APPOINTMENT_DELETE', 'Delete appointments'),
    ('eeeeeeee-eeee-eeee-eeee-eeeeeeeeeeee', 'BILLING_CREATE', 'Create billing records'),
    ('ffffffff-ffff-ffff-ffff-ffffffffffff', 'BILLING_READ', 'Read billing records'),
    ('gggggggg-gggg-gggg-gggg-gggggggggggg', 'BILLING_UPDATE', 'Update billing records'),
    ('hhhhhhhh-hhhh-hhhh-hhhh-hhhhhhhhhhhh', 'BILLING_DELETE', 'Delete billing records'),
    ('iiiiiiii-iiii-iiii-iiii-iiiiiiiiiiii', 'MEDICAL_RECORD_CREATE', 'Create medical records'),
    ('jjjjjjjj-jjjj-jjjj-jjjj-jjjjjjjjjjjj', 'MEDICAL_RECORD_READ', 'Read medical records'),
    ('kkkkkkkk-kkkk-kkkk-kkkk-kkkkkkkkkkkk', 'MEDICAL_RECORD_UPDATE', 'Update medical records'),
    ('llllllll-llll-llll-llll-llllllllllll', 'MEDICAL_RECORD_DELETE', 'Delete medical records'),
    ('mmmmmmmm-mmmm-mmmm-mmmm-mmmmmmmmmmmm', 'PRESCRIPTION_CREATE', 'Create prescriptions'),
    ('nnnnnnnn-nnnn-nnnn-nnnn-nnnnnnnnnnnn', 'PRESCRIPTION_READ', 'Read prescriptions'),
    ('oooooooo-oooo-oooo-oooo-oooooooooooo', 'PRESCRIPTION_UPDATE', 'Update prescriptions'),
    ('pppppppp-pppp-pppp-pppp-pppppppppppp', 'PRESCRIPTION_DELETE', 'Delete prescriptions');

-- Assign permissions to ADMIN role
INSERT INTO role_permissions (role, permission_id) 
SELECT 'ADMIN', id FROM permissions;

-- Assign permissions to DOCTOR role
INSERT INTO role_permissions (role, permission_id) 
SELECT 'DOCTOR', id FROM permissions 
WHERE name LIKE 'PATIENT_%' 
   OR name LIKE 'APPOINTMENT_%' 
   OR name LIKE 'MEDICAL_RECORD_%' 
   OR name LIKE 'PRESCRIPTION_%';

-- Assign permissions to NURSE role
INSERT INTO role_permissions (role, permission_id) 
SELECT 'NURSE', id FROM permissions 
WHERE name LIKE 'PATIENT_READ' 
   OR name LIKE 'APPOINTMENT_READ' 
   OR name LIKE 'MEDICAL_RECORD_READ' 
   OR name LIKE 'PRESCRIPTION_READ';

-- Assign permissions to RECEPTIONIST role
INSERT INTO role_permissions (role, permission_id) 
SELECT 'RECEPTIONIST', id FROM permissions 
WHERE name LIKE 'PATIENT_CREATE' 
   OR name LIKE 'PATIENT_READ' 
   OR name LIKE 'PATIENT_UPDATE' 
   OR name LIKE 'APPOINTMENT_%';

-- Assign permissions to PHARMACIST role
INSERT INTO role_permissions (role, permission_id) 
SELECT 'PHARMACIST', id FROM permissions 
WHERE name LIKE 'PRESCRIPTION_%';

-- Assign permissions to LAB_TECHNICIAN role
INSERT INTO role_permissions (role, permission_id) 
SELECT 'LAB_TECHNICIAN', id FROM permissions 
WHERE name LIKE 'MEDICAL_RECORD_CREATE' 
   OR name LIKE 'MEDICAL_RECORD_READ' 
   OR name LIKE 'MEDICAL_RECORD_UPDATE';

-- Create indexes for better performance
CREATE INDEX idx_audit_logs_user_id ON audit_logs(user_id);
CREATE INDEX idx_audit_logs_created_at ON audit_logs(created_at);
CREATE INDEX idx_audit_logs_action ON audit_logs(action);
CREATE INDEX idx_user_sessions_user_id ON user_sessions(user_id);
CREATE INDEX idx_user_sessions_session_token ON user_sessions(session_token);
CREATE INDEX idx_user_sessions_last_activity ON user_sessions(last_activity);
CREATE INDEX idx_role_permissions_role ON role_permissions(role);
CREATE INDEX idx_role_permissions_permission_id ON role_permissions(permission_id);

-- Create function to clean up expired sessions
CREATE OR REPLACE FUNCTION cleanup_expired_sessions()
RETURNS void AS $$
BEGIN
    UPDATE user_sessions 
    SET expired = TRUE, logout_time = CURRENT_TIMESTAMP 
    WHERE last_activity < CURRENT_TIMESTAMP - INTERVAL '24 hours' 
      AND expired = FALSE;
END;
$$ language 'plpgsql';

-- Create function to log user activity
CREATE OR REPLACE FUNCTION log_user_activity(
    p_user_id VARCHAR(36),
    p_action VARCHAR(100),
    p_resource_type VARCHAR(100),
    p_resource_id VARCHAR(36),
    p_details JSONB,
    p_ip_address INET,
    p_user_agent TEXT
)
RETURNS VARCHAR(36) AS $$
DECLARE
    v_log_id VARCHAR(36);
BEGIN
    v_log_id := gen_random_uuid();
    
    INSERT INTO audit_logs (id, user_id, action, resource_type, resource_id, details, ip_address, user_agent)
    VALUES (v_log_id, p_user_id, p_action, p_resource_type, p_resource_id, p_details, p_ip_address, p_user_agent);
    
    RETURN v_log_id;
END;
$$ language 'plpgsql';

-- Create comments for documentation
COMMENT ON TABLE permissions IS 'Stores system permissions';
COMMENT ON COLUMN permissions.id IS 'Unique identifier for the permission (UUID)';
COMMENT ON COLUMN permissions.name IS 'Unique permission name';
COMMENT ON COLUMN permissions.description IS 'Description of what the permission allows';

COMMENT ON TABLE role_permissions IS 'Junction table for role-permission many-to-many relationship';
COMMENT ON COLUMN role_permissions.role IS 'Reference to user_role enum';
COMMENT ON COLUMN role_permissions.permission_id IS 'Reference to permissions.id';

COMMENT ON TABLE audit_logs IS 'Stores audit logs for user activities';
COMMENT ON COLUMN audit_logs.id IS 'Unique identifier for the audit log (UUID)';
COMMENT ON COLUMN audit_logs.user_id IS 'Reference to users.id (nullable for system actions)';
COMMENT ON COLUMN audit_logs.action IS 'Action performed (e.g., LOGIN, CREATE, UPDATE, DELETE)';
COMMENT ON COLUMN audit_logs.resource_type IS 'Type of resource affected (e.g., USER, PATIENT)';
COMMENT ON COLUMN audit_logs.resource_id IS 'ID of the resource affected';
COMMENT ON COLUMN audit_logs.details IS 'Additional details about the action in JSON format';
COMMENT ON COLUMN audit_logs.ip_address IS 'IP address from which the action was performed';
COMMENT ON COLUMN audit_logs.user_agent IS 'User agent string from the client';
COMMENT ON COLUMN audit_logs.created_at IS 'Timestamp when the action was performed';

COMMENT ON TABLE user_sessions IS 'Stores user session information';
COMMENT ON COLUMN user_sessions.id IS 'Unique identifier for the session (UUID)';
COMMENT ON COLUMN user_sessions.user_id IS 'Reference to users.id';
COMMENT ON COLUMN user_sessions.session_token IS 'Unique session token';
COMMENT ON COLUMN user_sessions.ip_address IS 'IP address from which the session was created';
COMMENT ON COLUMN user_sessions.user_agent IS 'User agent string from the client';
COMMENT ON COLUMN user_sessions.device_info IS 'Device information in JSON format';
COMMENT ON COLUMN user_sessions.login_time IS 'Timestamp when the user logged in';
COMMENT ON COLUMN user_sessions.last_activity IS 'Timestamp of last user activity';
COMMENT ON COLUMN user_sessions.logout_time IS 'Timestamp when the user logged out';
COMMENT ON COLUMN user_sessions.expired IS 'Whether the session has expired';