# Ventas aéreas 

![Portada](assets/portada.jpg)

## :point_up: Antes de empezar: algunos consejos

El enunciado tiene **mucha** información, van a necesitar leerlo varias veces. La sugerencia es que lo lean entero una vez (para tener una idea general) y luego vuelvan a consultarlo las veces que hagan falta.

Concentrensé en los requerimientos y, excepto que se traben mucho, respeten el orden sugerido. No es necesario que hagan TDD, pero sí sería interesante que vayan creando las distintas clases y métodos a medida que resuelven cada requerimiento y no antes. 

En otras palabras: trabajen completando cada requerimiento antes de pasar al siguiente, con los tests que aseguran que funciona incluidos. Si al avanzar en los requerimientos les parece necesario refactorizar, adelante, van a tener los tests que garantizan que no rompieron nada. :smirk: 

## :bookmark_tabs: Descripción del dominio

Se pide desarrollar el modelo para un sistema de manejo de venta de pasajes de distintas empresas aéreas. Cada empresa tiene varios **aviones**, de cada avión se conoce la _cantidad de asientos_ y su _peso_. 

De cada **vuelo** que una empresa saca a la venta, se establece: 
* su _fecha_, 
* el _avión_ que se va a usar, 
* el _origen_, 
* el _destino_,
* y un precio que se define como _precio estándar_ para el vuelo. 
 
El sistema permite comprar un **pasaje** para un determinado vuelo. Cuando eso sucede, se emite un pasaje en el que registra la _fecha de venta_, el _DNI_ del pasajero o pasajera y el _importe_ que la persona abonó por él. Los pasajes no llevan número de asiento.

### Tipos de vuelo

Las empresas manejan tres tipos de vuelo, y cada tipo determina cómo se organizan los asientos de un avión. 
Cada vuelo se crea con su tipo, y no es posible modificarlo después. El tipo define tres cosas:
* la cantidad de **asientos disponibles**, que pueden usarse para pasajeros,
* la cantidad de **asientos ocupados**, que ya están reservados para algún pasajero,
* el **peso de la carga**, que servirá para calcular el peso de un vuelo.

Independientemente del tipo de vuelo, la cantidad de **asientos libres** se calcula como: `cantidad de asientos disponibles - cantidad de asientos ocupados`. Como veremos más adelante, esta cuenta puede llegar a dar como resultado un número negativo.

#### :family_woman_woman_boy_boy: Vuelo de pasajeros 

* **Asientos disponibles:** todos los asientos del avión están disponibles para llevar pasajeros,
* **Asientos ocupados:** son los de los pasajes vendidos para el vuelo,
* **Peso de la carga:** la cantidad de pasajeros * el peso del equipaje que permite llevar la empresa a cada pasajero (esto se configura para cada empresa).

#### :syringe: Vuelo de carga 

* **Asientos disponibles:** tiene 10 asientos disponibles para pasajeros, el resto del avión se utiliza para transportar vacunas,
* **Asientos ocupados:** son los de los pasajes vendidos para el vuelo,
* **Peso de la carga:** el peso de la carga (que se configura para cada vuelo) más 700 kg de equipamiento de seguridad.

#### **:tropical_drink: Vuelo charter** 

Es un vuelo que se hace para algún evento especial. 

* **Asientos disponibles:** es la `cantidad de asientos del avión - 25`, porque se ocupa una parte del avión para armar una pequeña barra que sirve tragos durante el vuelo (¡qué nivel!),
* **Asientos ocupados:** En el momento en que se organiza, ya tiene asignada una cantidad de pasajeros VIP cuyos datos no conocemos, pero sí sabemos cuántos son. Los asientos ocupados son los de estos pasajeros VIP más los de los pasajes que se vendan,
* **Peso de la carga:** 5000 kg (fijo, no cambia).

### Políticas de precio

Para cada vuelo, la empresa también establece una **política de precio** para ofrecer cada asiento. Se deben considerar estas tres variantes:

* **:moneybag: Estricta:** todos los asientos se venden al precio estándar.
* **:incoming_envelope: Venta anticipada:** si el vuelo tiene menos de 40 pasajes vendidos, 30% del precio estándar. Si el vuelo tiene entre 40 y 79 pasajes vendidos, 60% del precio estándar. Caso contrario, corresponde el precio estándar completo.
* **:mega: Remate:** si el vuelo tiene más de 30 asientos libres entonces corresponde el 25% del precio estándar, si no el 50%.

Observar que el precio depende de la cantidad de asientos vendidos en el vuelo. :wink:

**Importante:** la empresa puede decidir cambiar de política de precio para un vuelo existente, de acuerdo a cómo venga la venta. Tener en cuenta que estas tres variantes se eligieron para un primer prototipo, la idea es implementar más a futuro.

### Criterio de venta

