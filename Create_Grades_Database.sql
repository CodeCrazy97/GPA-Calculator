CREATE DATABASE COLLEGE;

USE COLLEGE;

CREATE TABLE GRADES (
	ID INT AUTO_INCREMENT PRIMARY KEY,
	GRADE DOUBLE NOT NULL,
	WEIGHT DOUBLE NOT NULL,
	DESCRIPTION VARCHAR(100) NOT NULL,
	CLASS VARCHAR(6) NOT NULL,
	SEMESTER VARCHAR(11) NOT NULL
);