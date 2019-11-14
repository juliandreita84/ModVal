package com.fiduprod.processor;

import java.io.File;
import java.io.IOException;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

public class FileTransferProcessor implements Processor {

	@Override
	public void process(Exchange exchange) throws Exception {

		FTPClient client = new FTPClient();

		String sFTP = (String) exchange.getIn().getHeader("Servidor");
		String sUser = (String) exchange.getIn().getHeader("Nombre de usuario");
		String sPassword = (String) exchange.getIn().getHeader("Contraseña");

		try {
			client.connect(sFTP);
			boolean login = client.login(sUser, sPassword);

			if (login) {

				System.out.println("Conexión establecida...");
//				System.out.println("Carpeta donde esta el archivo a procesar = "+exchange.getIn().getBody());
				client.changeWorkingDirectory("\\camel");
				System.out.println("Carpeta donde esta el archivo a procesar = " + client.printWorkingDirectory());

				// andrea.gomez Carpeta donde esta el archivo a procesar
//				String sCarpAct = (String) exchange.getIn().getBody();
				String sCarpAct = client.printWorkingDirectory();

				// andrea.gomez Listemos todas las carpetas y archivos de la carpeta actual con objetos
				// FTPFile para poder ver sus propiedades
				System.out.println("//// LISTADO DE CARPETAS Y ARCHIVOS");

				File carpeta = new File(sCarpAct);
//				File[] archivos = carpeta.listFiles();
				FTPFile[] archivos = client.listFiles();
				if (archivos == null || archivos.length == 0) {
					System.out.println("No hay elementos dentro de la carpeta actual");
//					return;
				} else {
//					SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
					for (int i = 0; i < archivos.length; i++) {
						FTPFile archivo = archivos[i];
//						System.out.println(String.format("%s (%s) - %d - %s", archivo.getName(),
//								archivo.isDirectory() ? "Carpeta" : "Archivo", archivo.getSize(),
//								sdf.format(archivo.lastModified())));
						System.out.println("NOMBRE ARCHIVO: " + archivo.getName() + " DIRECTORIO: "
								+ archivo.isDirectory() + " ARCHIVO: " + archivo.isFile() + " TAMAÑO: "
								+ archivo.getSize() / 1024 + " KB (" + archivo.getSize() + " bytes)");
					}
				}
			} else {
				System.out.println("error de inicio de sesión");
			}
		} catch (IOException ioe) {
		}

		client.logout();
		client.disconnect();

	}

}