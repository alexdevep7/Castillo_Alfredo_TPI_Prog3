package com.tp.jpa.util;

public class VerConsolaH2 {
    public static void main(String[] args) throws Exception {
        org.h2.tools.Server.createWebServer("-webPort", "8082", "-tcpAllowOthers").start();
        System.out.println("=================================================");
        System.out.println("Consola web de H2 iniciada correctamente.");
        System.out.println("URL en navegador: http://localhost:8082");
        System.out.println("JDBC URL: jdbc:h2:file:./data/jpa_db");
        System.out.println("User: sa | Password: (vacío)");
        System.out.println("=================================================");
        Thread.sleep(Long.MAX_VALUE);
    }
}