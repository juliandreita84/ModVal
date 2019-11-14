-- MySQL Administrator dump 1.4
--
-- ------------------------------------------------------
-- Server version	5.7.24


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;


--
-- Create schema busmanager
--

CREATE DATABASE IF NOT EXISTS busmanager;
USE busmanager;

--
-- Definition of table `lista_valores`
--

DROP TABLE IF EXISTS `lista_valores`;
CREATE TABLE `lista_valores` (
  `id_lista` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `item_lista` varchar(45) NOT NULL,
  `descripcion` varchar(100) NOT NULL,
  `id_padre` varchar(45) NOT NULL,
  `id_hijo` varchar(45) NOT NULL,
  PRIMARY KEY (`id_lista`,`item_lista`)
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=latin1;

--
-- Dumping data for table `lista_valores`
--

/*!40000 ALTER TABLE `lista_valores` DISABLE KEYS */;
INSERT INTO `lista_valores` (`id_lista`,`item_lista`,`descripcion`,`id_padre`,`id_hijo`) VALUES 
 (3,'NUMBER','NUMERICO','0','0'),
 (3,'STRING','TEXTO','0','0'),
 (7,'0','Administrador','',''),
 (7,'1','Usuario Plataforma','',''),
 (14,'No','No','',''),
 (14,'Si','Si','',''),
 (19,'ACTIVO','Activo','',''),
 (19,'INACTIVO','Inactivo','','');
/*!40000 ALTER TABLE `lista_valores` ENABLE KEYS */;


--
-- Definition of table `mensajes`
--

DROP TABLE IF EXISTS `mensajes`;
CREATE TABLE `mensajes` (
  `id_mensaje` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `id_usuario` varchar(45) NOT NULL,
  `fecha_msg` datetime NOT NULL,
  `text_msg` text NOT NULL,
  `estado` varchar(45) NOT NULL,
  `fecha_cerrado` datetime DEFAULT NULL,
  PRIMARY KEY (`id_mensaje`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=latin1;

--
-- Dumping data for table `mensajes`
--

/*!40000 ALTER TABLE `mensajes` DISABLE KEYS */;
INSERT INTO `mensajes` (`id_mensaje`,`id_usuario`,`fecha_msg`,`text_msg`,`estado`,`fecha_cerrado`) VALUES 
 (1,'1','2019-04-10 00:00:00','Pruebas','CERRADO','2019-04-10 11:11:31'),
 (2,'1','2019-04-09 00:00:00','prueba1','CERRADO','2019-04-10 11:11:13');
/*!40000 ALTER TABLE `mensajes` ENABLE KEYS */;


--
-- Definition of table `menu_opcion`
--

DROP TABLE IF EXISTS `menu_opcion`;
CREATE TABLE `menu_opcion` (
  `id_menu` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `orden_menu` int(10) unsigned NOT NULL,
  `nombre_menu` varchar(100) NOT NULL,
  `nivel` int(10) unsigned NOT NULL,
  `pagina` varchar(120) NOT NULL,
  `id_padre` int(10) unsigned NOT NULL,
  `icono` varchar(100) NOT NULL,
  PRIMARY KEY (`id_menu`)
) ENGINE=InnoDB AUTO_INCREMENT=1002 DEFAULT CHARSET=latin1;

--
-- Dumping data for table `menu_opcion`
--

/*!40000 ALTER TABLE `menu_opcion` DISABLE KEYS */;
INSERT INTO `menu_opcion` (`id_menu`,`orden_menu`,`nombre_menu`,`nivel`,`pagina`,`id_padre`,`icono`) VALUES 
 (1,0,'Dashboard',0,'*',0,'fa fa-home'),
 (2,1,'Configuracion',0,'*',0,'fa fa-cog'),
 (3,2,'Informes',0,'*',0,'fa fa-database'),
 (7,1,'Usuarios',1,'admin_user.php',2,''),
 (8,1,'Panel de Inicio',1,'index.php',1,''),
 (9,4,'Reglas',1,'rules_admin.php',2,''),
 (10,5,'Rutas',1,'routes_admin.php',2,''),
 (11,1,'Log del Integraciones',1,'log_integrations.php',3,''),
 (12,0,'Estadisticas',1,'control_rips.php',3,''),
 (13,2,'Perfiles',1,'profile_users.php',2,'0'),
 (14,2,'Integraciones',1,'integrations_admin.php',2,'0'),
 (15,3,'Aplicaciones',1,'applications_admin.php',2,'0'),
 (1000,0,'Perfil de Usuarios',2,'pages-user-profile.php',0,''),
 (1001,1,'Notificaciones Sistema',2,'inbox.php',0,'');
/*!40000 ALTER TABLE `menu_opcion` ENABLE KEYS */;


--
-- Definition of table `perfil_opciones`
--

DROP TABLE IF EXISTS `perfil_opciones`;
CREATE TABLE `perfil_opciones` (
  `id_opcion` int(10) NOT NULL DEFAULT '0',
  `id_perfil` varchar(45) NOT NULL,
  PRIMARY KEY (`id_opcion`,`id_perfil`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `perfil_opciones`
--

/*!40000 ALTER TABLE `perfil_opciones` DISABLE KEYS */;
INSERT INTO `perfil_opciones` (`id_opcion`,`id_perfil`) VALUES 
 (0,'1'),
 (7,'1'),
 (8,'1'),
 (9,'1'),
 (10,'1'),
 (11,'1'),
 (12,'1'),
 (13,'1'),
 (14,'1'),
 (15,'1'),
 (16,'2'),
 (1000,'6'),
 (1001,'2'),
 (1001,'6');
/*!40000 ALTER TABLE `perfil_opciones` ENABLE KEYS */;


--
-- Definition of table `usuario`
--

DROP TABLE IF EXISTS `usuario`;
CREATE TABLE `usuario` (
  `id_usuario` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `nombre_usuario` varchar(200) NOT NULL,
  `clave` varchar(45) NOT NULL,
  `correo` varchar(200) NOT NULL,
  `perfil` int(11) NOT NULL,
  `estado` varchar(45) NOT NULL DEFAULT 'ACTIVO',
  `pagina_inicio` varchar(200) NOT NULL,
  PRIMARY KEY (`id_usuario`,`clave`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=latin1;

--
-- Dumping data for table `usuario`
--

/*!40000 ALTER TABLE `usuario` DISABLE KEYS */;
INSERT INTO `usuario` (`id_usuario`,`nombre_usuario`,`clave`,`correo`,`perfil`,`estado`,`pagina_inicio`) VALUES 
 (1,'Usuario Pruebas','UkZ2SnhweCtNQytCS2FySEMvRG0vQT09','admin@entelgy.com',1,'ACTIVO','index.php');
/*!40000 ALTER TABLE `usuario` ENABLE KEYS */;




/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
