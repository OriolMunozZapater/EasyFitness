-- Insert para el Usuario Repositorio
INSERT INTO usuario (userID, correo, password, is_first_login) VALUES
(0, 'repositorio@tuapp.com', 'password_segura', FALSE);
UPDATE `easyfitness`.`usuario` SET `userID` = '0' WHERE (`userID` = '1');

-- Inserts para Ejercicios
INSERT INTO ejercicio (nombre, descripcion, tipo, grupo_muscular, userID, rutinaID) VALUES
('Sentadillas', 'Ejercicio completo para piernas', 'Fuerza', 'Piernas', 0, 0),
('Press de banca', 'Ejercicio para pecho', 'Fuerza', 'Pecho', 0, 0),
('Pull-up', 'Ejercicio de espalda superior', 'Calistenia', 'Espalda', 0, 0),
('Jogging', 'Correr a un ritmo moderado', 'Cardio', 'Todo el cuerpo', 0, 0),
('Yoga', 'Sesión de yoga para mejorar flexibilidad', 'Flexibilidad', 'Todo el cuerpo', 0, 0),
('Tai Chi', 'Formas lentas y meditativas', 'Equilibrio', 'Todo el cuerpo', 0, 0);

-- Inserts para Rutinas
INSERT INTO rutina (nombre, descripcion, userID, publico) VALUES
('Rutina de Fuerza', 'Rutina completa de fuerza para todo el cuerpo', 0, TRUE),
('Rutina de Calistenia', 'Rutina usando peso corporal', 0, TRUE),
('Rutina de Cardio', 'Rutina para mejorar la capacidad cardiovascular', 0, TRUE),
('Rutina de Flexibilidad', 'Mejora la flexibilidad general', 0, TRUE),
('Rutina de Equilibrio', 'Fomenta el equilibrio y la serenidad', 0, TRUE);

-- Inserts para Series
INSERT INTO serie (ejercicioID, n_repeticiones, peso, tipo) VALUES
(1, 10, 50, 'Normal'),
(2, 8, 70, 'Normal'),
(3, 5, 0, 'Normal'),
(4, 30, 0, 'Minutos'),
(5, 60, 0, 'Minutos'),
(6, 20, 0, 'Minutos');

-- Inserts para Rutina Ejercicio
-- Relaciones para Rutina de Fuerza
INSERT INTO rutina_ejercicio (rutinaID, ejercicioID, orden) VALUES
(1, 1, 1),
(1, 2, 2),
(1, 3, 3);

-- Relaciones para Rutina de Calistenia
INSERT INTO rutina_ejercicio (rutinaID, ejercicioID, orden) VALUES
(2, 3, 1),
(2, 1, 2);

-- Relaciones para Rutina de Cardio
INSERT INTO rutina_ejercicio (rutinaID, ejercicioID, orden) VALUES
(3, 4, 1);

-- Relaciones para Rutina de Flexibilidad
INSERT INTO rutina_ejercicio (rutinaID, ejercicioID, orden) VALUES
(4, 5, 1);

-- Relaciones para Rutina de Equilibrio
INSERT INTO rutina_ejercicio (rutinaID, ejercicioID, orden) VALUES
(5, 6, 1);
