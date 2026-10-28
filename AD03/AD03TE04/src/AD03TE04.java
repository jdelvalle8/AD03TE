import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Scanner;

public class AD03TE04 {

    private static final String URL = "jdbc:mysql://localhost:3306/dbformacion";
    private static final String USER = "root";
    private static final String PASS = "secret";

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        String sqlCursos = "SELECT id_curso, titulo FROM cursos ORDER BY id_curso";
        String sqlFuncion = "{ ? = call obtener_numero_matriculados(?) }";

        try (Connection con = DriverManager.getConnection(URL, USER, PASS)) {

            // Mostramos los cursos
            System.out.println("Cursos:");
            PreparedStatement ps = con.prepareStatement(sqlCursos);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                System.out.println(rs.getInt("id_curso") + ". " + rs.getString("titulo"));
            }

            // Pedimos el ID
            System.out.print("Introduce el ID del curso: ");
            int idCurso = Integer.parseInt(sc.nextLine());

            // Llamamos a la funcion obtener_numero_matriculados
            CallableStatement cs = con.prepareCall(sqlFuncion);
            cs.registerOutParameter(1, Types.INTEGER);
            cs.setInt(2, idCurso);
            cs.execute();

            int total = cs.getInt(1);
            // Los mostramos por pantalla
            System.out.println("Numero de alumnos matriculados: " + total);

        } catch (SQLException | NumberFormatException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}