## Taller Servicios:
Banco ABC
## Modelado y Validación de la Arquitectura

## Presentado Por:
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

https://raw.githubusercontent.com/juliandreita84/ModVal/master/diagramaComponentes.png

El servicio (Gateway) expone las operaciones que actualmente están publicadas entre los diferentes canales de servicio (Cajeros Automáticos, Cajero de Oficina, Teléfono, Portal Web, Aplicación Móvil) y Los Proveedores. Hacia el interior se encuentran los microservicios que hacen la comunicación hacia Los Proveedores (gas-service-soap, wáter-service-rest).

# 4.6	Catálogo de Servicios
La orientación SOA y de arquitectura basadas en microservicios donde se tiene un inventario estandarizado de servicios los cuales definirán el catálogo de servicios. Este inventario puede ser estructurado en capas de acuerdo a su modelo de servicio, pero la aplicación de los principios de orientación a servicios y la implementación de los microservicios es lo que hará posible posicionarlo como un activo importante en logro del acompañamiento en los objetivos estratégicos perseguidos por el Banco ABC.

Antes de que cualquier servicio o microservicio sea construido, es deseable establecer un blueprint conceptual de los todos los servicios que se planean incluir en este.  Esta perspectiva es la que se describe en el blueprint del inventario de servicios.  Para facilitar la tarea de identificación, se presentan a continuación esta vista conceptual del catálogo de servicio para el dominio de negocio identificado para el Banco ABC, los cuales unidos forma el inventario final resultante del trabajo de arquitectura de integración para la implementación:

# 4.6.1 Blueprint Dominio Pagos
https://raw.githubusercontent.com/juliandreita84/ModVal/master/bluePrint.png

# 4.7 Diseño de los componentes de la orquestación
# 4.7.1 Elementos en el Procesador (processor beans)

- Operación: consultarProcessor
 
●	El procesar se implementará en una ruta con el nombre consultarProcessor y principalmente deberá:

o	Transformar el Mensaje SOAP a formato JSON usando los mismos nombres de los elementos en los esquemas del WSDL actual.
o	Enviar el mensaje al productor (cola de mensajería, usando MEP “inOut”), para que sea procesada por el microservicio consultar.  Debe esperar por la respuesta de la consulta, en caso de error retornar un SOAP Fault al consumidor inicial.

- Operación: pagarProcessor
 
●	El procesor se implementará en una ruta con el nombre pagarProcessor y principalmente deberá:

o	Transformar el Mensaje SOAP a formato JSON usando los mismos nombres de los elementos en los esquemas del WSDL actual.
o	Enviar el mensaje al productor (cola de mensajería, usando MEP “inOut”), para que sea procesada por el microservicio pagar[5].  Debe esperar por la respuesta de la consulta, en caso de error retornar un SOAP Fault al consumidor inicial.

- Operación: compensarProcessor
 
●	El procesar se implementará en una ruta con el nombre compensarProcessor y principalmente deberá:

o	Transformar el Mensaje SOAP a formato JSON usando los mismos nombres de los elementos en los esquemas del WSDL actual.
o	Enviar el mensaje al productor (cola de mensajería, usando MEP “inOut”), para que sea procesada por el microservicio compensar[6].  Debe esperar por la respuesta de la consulta, en caso de error retornar un SOAP Fault al consumidor inicial.

# 4.7.5 Elementos en el Productor (producer endpoint)

El productor(s) se implementará con el nombre consultarProducer / pagarProducer/ compensarProducer y principalmente deberá hacer uso de los componentes de AMQ para el envío de los JSON a las colas de mensajería confiable y la recepción de las respuestas de los microservicios base en el Back End.  Las colas de mensajería serán transversales a todas las rutas.


# 5. REFERENCIAS

[1]- https://camel.apache.org/components/latest/sql-stored-component.html
[2]- https://access.redhat.com/documentation/en-us/red_hat_jboss_fuse/6.1/html/apache_camel_development_guide/DemoCode
[3]- https://access.redhat.com/documentation/en-US/Fuse_ESB_Enterprise/7.1/html/Web_Services_and_Routing_with_Camel_CXF/files/Payload-Format.html
[4]- https://access.redhat.com/documentation/en-[3]- us/red_hat_jboss_fuse/6.3/html/apache_camel_development_guide/fmrs-p
[5] Fowler, M.; Lewis, J. Microservices. Viittattu, (2014)


# 6. ANEXOS

