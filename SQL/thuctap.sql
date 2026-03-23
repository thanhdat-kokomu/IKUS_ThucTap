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
('Project 1', 'ACTIVE'),
('Project 2', 'PLANNING'),
('Project 3', 'FINISHED');
GO

INSERT INTO users (username, email, password, project_id) VALUES 
('kokomu1', 'kokomu1@gmail.com', '123456', 1),
('kokomu2', 'kokomu2@gmail.com', '123456', 1),
('kokomu3', 'kokomu3@gmail.com', '123456', 2);
GO

INSERT INTO tasks (title, status, project_id, assigned_user_id) VALUES 
('Task 1', 'TODO', 1, 1),
('Task 2', 'IN_PROGRESS', 1, 1),
('Task 3', 'DONE', 1, 2),
('Task 4', 'TODO', 2, 3);
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