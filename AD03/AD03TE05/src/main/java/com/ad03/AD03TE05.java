package com.ad03;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.vibur.dbcp.ViburDBCPDataSource;

public class AD03TE05 {

    public static void main(String[] args) {

        String sqlCrear = "CREATE TABLE comprobacion ("
                + "id INTEGER PRIMARY KEY, "
                + "mensaje VARCHAR(50) NOT NULL)";
        String sqlInsertar = "INSERT INTO comprobacion (id, mensaje) VALUES (?, ?)";
        String sqlConsultar = "SELECT id, mensaje FROM comprobacion";

        // Configuramos el pool
        ViburDBCPDataSource ds = new ViburDBCPDataSource();
        ds.setDriverClassName("org.hsqldb.jdbc.JDBCDriver");
        ds.setJdbcUrl("jdbc:hsqldb:file:datos/ad03db");
        ds.setUsername("root");
        ds.setPassword("secret");
        ds.setPoolInitialSize(2);
        ds.setPoolMaxSize(8);

        // Lo iniciamos
        ds.start();

        try (Connection con = ds.getConnection()) {

            System.out.println("Conexion valida: " + con.isValid(2));

            // Creamos la tabla
            try (Statement st = con.createStatement()) {
                st.executeUpdate(sqlCrear);
            }

            // Insertamos una fila
            try (PreparedStatement ps = con.prepareStatement(sqlInsertar)) {
                ps.setInt(1, 0);
                ps.setString(2, "Conexion HSQLDB mediante Vibur correcta");
                ps.executeUpdate();
            }

            // La recuperamos
            try (PreparedStatement ps = con.prepareStatement(sqlConsultar);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    System.out.println(rs.getInt("id") + " - " + rs.getString("mensaje"));
                }
            }

        } catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());
        } finally {
            ds.terminate();
        }
    }
}