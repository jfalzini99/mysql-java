DROP TABLE IF EXISTS project_category;
DROP TABLE IF EXISTS category;
DROP TABLE IF EXISTS step;
DROP TABLE IF EXISTS material;
DROP TABLE IF EXISTS project;

CREATE TABLE project (
	project_id INT AUTO_INCREMENT NOT NULL,
	project_name VARCHAR(128) NOT NULL,
	estimated_hours DECIMAL(7, 2), 
	actual_hours DECIMAL(7, 2),
	difficulty INT, 
	notes TEXT, 
	PRIMARY KEY (project_id)
);

CREATE TABLE material (
	material_id INT AUTO_INCREMENT NOT NULL,
	project_id INT NOT NULL,
	material_name VARCHAR(128) NOT NULL,
	num_required INT, 
	cost DECIMAL(7, 2),
	PRIMARY KEY (material_id),
	FOREIGN KEY (project_id) REFERENCES project (project_id) ON DELETE CASCADE
);

CREATE TABLE step (
	step_id INT AUTO_INCREMENT NOT NULL,
	project_id INT NOT NULL,
	step_text TEXT NOT NULL,
	step_order INT NOT NULL,
	PRIMARY KEY (step_id),
	FOREIGN KEY (project_id) REFERENCES project (project_id) ON DELETE CASCADE
);

CREATE TABLE category (
	category_id INT AUTO_INCREMENT NOT NULL,
	category_name VARCHAR(128) NOT NULL,
	PRIMARY KEY (category_id)
);

CREATE TABLE project_category (
	project_id INT NOT NULL,
	category_id INT NOT NULL,
	FOREIGN KEY (project_id) REFERENCES project (project_id) ON DELETE CASCADE,
	FOREIGN KEY (category_id) REFERENCES category (category_id) ON DELETE CASCADE,
	UNIQUE KEY (project_id, category_id)
);

-- Add some data
INSERT INTO project (project_name, estimated_hours, actual_hours, difficulty, notes) 
	VALUES ('Paint bedroom walls', 8, 6, 2, 'Buy blue paint and paint brushes');
INSERT INTO material (project_id, material_name, num_required, cost)
	VALUES (1, 'Blue paint', 3, 75);
INSERT INTO step (project_id, step_text, step_order)
 	VALUES (1, 'Cover the floor with plastic drop cloth and secure with masking tape', 1);
INSERT INTO category (category_name) VALUES ('Walls and Ceiling');