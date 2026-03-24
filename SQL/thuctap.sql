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
('Data Collection for AI', 'TODO', 6, 8),
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

INSERT INTO roles (name, description) VALUES 
('ROLE_USER', 'nguoi dung co the xem va sua task cua ho'),
('ROLE_MANAGER', 'quan ly co the xem va sua cua moi user');
GO

INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u, roles r
WHERE u.username IN ('alice_smith', 'eve_wilson', 'ian_wright') 
  AND r.name = 'ROLE_MANAGER';
GO

INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u, roles r
WHERE u.username NOT IN ('alice_smith', 'eve_wilson', 'ian_wright')
  AND r.name = 'ROLE_USER';
GO