La empresa decide si se puede vender o no pasajes sobre un vuelo, de acuerdo a uno de estos criterios:
* **:shield: Segura**: se pueden vender pasajes sobre los vuelos que tengan, al menos, 3 asientos libres.
* **:money_mouth_face: Laxa fija**: se permite vender en cada vuelo hasta 10 pasajes más de los asientos disponibles.
* **:100: Laxa porcentual**: se permite vender en cada vuelo hasta un 10% más de los asientos disponibles.
* **:mask: Pandemia**: no se puede vender ningún pasaje.

Este criterio es general para la empresa: el que la empresa elija, aplica para todos los vuelos. Si la empresa cambia de criterio, la nueva política rige para todos los vuelos, actuales y futuros.

### IATA

Por último, tenemos a la **IATA** (Asociación Internacional de Transporte Aéreo), que es una organización que regula la actividad aérea definiendo estándares y realizando estadísticas sobre todos los vuelos que hay en el mundo.

En este sistema, nos interesa que todos los vuelos que se crean queden registrados en la IATA, para posteriormente poder realizar estadísticas con ellos. Además, la IATA fija un valor para el peso estándar de un pasajero, que se utiliza para calcular el peso máximo de un vuelo. 

## :heavy_check_mark: Requerimientos

1. Conocer la cantidad de asientos libres de un vuelo.
3. Saber si se pueden vender pasajes para un vuelo o no, de acuerdo al criterio que en el momento tenga la empresa.
4. Saber el precio de venta de pasaje para un vuelo, de acuerdo a la política que tenga el vuelo, y a la cantidad de pasajes vendidos o disponibles.
5. Registrar la venta de un pasaje para un vuelo, indicando fecha y DNI del comprador. Debe registrarse también el precio establecido para el pasaje, que depende de la política que tenga el vuelo en ese momento. Si no se pueden vender pasajes, lanzar un error.
6. Saber, para un vuelo, el importe total generado por venta de pasajes.
7. Saber el peso máximo de un vuelo, que es la suma de estos factores:
   * Peso del avión.  
   * Peso de los pasajeros, que es el resultado de multiplicar la cantidad de pasajeros del vuelo por el peso estándar definido por la IATA.
   * Peso de la carga, que depende del tipo de vuelo. 

Se pide también poder realizar las siguientes consultas, teniendo en cuenta _todos_ los vuelos existentes:

8. Saber para qué fechas, una determinada persona (que se identifica por su DNI) tiene sacado pasaje para un determinado destino. P.ej. en qué fechas la persona con DNI 74404949 tiene sacado pasaje a Tahití.
9. Conocer el total de asientos libres para un destino entre dos fechas.
10. Y otra más: si dos personas son compañeras, o sea, comparten al menos 3 vuelos

## Bonus

Los siguientes requerimientos son completamente opcionales, y requieren un poquito más de algoritmia que los demás.

### Criterios por ciudad

Que el criterio de si se pueden vender o no pasajes pueda definirse en función de la ciudad de origen del vuelo. 

Si para una ciudad hay definido un criterio particular, se usa ese, y si no el general de la empresa. Este criterio tiene que poder cambiarse (o eliminarse) en cualquier momento.

### Vuelos intercontinentales

Obtener el conjunto de vuelos intercontinentales que están programados para un determinado día. Para eso, agregar al modelo la información de en qué continente está cada ciudad.

### Pagos

Pagar un pasaje es una operación distinta a comprarlo: primero se compra y luego se paga. De cada pago, nos interesa registrar la fecha y la hora en que se realizó y su importe. Separar, para cada pasaje, el importe total del efectivamente cobrado. 

Además, nos interesa poder consultar, dado un DNI, cuál es la deuda total de esa persona - considerando los pasajes que haya comprado en todos los vuelos existentes.

## :fountain_pen: Licencia

Esta obra fue elaborada por [Federico Aloi](https://github.com/faloi) y publicada bajo una [Licencia Creative Commons Atribución-CompartirIgual 4.0 Internacional][cc-by-sa].

[![CC BY-SA 4.0][cc-by-sa-image]][cc-by-sa]

[cc-by-sa]: https://creativecommons.org/licenses/by-sa/4.0/deed.es
[cc-by-sa-image]: https://licensebuttons.net/l/by-sa/4.0/88x31.png

### Créditos

:memo: [Enunciado original](https://web-ciu-programacion.github.io/site/material/documentos/ejercicios/ventas-aereas.pdf) creado por [Carlos Lombardi](https://github.com/clombardi).

:camera_flash: Imagen de portada por <a href="https://unsplash.com/@juanmascan1978?utm_source=unsplash&utm_medium=referral&utm_content=creditCopyText">Juan Pablo Mascanfroni</a> en <a href="https://unsplash.com/s/photos/aviones?utm_source=unsplash&utm_medium=referral&utm_content=creditCopyText">Unsplash</a>.
  