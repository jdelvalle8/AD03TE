import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class AD03TE03 {

    private static final String URL = "jdbc:mysql://localhost:3306/dbformacion";
    private static final String USER = "root";
    private static final String PASS = "secret";

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        String dni;
        while (true) {
            System.out.print("Introduce el DNI del alumno: ");
            dni = sc.nextLine().toUpperCase();

            if (dni.matches("\\d{8}[A-Z]")) {
                break;
            }
            System.out.println("DNI no valido. Debe tener 8 numeros y una letra.");
        }

        String sqlBuscarAlumno = "SELECT nombre FROM alumnos WHERE dni = ?";

        String sqlListadoCursos = "SELECT c.id_curso, c.titulo, "
                + "a.capacidad - obtener_numero_matriculados(c.id_curso) AS libres "
                + "FROM cursos c JOIN aulas a ON a.id_aula = c.id_aula "
                + "ORDER BY c.id_curso";

        String sqlBuscarCurso = "SELECT c.titulo, "
                + "a.capacidad - obtener_numero_matriculados(c.id_curso) AS libres "
                + "FROM cursos c JOIN aulas a ON a.id_aula = c.id_aula "
                + "WHERE c.id_curso = ?";

        String sqlYaMatriculado = "SELECT 1 FROM matriculas WHERE dni = ? AND id_curso = ?";

        String sqlAltaAlumno = "INSERT INTO alumnos (dni, nombre) VALUES (?, ?)";

        String sqlMatricula = "INSERT INTO matriculas (dni, id_curso, fecha_matricula) "
                + "VALUES (?, ?, CURDATE())";

        Connection con = null;

        try {
            con = DriverManager.getConnection(URL, USER, PASS);
            con.setAutoCommit(false);

            // Busco el alumno
            String nombre = null;
            PreparedStatement ps = con.prepareStatement(sqlBuscarAlumno);
            ps.setString(1, dni);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                nombre = rs.getString("nombre");
                System.out.println("Alumno: " + nombre);
            }
            
            boolean esNuevo = (nombre == null);

            if (esNuevo) {
                System.out.print("Alumno nuevo. Introduce su nombre: ");
                nombre = sc.nextLine();
            }

            // Listado de cursos
            System.out.println();
            System.out.println("Cursos disponibles:");
            ps = con.prepareStatement(sqlListadoCursos);
            rs = ps.executeQuery();
            while (rs.next()) {
                System.out.println(rs.getInt("id_curso") + ". " + rs.getString("titulo") + "     plazas libres: " + rs.getInt("libres"));
            }

            System.out.print("Introduce el ID del curso: ");
            int idCurso = Integer.parseInt(sc.nextLine());

            // Compruebo que el curso existe y que quedan plazas
            ps = con.prepareStatement(sqlBuscarCurso);
            ps.setInt(1, idCurso);
            rs = ps.executeQuery();

            if (!rs.next()) {
                System.out.println("El curso no existe.");
                return;
            }
            String titulo = rs.getString("titulo");
            if (rs.getInt("libres") <= 0) {
                System.out.println("No quedan plazas en ese curso.");
                return;
            }

            // Compruebo que no esta ya matriculado
            ps = con.prepareStatement(sqlYaMatriculado);
            ps.setString(1, dni);
            ps.setInt(2, idCurso);
            rs = ps.executeQuery();
            if (rs.next()) {
                System.out.println("El alumno ya esta matriculado en ese curso.");
                return;
            }

            // Alta y matricula
            if (esNuevo) {
                ps = con.prepareStatement(sqlAltaAlumno);
                ps.setString(1, dni);
                ps.setString(2, nombre);
                ps.executeUpdate();
            }

            ps = con.prepareStatement(sqlMatricula);
            ps.setString(1, dni);
            ps.setInt(2, idCurso);
            ps.executeUpdate();

            con.commit();
            System.out.println("Matricula registrada para " + nombre + " en " + titulo + ".");

        } catch (SQLException | NumberFormatException e) {
            System.err.println("Error: " + e.getMessage());
            try {
                if (con != null) {
                    con.rollback();
                }
            } catch (SQLException ex) {
                System.err.println("Error en rollback: " + ex.getMessage());
            }
        } finally {
            try {
                if (con != null) {
                    con.close();
                }
            } catch (SQLException ex) {
            }
        }
    }
}