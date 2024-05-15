SET FOREIGN_KEY_CHECKS = 0; -- Desactiva la verificación de claves foráneas

DROP TABLE IF EXISTS rutina_ejercicio;
DROP TABLE IF EXISTS valoracion_ejercicio;
DROP TABLE IF EXISTS serie;
DROP TABLE IF EXISTS ejercicio;
DROP TABLE IF EXISTS usuarios_seguidos;
DROP TABLE IF EXISTS rutina;
DROP TABLE IF EXISTS usuario;
DROP TABLE IF EXISTS objetivo;
DROP TABLE IF EXISTS rutina_compartida;
DROP TABLE IF EXISTS registro;

SET FOREIGN_KEY_CHECKS = 1; -- Reactiva la verificación de claves foráneas

CREATE TABLE objetivo (
  objetivoID INT AUTO_INCREMENT PRIMARY KEY,
  peso_objetivo DECIMAL(5,2) NOT NULL,
  descripcion VARCHAR(255)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE usuario (
  userID INT AUTO_INCREMENT PRIMARY KEY,
  nombre VARCHAR(255),
  apellido VARCHAR(255),
  correo VARCHAR(255) UNIQUE NOT NULL,
  password VARCHAR(255) NOT NULL,
  sexo VARCHAR(10),
  peso_actual DECIMAL(5,2),
  altura INT,
  foto BLOB,
  descripcion VARCHAR(255),
  redes_sociales VARCHAR(255),
  tiempo_entrenamiento TIME,
  fecha_nacimiento DATE,
  objetivoID INT,
  is_first_login BOOLEAN DEFAULT TRUE,
  FOREIGN KEY (objetivoID) REFERENCES objetivo(objetivoID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE usuarios_seguidos (
  usuarioID INT NOT NULL,
  seguidoID INT NOT NULL,
  PRIMARY KEY (usuarioID, seguidoID),
  FOREIGN KEY (usuarioID) REFERENCES usuario(userID),
  FOREIGN KEY (seguidoID) REFERENCES usuario(userID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE ejercicio (
  ejercicioID INT AUTO_INCREMENT PRIMARY KEY,
  rutinaID INT,
  userID INT,
  nombre VARCHAR(255) NOT NULL,
  descripcion VARCHAR(255),
  tipo VARCHAR(50),
  valoracion DOUBLE,
  grupo_muscular VARCHAR(50),
  video BLOB
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE serie (
  serieID INT AUTO_INCREMENT PRIMARY KEY,
  ejercicioID INT NOT NULL,
  n_repeticiones INT,
  peso INT,
  comentario_serie VARCHAR(255),
  FOREIGN KEY (ejercicioID) REFERENCES ejercicio(ejercicioID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE valoracion_ejercicio (
  valoracionID INT AUTO_INCREMENT PRIMARY KEY,
  userID INT NOT NULL,
  ejercicioID INT NOT NULL,
  valoracion INT,
  comentario VARCHAR(255),
  FOREIGN KEY (userID) REFERENCES usuario(userID),
  FOREIGN KEY (ejercicioID) REFERENCES ejercicio(ejercicioID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE rutina (
  rutinaID INT AUTO_INCREMENT PRIMARY KEY,
  nombre VARCHAR(255) NOT NULL,
  descripcion VARCHAR(255),
  userID INT NOT NULL,
  publico BOOLEAN,
  FOREIGN KEY (userID) REFERENCES usuario(userID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE rutina_ejercicio (
  id INT AUTO_INCREMENT PRIMARY KEY,
  rutinaID INT NOT NULL,
  ejercicioID INT NOT NULL,
  orden INT NOT NULL,
  FOREIGN KEY (rutinaID) REFERENCES rutina(rutinaID) ON DELETE CASCADE,
  FOREIGN KEY (ejercicioID) REFERENCES ejercicio(ejercicioID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE rutina_compartida (
  rutinaID INT NOT NULL,
  userID INT NOT NULL,
  FOREIGN KEY (rutinaID) REFERENCES rutina(rutinaID),
  FOREIGN KEY (userID) REFERENCES usuario(userID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE registro (
  registroID INT AUTO_INCREMENT PRIMARY KEY,
  nombre_rutina VARCHAR(255),
  tiempo_tardado TIME,
  userID INT,
  FOREIGN KEY (userID) REFERENCES usuario(userID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Inserts para Usuario Repositorio
INSERT INTO usuario (correo, password, is_first_login) VALUES
('repositorio@tuapp.com', 'password_segura', FALSE);

-- Inserts para Ejercicios
INSERT INTO ejercicio (nombre, descripcion, tipo, grupo_muscular) VALUES
('Sentadillas', 'Ejercicio completo para piernas', 'Fuerza', 'Piernas'),
('Press de banca', 'Ejercicio para pecho', 'Fuerza', 'Pecho'),
('Pull-up', 'Ejercicio de espalda superior', 'Calistenia', 'Espalda'),
('Jogging', 'Correr a un ritmo moderado', 'Cardio', 'Todo el cuerpo'),
('Yoga', 'Sesión de yoga para mejorar flexibilidad', 'Flexibilidad', 'Todo el cuerpo'),
('Tai Chi', 'Formas lentas y meditativas', 'Equilibrio', 'Todo el cuerpo');

-- Inserts para Rutinas
INSERT INTO rutina (nombre, descripcion, userID, publico) VALUES
('Rutina de Fuerza', 'Rutina completa de fuerza para todo el cuerpo', 1, TRUE),
('Rutina de Calistenia', 'Rutina usando peso corporal', 1, TRUE),
('Rutina de Cardio', 'Rutina para mejorar la capacidad cardiovascular', 1, TRUE),
('Rutina de Flexibilidad', 'Mejora la flexibilidad general', 1, TRUE),
('Rutina de Equilibrio', 'Fomenta el equilibrio y la serenidad', 1, TRUE);

-- Inserts para Series sin la columna 'tipo'
INSERT INTO serie (ejercicioID, n_repeticiones, peso) VALUES
(1, 10, 50),
(2, 8, 70),
(3, 5, 0),
(4, 30, 0),
(5, 60, 0),
(6, 20, 0);

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
