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

SET FOREIGN_KEY_CHECKS = 1; -- Reactiva la verificación de claves foráneas

CREATE TABLE objetivo (
  objetivoID INT AUTO_INCREMENT PRIMARY KEY,
  peso_objetivo DECIMAL(5,2) NOT NULL,
  descripcion VARCHAR(255)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE usuario (
  userID INT AUTO_INCREMENT PRIMARY KEY,
  nombre VARCHAR(255) NOT NULL,
  apellido VARCHAR(255) NOT NULL,
  correo VARCHAR(255) UNIQUE NOT NULL,
  password VARCHAR(255) NOT NULL,
  sexo VARCHAR(10) NOT NULL,
  peso_actual DECIMAL(5,2) NOT NULL,
  altura INT NOT NULL,
  foto BLOB,
  descripcion VARCHAR(255),
  redes_sociales VARCHAR(255),
  tiempo_entrenamiento TIME,
  objetivoID INT,
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
  tipo VARCHAR(50),
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
  user_ID INT NOT NULL,
  publico BOOLEAN,
  FOREIGN KEY (user_ID) REFERENCES usuario(userID)
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
