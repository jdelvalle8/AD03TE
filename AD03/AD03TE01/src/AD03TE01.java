import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class AD03TE01 {

    private static final String URL = "jdbc:mysql://localhost:3306/dbformacion";
    private static final String USER = "root";
    private static final String PASS = "secret";

    public static void main(String[] args) {

        // Sentencia sql
        String sql = "SELECT c.id_curso, c.titulo, c.fecha, COUNT(m.dni) AS matriculados, "
                + "a.capacidad, a.nombre AS aula, a.edificio "
                + "FROM cursos c JOIN aulas a ON c.id_aula = a.id_aula "
                + "LEFT JOIN matriculas m ON c.id_curso = m.id_curso "
                + "GROUP BY c.id_curso, c.titulo, c.fecha, a.capacidad, a.nombre, a.edificio "
                + "ORDER BY c.fecha";
        // Conectamos con dbformacion
        try (Connection con = DriverManager.getConnection(URL, USER, PASS);
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
        	
        	// Formato fila de arriba
            System.out.printf("%-8s %-22s %-12s %-21s %-25s %s%n",
                    "ID", "Curso", "Fecha", "Matrículas", "Aula", "Edificio");
            System.out.println("-------------------------------------------------------------------------------------------------------");

            while (rs.next()) {
                int id = rs.getInt("id_curso");
                String titulo = rs.getString("titulo");
                String fecha = rs.getString("fecha");
                int matriculados = rs.getInt("matriculados");
                int capacidad = rs.getInt("capacidad");
                String aula = rs.getString("aula");
                String edificio = rs.getString("edificio");

                String ocupacion = matriculados + "/" + capacidad;
                
                // Formato fila de datos
                System.out.printf("%-4d %-24s %-17s %-12s %-30s %s%n",
                        id, titulo, fecha, ocupacion, aula, edificio);
            }

        } catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}