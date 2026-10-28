import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class AD03TE02 {

    private static final String URL = "jdbc:mysql://localhost:3306/dbformacion";
    private static final String USER = "root";
    private static final String PASS = "secret";

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        System.out.print("Introduce el DNI del alumno: ");
        String dni = sc.nextLine();

        String sqlBuscar = "SELECT nombre FROM alumnos WHERE dni = ?";
        String sqlCambiar = "UPDATE alumnos SET nombre = ? WHERE dni = ?";

        try (Connection con = DriverManager.getConnection(URL, USER, PASS);
             PreparedStatement psBuscar = con.prepareStatement(sqlBuscar)) {

            // Primero busco al alumno por su DNI
            psBuscar.setString(1, dni);
            String nombreActual = null;

            try (ResultSet rs = psBuscar.executeQuery()) {
                if (rs.next()) {
                    nombreActual = rs.getString("nombre");
                }
            }

            // Si no se ha encontrado, aviso y termino sin tocar nada
            if (nombreActual == null) {
                System.out.println("No existe ningun alumno con ese DNI.");
                return;
            }

            System.out.println("Nombre actual: " + nombreActual);
            System.out.print("Nuevo nombre (deja en blanco para no modificar): ");
            String nuevoNombre = sc.nextLine();

            // Si lo deja en blanco no hago nada
            if (nuevoNombre.isEmpty()) {
                System.out.println("No se ha modificado el nombre.");
                return;
            }

            // Si ha escrito algo, actualizo el nombre
            try (PreparedStatement psCambiar = con.prepareStatement(sqlCambiar)) {
                psCambiar.setString(1, nuevoNombre);
                psCambiar.setString(2, dni);

                psCambiar.executeUpdate();

                System.out.println("Nombre actualizado correctamente.");

            }

        } catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}