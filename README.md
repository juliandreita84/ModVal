##Taller Servicios:
Banco ABC
##Modelado y Validación de la Arquitectura

##Presentado Por:
Andrea Gómez Rojas y Jennifer Goyeneche Ramírez



# 1. INTRODUCCIÓN

A lo largo de este documento se describen los diferentes elementos y las decisiones de diseño que constituyen la solución para la implementación de los servicios Pagos Proveedores.

En este se muestran las consideraciones de diseño para la implementación del servicio el cual estará dentro del contexto de 1 o más integraciones.

# 2. OBJETIVOS

# 2.1. General 
Este documento se constituye en un insumo para los procesos de construcción del software, sin embargo, para mantener a todos los interesados siempre en el mismo entendimiento, será necesario actualizar este entregable cada vez que se presenten cambios en la solución inicial diseñada ya sea por causas relevantes al desarrollo en sí mismo o por actualizaciones puntuales en el alcance.


# 3. ARQUITECTURA DE INTEGRACIÓN

# 3.1. Principios de la arquitectura de aplicaciones 
Los principios de la arquitectura [5] son reglas y valores generales, que pretenden informar y soportar la manera en que la organización logrará su misión.  Estos principios guían el trabajo de arquitectura y se han definido a nivel del dominio aplicaciones y de integración.  A continuación, se describen nuevamente para su validación durante la revisión de este diseño y para la construcción de la integración:

•	Implementación de la arquitectura mediante capas de microservicios: lo cual permite no solamente una correcta organización del proyecto siguiendo la arquitectura, sino que genera un desacoplamiento de todas las partes, permitiendo que la implementación futura de integraciones crezca posteriormente sin afectar la operación de los demás microservicios existentes.

•	Orientación a Microservicios:  Arquitectura de microservicios, conocido por las siglas MSA (del inglés Micro Services Architecture) consiste en la implementación de microcomponentes cada uno especializado ejecutando su propio proceso en un contenedor tipificado por categoría de microservicios especializados. Cada microservicio debe estar contenido en un único contenedor y este a su vez puede tener varios microservicios especificados.

•	Consistencia y separación de responsabilidades: este principio indica que las directrices, lineamientos y buenas prácticas de codificación, deben aplicar y ser los mismos para todos los componentes pertenecientes a la misma capa. Es decir, lo definido para la capa de persistencia aplica para todas las entidades pertenecientes al modelo de datos. Alta Cohesión es la medida de especialización de las responsabilidades de los componentes/servicios que conforman la solución.

•	Coherencia entre canales: los flujos de información y los datos manejados para utilizar las funcionalidades de la solución deben ser coherentes independiente del canal que se esté utilizando para interactuar con la funcionalidad.


# 4. PERFIL DEL SERVICIO

# 4.1. Identificación

- Código:	GATEWAY001
- Nombre del servicio:	gateway – Web Service Proveedores
- Versión del servicio:	1.0
- Diseño API:	https://studio-ws.apicur.io/sharing/918295e6-1d7a-4001-beb9-4e99bd0b4dcd
- Descripción del servicio:
Este servicio permite conservar las operaciones de servicio expuestas por el Integrador hacia los diferentes canales de servicio (Cajeros Automáticos, Cajero de
Oficina, Teléfono, Portal Web, Aplicación Móvil), las cuales contemplan, la Consulta de saldo a Pagar, Pago del Servicio y Compensación de pago.  Elimina la responsabilidad del integrador de la exposición y consumo de WSDLs permitiendo publicar el servicio como un nuevo endpoint en el ESB que se comunicará a través de colas de mensajería a las APIs internas las cuales tienen configurada la conexión con la base de datos externa.

# 4.2. Características

- Protocolo del servicio:	SOAP/HTTPS
- Tipo de comunicación: 	Sincrónica
- Patrón (MEP):	InOut
- Frecuencia:	A demanda por invocación externa (ver requerimiento volumen)
- Tamaño del mensaje:	Máximo 1MB (1 consulta) 
- Dominio:	Pagos
- WSDL actual: 	http://ec2-18-231-101-38.sa-east-1.compute.amazonaws.com:8080/gas-service/PagosService?wsdl1 --> Este es uno de los proveedores a integrar
- Tipo de Inicio:	Endpoint SOAP
- Patrón Integración: 	Orquestación / Microservicios / API / Cola de mensajes / Split

# 4.3. Repositorio
< Será actualizado durante fase de documentación técnica y entrega>

# 4.4. Capacidades del servicio
A continuación se describen las capacidades del servicio en términos de la(s) función(es) que ofrece cuando se invoquen las operaciones del servicio una vez se encuentre implementado:

- Consulta de saldo a Pagar:	Los diferentes canales de servicio (Cajeros Automáticos, Cajero de Oficina, Teléfono, Portal Web, Aplicación Móvil) ejecutan la Consulta de saldo a Pagar en el proveedor de Gas.
- Pago del Servicio:	Los diferentes canales de servicio (Cajeros Automáticos, Cajero de Oficina, Teléfono, Portal Web, Aplicación Móvil) y una vez seleccionado el pago a realizar, paga el servicio.
- Compensación de pago:	Una vez realizada la integración Pago del servicio, se ejecuta la compensación de pago.

# Atributos de Calidad

1. Invocación:	Tipo de invocación / Protocolo	-->	Sincrónica / SOAP
2. Seguridad:	Autenticación Básica / Cabecera WS-Security / Transporte	-->	Usuario y Contraseña / https
3. Calidad de Servicio:	Tiempo de respuesta promedio (Segs.)	-->	Para operación de Consultar: 256ms. / Para operación de Pagar: 258ms. / Para operación de Compensar: 258ms.
4. Cifrado de Mensaje:	Na	
5. Transacciones:	Insert, Update	-->	No se requiere gestión de transacción. / Se hace propagación del error para su notificación en el consumidor final.

# 4.5	Diagrama de Componentes
# 4.3.1 Integración gas-service_Pagos
En el siguiente diagrama se describen los principales componentes para el desarrollo de esta integración. El módulo Gris (Gateway) dentro del componente de orquestación, hacen parte de este diseño.













