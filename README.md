# Ventas aéreas :small_airplane: 

## Antes de empezar: algunos consejos

El enunciado tiene **mucha** información, van a necesitar leerlo varias veces. La sugerencia es que lo lean entero una vez (para tener una idea general) y luego vuelvan a consultarlo las veces que hagan falta.

Concentrensé en los requerimientos y, excepto que se traben mucho, respeten el orden sugerido. No es necesario que hagan TDD, pero sí sería interesante que vayan creando las distintas clases y métodos a medida que resuelven cada requerimiento y no antes. 

En otras palabras: trabajen completando cada requerimiento antes de pasar al anterior, con los tests que aseguran que funciona incluidos. Si al avanzar en los requerimientos les parece necesario refactorizar, adelante, van a tener los tests que garantizan que no rompieron nada. :smirk: 

## Descripción del dominio

Se pide desarrollar el modelo para un sistema de manejo de venta de pasajes de una empresa aérea. La línea tiene varios **aviones**, de cada avión se conoce la _cantidad de asientos_ y la _altura de la cabina_. 

De cada **vuelo** que la empresa saca a la venta, se establece: _fecha_, _avión_ que se va a usar, _origen_, _destino_, _tiempo de vuelo_, y un precio que se define como _precio estándar_ para el vuelo. Cada **pasaje** emitido corresponde a un vuelo, se registra la _fecha de venta_ y el _DNI_ del pasajero. Los pasajes no llevan número de asiento.

La empresa maneja tres tipos de vuelo, a saber:

* **Vuelo normal**: todos los asientos del avión están disponibles para llevar pasajeros. Los asientos ocupados son los de los pasajes vendidos para el vuelo.
* **Vuelo de carga**: tiene 30 asientos disponibles para pasajeros, el resto del avión lleva carga. Los asientos ocupados son los de los pasajes vendidos para el vuelo.
* **Vuelo charter**: es un vuelo que se hace para algún evento especial. La cantidad de asientos disponibles para pasajeros es la `cantidad de asientos del avión - 25`, porque se ocupa una parte del avión para armar una pequeña barra que sirve tragos durante el vuelo (¡qué nivel!).
En el momento en que se organiza, ya tiene asignada una cantidad de pasajeros cuyos datos no nos interesan. El resto de los asientos sale a la venta. Los asientos ocupados son los de estos pasajeros iniciales, más los de los pasajes que se vendan.

La cantidad de asientos libres de un avión se calcula como: `cantidad de asientos disponibles - cantidad de asientos ocupados`.
Para cada vuelo, la empresa también establece una política de a qué precio ofrecer cada asiento. Se deben considerar estas tres variantes:

* **Estricta:** todos los asientos se venden al precio estándar.
* **Venta anticipada:** si el vuelo tiene menos de 40 pasajes vendidos, 30% del precio estándar. Si el vuelo tiene entre 40 y 79 pasajes vendidos, 60%, del precio estándar. Caso contrario, corresponde el precio estándar completo.
* **Remate:** si el vuelo tiene más de 30 asientos libres entonces corresponde el 25% del precio estándar, si no el 50%.

Observar que el precio depende de la cantidad de asientos vendidos en el vuelo.

**Importante:** La empresa puede decidir cambiar de política de precio para un vuelo existente, de acuerdo a cómo venga la venta. Tener en cuenta que estas tres variantes se eligieron para un primer prototipo, la idea es implementar más a futuro.

La empresa decide si se puede vender o no pasajes sobre un vuelo, de acuerdo a uno de estos criterios:
* **Segura**: se pueden vender pasajes sobre los vuelos que tengan, al menos, 3 asientos libres.
* **Laxa**: se permite vender en cada vuelo hasta 10 pasajes más de los asientos disponibles.
* **Por porcentaje**: se permite vender en cada vuelo hasta un 10% más de los asientos disponibles.
* **Pandemia**: no se puede vender ningún pasaje.

Este criterio es general para la empresa: el que la empresa elija, aplica para todos los vuelos. Si la empresa cambia de criterio, la nueva políica rige para todos los vuelos, actuales y futuros, hasta el siguiente cambio.

Por último, tenemos a la **IATA** (Asociación Internacional de Transporte Aéreo), que es una organización que regula la actividad aérea, definiendo estándares y realizando estadísticas sobre los vuelos que hay en el mundo. 

## Requerimientos

1. Conocer la cantidad de asientos libres de un vuelo.
2. Poder decir si un vuelo es relajado o no. Se considera que un vuelo es relajado si la cabina del avión tiene más de 4 metros de alto, y tiene menos de 100 asientos disponibles para pasajeros.
3. Saber si se pueden vender pasajes para un vuelo o no, de acuerdo al criterio que en el momento tenga la empresa.
4. Saber el precio de venta de pasaje para un vuelo, de acuerdo a la política que tenga el vuelo, y a la cantidad de pasajes vendidos o disponibles.
5. Registrar la venta de un pasaje para un vuelo, indicando fecha y DNI del comprador. Registrar también el precio establecido para el pasaje. Si no se pueden vender pasajes, lanzar un error.
6. Saber, para un vuelo, el importe total generado por venta de pasajes.
7. Saber el peso máximo de un vuelo, que es la suma de estos factores:
   * Peso del avión.  
   * Peso de los pasajeros, que es el resultado de multiplicar la cantidad de pasajeros del vuelo por un peso estándar definido por la IATA (Asociación Internacional de Transporte Aéreo).
   * Peso de la carga, que depende del tipo de vuelo. Para vuelos normales, es la cantidad de pasajeros * lo que permite llevar la empresa a cada pasajero. Para vuelos de carga, el peso de la carga (que se setea para cada vuelo de carga) más 700 kg de equipamiento de seguridad. Para vuelos charter, 5000 kg fijo.

Se pide también poder realizar las siguientes consultas, teniendo en cuenta _todos_ los vuelos existentes:

8. Saber para qué fecha o fechas, una determinada persona (que se identifica por su DNI) tiene sacado pasaje para un determinado destino. P.ej. en qué fechas la persona con DNI 74404949 tiene sacado pasaje a Tahití.
9. Conocer el total de asientos libres para un destino entre dos fechas.
10. Y otra más: si dos personas son compañeras, o sea, comparten al menos 3 vuelos

## Bonus

Los siguientes requerimientos son completamente opcionales, y requieren un poquito más de algoritmia que los demás.

1. Que el criterio de si se pueden vender o no pasajes pueda cambiarse de acuerdo al origen del vuelo. Tiene que seguir habiendo un criterio general. Si para una ciudad hay definido un criterio particular, usar ese, y si no el general de la empresa.
3. Obtener el conjunto de vuelos intercontinentales que están programados para un determinado día. Para eso se cuenta con la información de en qué continente está cada ciudad.
2. Registrar los pagos. Pagar un pasaje es una operación distinta a comprarlo, primero se compra y luego se paga. Soportar pagos parciales. Separar, para un vuelo, el importe total vendido del efectivamente cobrado. Poder saber, dado un DNI, cuánto debe la persona por pasajes que compró y no pagó (o no pagó totalmente).
