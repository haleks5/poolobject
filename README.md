poolobject
==========
Los pasos para gestionar el procesos son los siguientes:
1. Cada participante del equipo tiene que estar registrado en Github. Se tiene que registrar con el
nombre usuario de la UBU en repositorio de proyectos Github https://github.com/
2. Uno de los participantes, le referenciamos como repoadmin, tiene que realizar un fork del
repositorio donde se encuentra el código que se quiere probar
https://github.com/clopezno/poolobject. El nuevo fork de ese repositorio tiene que ser público.
3. Invitar al resto de miembros del equipo para que puedan participar en el proceso de desarrollo del
conjunto de pruebas.
4. El participante repoadmin vincula el proyecto con Github actions y Codecov.io.
5. Realizar un trabajo colaborativo implementando un caso de prueba cada participante.
1. Comunicarse entre los participantes para distribuir los métodos de la clase
main.java.ubu.gii.dass.c01.PoolTest.java. que implementará cada participante.
2. Una vez que un participante haya implementado su test y comprobado su ejecución en su
entorno de desarrollo deberá compartirlo en github con un commit/push al repositorio del equipo,
con la previa actualización pull. Para documentar su colaboración el commit tiene que tener un
mensaje descriptivo del caso de prueba añadido y que coincida con la anotación interna
@Displayname de su implementación em Junit.
3. Después del commit verificar el resultado de las pruebas en el workflow de integración continua
y cómo la cobertura de pruebas va mejorando con las sucesivas integraciones.
4. Terminadas la implementación de las pruebas colaborativas (cobertura 100% en codecov)
documentar en la wiki del propio repositorio. Recordad configurar la activación de la wiki en el
repositorio.

