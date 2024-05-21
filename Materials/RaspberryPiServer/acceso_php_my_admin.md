Abrid un terminal, y ejecutais el ssh para conectaros a traves de vuestro ordenador por el puerto 8888 al 80 de la maquina,
que es donde se encuentra el phpmyadmin:
 ```cmd
ssh -L 8888:localhost:80 backend@easyfitness-uab.duckdns.org
```
Una vez hecho el tunel, meteros en un navegador en vuestro ordenador y haced:
```cmd
http://localhost:8888/phpmyadmin
```
Desde aqui podeis acceder al phpMyAdmin.

Una vez acabeis de configurar lo que sea, cerrad la conexion ssh con
```cmd
exit
```

**Claves para acceder**

usuario: root
password: cnet1342