- WSDL
<!--
 Published by JAX-WS RI (http://jax-ws.java.net). RI's version is Metro/2.4.0 (wsit240-7e98ff4; 2017-08-03T21:19:54+0200) JAXWS-RI/2.3.0 JAXWS-API/2.3.0 JAXB-RI/2.3.0 JAXB-API/2.3.0 svn-revision#unknown. 
-->
<!--
 Generated by JAX-WS RI (http://javaee.github.io/metro-jax-ws). RI's version is Metro/2.4.0 (wsit240-7e98ff4; 2017-08-03T21:19:54+0200) JAXWS-RI/2.3.0 JAXWS-API/2.3.0 JAXB-RI/2.3.0 JAXB-API/2.3.0 svn-revision#unknown. 
-->
<definitions xmlns:wsu="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd" xmlns:wsp="http://www.w3.org/ns/ws-policy" xmlns:wsp1_2="http://schemas.xmlsoap.org/ws/2004/09/policy" xmlns:wsam="http://www.w3.org/2007/05/addressing/metadata" xmlns:soap="http://schemas.xmlsoap.org/wsdl/soap/" xmlns:tns="http://boundary.pagos.modval.aes.javeriana.edu.co/" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns="http://schemas.xmlsoap.org/wsdl/" targetNamespace="http://boundary.pagos.modval.aes.javeriana.edu.co/" name="PagosService">
<import namespace="http://www.servicios.co/pagos/service" location="http://ec2-18-231-101-38.sa-east-1.compute.amazonaws.com:8080/gas-service/PagosService?wsdl=1"/>
<binding xmlns:ns1="http://www.servicios.co/pagos/service" name="PagosPortBinding" type="ns1:PagosInerface">
<soap:binding transport="http://schemas.xmlsoap.org/soap/http" style="document"/>
<operation name="Cosultar">
<soap:operation soapAction="consultar"/>
<input>
<soap:body use="literal"/>
</input>
<output>
<soap:body use="literal"/>
</output>
</operation>
<operation name="Pagar">
<soap:operation soapAction="pagar"/>
<input>
<soap:body use="literal"/>
</input>
<output>
<soap:body use="literal"/>
</output>
</operation>
<operation name="Compensar">
<soap:operation soapAction="compensar"/>
<input>
<soap:body use="literal"/>
</input>
<output>
<soap:body use="literal"/>
</output>
</operation>
</binding>
<service name="PagosService">
<port name="PagosPort" binding="tns:PagosPortBinding">
<soap:address location="http://ec2-18-231-101-38.sa-east-1.compute.amazonaws.com:8080/gas-service/PagosService"/>
</port>
</service>
</definitions>
Esquemas
<!--Published by JAX-WS RI (http://jax-ws.java.net). RI's version is Metro/2.4.0 (wsit240-7e98ff4; 2017-08-03T21:19:54+0200) JAXWS-RI/2.3.0 JAXWS-API/2.3.0 JAXB-RI/2.3.0 JAXB-API/2.3.0 svn-revision#unknown.-->
<xs:schema elementFormDefault="qualified" version="1.0" targetNamespace="http://www.servicios.co/pagos/schemas" xmlns:tns="http://www.servicios.co/pagos/schemas" xmlns:xs="http://www.w3.org/2001/XMLSchema">
   <xs:element name="PagoResource" type="tns:Pago"/>
   <xs:element name="ReferenciaFactura" type="tns:ReferenciaFactura"/>
   <xs:element name="Resultado" type="tns:Resultado"/>
   <xs:element name="ResultadoConsulta" type="tns:ResultadoConsulta"/>
   <xs:complexType name="Resultado">
      <xs:sequence>
         <xs:element name="referenciaFactura" type="tns:ReferenciaFactura"/>
         <xs:element name="mensaje" type="xs:string"/>
      </xs:sequence>
   </xs:complexType>
   <xs:complexType name="ReferenciaFactura">
      <xs:sequence>
         <xs:element name="referenciaFactura" type="xs:string"/>
      </xs:sequence>
   </xs:complexType>
   <xs:complexType name="ResultadoConsulta">
      <xs:sequence>
         <xs:element name="referenciaFactura" type="tns:ReferenciaFactura"/>
         <xs:element name="totalPagar" type="xs:double"/>
      </xs:sequence>
   </xs:complexType>
   <xs:complexType name="Pago">
      <xs:sequence>
         <xs:element name="referenciaFactura" type="tns:ReferenciaFactura"/>
         <xs:element name="totalPagar" type="xs:double"/>
      </xs:sequence>
   </xs:complexType>
</xs:schema>






