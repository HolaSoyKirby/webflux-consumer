Proyecto que funciona como consumer de kafka para procesar las órdenes pedidas desde el proyecto webflux-producer.

Es necesario contar con un servidor de mongodb corriendo y el servicio de kafka para que el proyecto funcione correctamente.

El proyecto se levanta con la versión 21 del JDK.

Para generar los eventos se necesita levantar el siguiente proyecto y ejecutar el endpoint que viene en su dicho README

https://github.com/HolaSoyKirby/webflux-producer