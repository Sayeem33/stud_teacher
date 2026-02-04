-- V3__Remove_Admin_Role.sql
-- Remove admin user since we're removing the ADMIN role from the system
-- Now only TEACHER and STUDENT roles exist

DELETE FROM users WHERE email = 'admin@school.com';
