CREATE DATABASE thuctap;
GO

USE thuctap;
GO

CREATE TABLE projects (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    name NVARCHAR(150) NOT NULL,
    status NVARCHAR(20) NOT NULL DEFAULT 'PLANNING'
);
GO

CREATE TABLE users (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    username NVARCHAR(50) NOT NULL UNIQUE,
    email NVARCHAR(100) NOT NULL UNIQUE,
    password NVARCHAR(255) NOT NULL,
    active BIT NOT NULL DEFAULT 1,
    created_at DATETIME2 NOT NULL DEFAULT GETDATE(),
    project_id BIGINT NULL,
    CONSTRAINT FK_users_project FOREIGN KEY (project_id) REFERENCES projects(id)
);
GO

CREATE TABLE tasks (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    title NVARCHAR(200) NOT NULL,
    status NVARCHAR(20) NOT NULL DEFAULT 'TODO',
    project_id BIGINT NOT NULL,
    assigned_user_id BIGINT NULL,
    CONSTRAINT FK_tasks_project FOREIGN KEY (project_id) REFERENCES projects(id),
    CONSTRAINT FK_tasks_user FOREIGN KEY (assigned_user_id) REFERENCES users(id)
);
GO

ALTER TABLE projects
ADD CONSTRAINT chk_project_status 
CHECK (status IN ('PLANNING', 'ACTIVE', 'FINISHED', 'ARCHIVED'));
GO

ALTER TABLE tasks
ADD CONSTRAINT chk_task_status 
CHECK (status IN ('TODO', 'IN_PROGRESS', 'DONE', 'CANCELLED'));
GO

ALTER TABLE tasks
ADD CONSTRAINT uq_task_title_project 
UNIQUE (title, project_id);
GO

INSERT INTO projects (name, status) VALUES 
('E-Commerce Website', 'ACTIVE'),
('Mobile Banking App', 'PLANNING'),
('AI Research Lab', 'ACTIVE'),
('Cloud Migration Project', 'ARCHIVED'),
('Internal HR Portal', 'FINISHED');
GO

INSERT INTO users (username, email, password, project_id) VALUES 
('alice_smith', 'alice@company.com', 'pass123', 4),
('bob_johnson', 'bob@company.com', 'pass123', 4),
('charlie_brown', 'charlie@company.com', 'pass123', 5),
('david_miller', 'david@company.com', 'pass123', 5),
('eve_wilson', 'eve@company.com', 'pass123', 6),
('frank_white', 'frank@company.com', 'pass123', 6),
('grace_lee', 'grace@company.com', 'pass123', 7),
('hannah_kim', 'hannah@company.com', 'pass123', 7),
('ian_wright', 'ian@company.com', 'pass123', 8),
('jack_sparrow', 'jack@company.com', 'pass123', 8),
('kelly_clark', 'kelly@company.com', 'pass123', 1),
('liam_neeson', 'liam@company.com', 'pass123', 2);
GO

INSERT INTO tasks (title, status, project_id, assigned_user_id) VALUES 
('Setup Database Schema', 'DONE', 4, 4),
('Implement Login API', 'IN_PROGRESS', 4, 4),
('Frontend UI Layout', 'TODO', 4, 5),
('Bug fix: Payment gateway', 'DONE', 4, 5),
('Design System Architecture', 'IN_PROGRESS', 5, 6),
('Setup CI/CD Pipeline', 'TODO', 5, 7),
('Data Collection for AI', 'ACTIVE', 6, 8),
('Model Training Phase 1', 'TODO', 6, 9),
('Analyze Legacy Code', 'DONE', 7, 10),
('Backup S3 Buckets', 'CANCELLED', 7, 11),
('Employee Benefits Module', 'DONE', 8, 12),
('Payroll Integration', 'TODO', 8, 13),
('Unit Testing Suite', 'IN_PROGRESS', 1, 1),
('API Documentation', 'DONE', 1, 2),
('Performance Tuning', 'TODO', 2, 3),
('Client Meeting Prep', 'TODO', 3, NULL),
('Security Audit', 'IN_PROGRESS', 4, 5),
('Refactoring Utils', 'DONE', 5, 6),
('User Interview', 'TODO', 6, 8),
('Server Maintenance', 'DONE', 7, 11);
GO

INSERT INTO user_roles (user_id, role_id)
SELECT u.id, (SELECT id FROM roles WHERE name = 'ROLE_MANAGER')
FROM users u WHERE u.username IN ('alice_smith', 'eve_wilson', 'ian_wright');

INSERT INTO user_roles (user_id, role_id)
SELECT u.id, (SELECT id FROM roles WHERE name = 'ROLE_USER')
FROM users u WHERE u.username NOT IN ('kokomu1', 'alice_smith', 'eve_wilson', 'ian_wright');
GO

DROP TABLE IF EXISTS user_roles;
DROP TABLE IF EXISTS roles;
GO

CREATE TABLE roles (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(255),
    created_at DATETIME2 DEFAULT GETDATE() 
);
GO

CREATE TABLE user_roles (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    assigned_at DATETIME2 DEFAULT GETDATE(), 
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
);
GO

INSERT INTO roles (name, description) VALUES 
('ROLE_USER', 'nguoi dung co the xem va sua task cua ho'),
('ROLE_MANAGER', 'quan ly co the xem va sua cua moi user');
GO

SELECT * FROM roles;
GO

INSERT INTO user_roles (user_id, role_id) 
SELECT u.id, r.id 
FROM users u, roles r 
WHERE u.username = 'kokomu1' AND r.name = 'ROLE_MANAGER';
GO

SELECT u.username, r.name as role_name
FROM users u
JOIN user_roles ur ON u.id = ur.user_id
JOIN roles r ON ur.role_id = r.id
ORDER BY u.username;
GO

SELECT id, username, email, password 
FROM users 
WHERE username = 'newuser';

SELECT id, username, email, password 
FROM users 
WHERE username = 'testuser2';

INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u, roles r
WHERE u.username = 'manager1' AND r.name = 'ROLE_MANAGER';

SELECT * FROM users
SELECT * FROM tasks
SELECT * FROM projects

UPDATE users
SET project_id = 4
WHERE id = 8;

SELECT * FROM users;
SELECT * FROM projects;
SELECT * FROM tasks;

SELECT COUNT(*) FROM users;
SELECT COUNT(*) FROM projects;
SELECT COUNT(*) FROM tasks;

ALTER TABLE projects
ADD CONSTRAINT chk_project_status
CHECK (status IN ('PLANNING', 'ACTIVE', 'FINISHED', 'ARCHIVED'));

ALTER TABLE tasks
ADD CONSTRAINT chk_task_status
CHECK (status IN ('TODO', 'IN_PROGRESS', 'DONE', 'CANCELLED'));

ALTER TABLE tasks
ADD CONSTRAINT uq_task_title_project
UNIQUE (title, project_id);

SELECT
    t.id,
    t.title,
    t.status,
    p.name AS project_name
FROM tasks t
JOIN users u ON t.assigned_user_id = u.id
JOIN projects p ON t.project_id = p.id
WHERE u.id = 1;

SELECT
    t.id,
    t.title,
    t.status,
    u.username AS assigned_user
FROM tasks t
LEFT JOIN users u ON t.assigned_user_id = u.id
WHERE t.project_id = 101;

SELECT
    id,
    title,
    status,
    project_id,
    assigned_user_id
FROM tasks
WHERE status = 'IN_PROGRESS';
