-- V2__Insert_Sample_Data.sql
-- Insert sample departments
INSERT INTO departments (name, code, description) VALUES
('Computer Science', 'CS', 'Department of Computer Science and Engineering'),
('Mathematics', 'MATH', 'Department of Mathematics'),
('Physics', 'PHY', 'Department of Physics'),
('Electronics', 'ECE', 'Department of Electronics and Communication');

-- Insert admin user (password: password)
-- BCrypt hash for 'password' with cost 10
INSERT INTO users (email, password, first_name, last_name, role, enabled) VALUES
('admin@school.com', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', 'Admin', 'User', 'ADMIN', TRUE);

-- Insert sample teachers (password: password)
INSERT INTO users (email, password, first_name, last_name, role, enabled) VALUES
('john.doe@school.com', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', 'John', 'Doe', 'TEACHER', TRUE),
('jane.smith@school.com', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', 'Jane', 'Smith', 'TEACHER', TRUE);

-- Insert teacher profiles
INSERT INTO teachers (teacher_id, user_id, department_id, phone, qualification, specialization, joining_year) VALUES
('TCH20260001', (SELECT id FROM users WHERE email = 'john.doe@school.com'), 1, '1234567890', 'Ph.D. Computer Science', 'Machine Learning', 2020),
('TCH20260002', (SELECT id FROM users WHERE email = 'jane.smith@school.com'), 2, '0987654321', 'Ph.D. Mathematics', 'Applied Mathematics', 2019);

-- Insert sample courses
INSERT INTO courses (code, name, description, credits, department_id, teacher_id) VALUES
('CS101', 'Introduction to Programming', 'Basic programming concepts using Java', 4, 1, 1),
('CS201', 'Data Structures', 'Arrays, Linked Lists, Trees, Graphs', 4, 1, 1),
('CS301', 'Database Management', 'SQL, NoSQL, Database Design', 3, 1, NULL),
('MATH101', 'Calculus I', 'Limits, Derivatives, Integrals', 4, 2, 2),
('MATH201', 'Linear Algebra', 'Vectors, Matrices, Linear Transformations', 3, 2, 2),
('PHY101', 'Physics I', 'Mechanics and Thermodynamics', 4, 3, NULL);

-- Insert sample students (password: password)
INSERT INTO users (email, password, first_name, last_name, role, enabled) VALUES
('alice.johnson@school.com', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', 'Alice', 'Johnson', 'STUDENT', TRUE),
('bob.wilson@school.com', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', 'Bob', 'Wilson', 'STUDENT', TRUE),
('charlie.brown@school.com', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', 'Charlie', 'Brown', 'STUDENT', TRUE);

-- Insert student profiles
INSERT INTO students (student_id, user_id, department_id, phone, address, enrollment_year) VALUES
('STU20260001', (SELECT id FROM users WHERE email = 'alice.johnson@school.com'), 1, '1112223333', '123 Main St', 2024),
('STU20260002', (SELECT id FROM users WHERE email = 'bob.wilson@school.com'), 1, '4445556666', '456 Oak Ave', 2024),
('STU20260003', (SELECT id FROM users WHERE email = 'charlie.brown@school.com'), 2, '7778889999', '789 Pine Rd', 2023);

-- Enroll students in courses
INSERT INTO student_courses (student_id, course_id) VALUES
(1, 1), -- Alice in CS101
(1, 2), -- Alice in CS201
(1, 4), -- Alice in MATH101
(2, 1), -- Bob in CS101
(2, 3), -- Bob in CS301
(3, 4), -- Charlie in MATH101
(3, 5); -- Charlie in MATH201
