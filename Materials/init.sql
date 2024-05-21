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
DROP TABLE IF EXISTS comentario; 

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
  foto_url VARCHAR(510),
  descripcion VARCHAR(255),
  gimnasio VARCHAR(255),
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
  comentario VARCHAR(255),
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
  valoracion BOOLEAN,
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

CREATE TABLE comentario (
  comentarioID INT AUTO_INCREMENT PRIMARY KEY,
  userID INT NOT NULL,
  descripcion TEXT NOT NULL,
  fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (userID) REFERENCES usuario(userID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Inserts para Usuario Repositorio
INSERT INTO usuario (userID, correo, password, is_first_login) VALUES
(0, 'repositorio@tuapp.com', 'password_segura', FALSE);

-- Desactivar la verificación de clave foránea
SET FOREIGN_KEY_CHECKS = 0;

-- Actualizar el userID
UPDATE `usuario` SET `userID` = '0' WHERE `userID` = '1';

-- Re-activar la verificación de clave foránea
SET FOREIGN_KEY_CHECKS = 1;

-- Inserts para Ejercicios
INSERT INTO ejercicio (userID, nombre, descripcion, tipo, grupo_muscular) VALUES
(0, 'Sentadillas', 'Ejercicio completo para piernas', 'Fuerza', 'Piernas'),
(0, 'Press de banca', 'Ejercicio para pecho', 'Fuerza', 'Pecho'),
(0, 'Pull-up', 'Ejercicio de espalda superior', 'Calistenia', 'Espalda'),
(0, 'Jogging', 'Correr a un ritmo moderado', 'Cardio', 'Todo el cuerpo'),
(0, 'Yoga', 'Sesión de yoga para mejorar flexibilidad', 'Flexibilidad', 'Todo el cuerpo'),
(0, 'Tai Chi', 'Formas lentas y meditativas', 'Equilibrio', 'Todo el cuerpo');

-- Inserts para Rutinas
INSERT INTO rutina (nombre, descripcion, userID, publico) VALUES
('Rutina de Fuerza', 'Rutina completa de fuerza para todo el cuerpo', 0, FALSE),
('Rutina de Calistenia', 'Rutina usando peso corporal', 0, FALSE),
('Rutina de Cardio', 'Rutina para mejorar la capacidad cardiovascular', 0, FALSE),
('Rutina de Flexibilidad', 'Mejora la flexibilidad general', 0, FALSE),
('Rutina de Equilibrio', 'Fomenta el equilibrio y la serenidad', 0, FALSE);

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
