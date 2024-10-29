package game;

import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import java.util.Scanner;

/**
 * @author ahsch
 * Clase principal del juego que gestiona el registro de usuarios,
 * la autenticación, y el juego de adivinar palabras.
 */

public class main {
    
    static Scanner sc = new Scanner(System.in);

    /**
     * Método principal que inicia la aplicación.
     * Verifica si el usuario administrador existe y presenta el menú principal.
     *
     * @param args
     */
    public static void main(String[] args) {
        if (!userExist("Abdul")) { // Verifica si el admin ya existe
            User admin = new User("Abdul", "Abdul", "1234", true, 0);
            guardarUsuari(admin); // Solo guardar el admin si no hay usuarios
        }

        while(true) {
            // Mostrar el menú
            System.out.println("1. Login");
            System.out.println("2. Register");
            System.out.println("3. See Users");
            System.out.println("4. Delete user");
            System.out.print("Choose a option: ");
            int option = sc.nextInt();
            sc.nextLine(); // Consumir nueva línea

            switch(option) {
                case 1:
                    loginUser(); // Llama al método para iniciar sesión
                    break;
                case 2: 
                    registerUser(); // Llama al método para registrar un nuevo usuario
                    break;
                case 3: 
                    mostrarUsuarios(); // Muestra la lista de usuarios registrados
                    break;
                case 4: 
                    eliminarUsuario(); // Llama al método para eliminar un usuario
                    break;
                default: 
                    System.err.println("Invalid option");
                    break;
            }       
        }
    }

    /**
     * Método que permite a un usuario iniciar sesión.
     * Solicita el nombre de usuario y la contraseña.
     */
    private static void loginUser() {
        System.out.println("Username: ");
        String user = sc.next();
        System.out.println("Password: ");
        String psw = sc.next();
        
        // Verifica si el usuario existe
        if (!userExist(user)) {
            System.out.println("Usuario no encontrado.");
        } else {
            ArrayList<User> users = cargarUsuarios(); // Cargar usuarios existentes
            for (User u : users) {
                if (u.getUser().equals(user)) { // Verifica el usuario
                    if (u.getPassword().equals(psw)) { // Verifica la contraseña
                        System.out.println("Login exitoso!");
                        mostrarMenuUsuario(u); // Muestra el menú del usuario
                    } else {
                        System.out.println("Contraseña incorrecta."); 
                    }
                    break; // Sale del bucle al encontrar el usuario
                }
            }
        }
    }

    /**
     * Método que permite registrar un nuevo usuario.
     * Solicita información del nuevo usuario y lo guarda.
     */
    private static void registerUser() {
        System.out.println("Introduce tu nombre: ");
        String nom = sc.next();
        System.out.println("Introduce tu nombre de usuario: ");
        String user = sc.next();
        while(userExist(user)) { // Verifica que el nombre de usuario no exista
            System.err.println("Usuario ya existe, introduce otro nombre de usuario: ");
            user = sc.next();
        }
        System.out.println("Introduce una contraseña: ");
        String pass = sc.next();
        boolean adm = false; // El nuevo usuario no es admin por defecto
        
        User newUser = new User(nom, user, pass, adm, 0);
        guardarUsuari(newUser); // Guarda el nuevo usuario
    }

    /**
     * Método que guarda un usuario en el archivo.
     *
     * @param user El objeto User que se va a guardar.
     */
    private static void guardarUsuari(User user) {
        ArrayList<User> users = cargarUsuarios(); // Cargar usuarios existentes
        users.add(user); // Agrega el nuevo usuario a la lista
        try (ObjectOutputStream writer = new ObjectOutputStream(new FileOutputStream("users.dat"))) {
            for (User u : users) {
                writer.writeObject(u); // Escribir todos los usuarios de nuevo
            }
            System.out.println("Usuario guardado correctamente!");
        } catch (IOException ex) {
            System.err.println(ex);
        }
    }

    /**
     * Método que carga todos los usuarios desde el archivo.
     *
     * @return Lista de usuarios cargados.
     */
    private static ArrayList<User> cargarUsuarios() {
        ArrayList<User> users = new ArrayList<>();
        try (ObjectInputStream reader = new ObjectInputStream(new FileInputStream("users.dat"))) {
            while (true) {
                User user = (User) reader.readObject(); // Leer el objeto User
                users.add(user); // Agregar a la lista
            }
        } catch (EOFException e) {
            
        } catch (IOException ex) {
            System.err.println(ex);
        } catch (ClassNotFoundException ex) {
            System.err.println(ex); 
        }
        return users; // Devuelve la lista de usuarios
    }

    /**
     * Método que verifica si un usuario existe en el sistema.
     *
     * @param username El nombre de usuario a verificar.
     * @return true si el usuario existe, false en caso contrario.
     */
    private static boolean userExist(String username) {
        ArrayList<User> users = cargarUsuarios(); // Cargar usuarios existentes
        for (User user : users) {
            if (user.getUser().equals(username)) { // Compara el username
                return true; // El usuario existe
            }
        }
        return false; // El usuario no existe
    }

    /**
     * Método que muestra todos los usuarios registrados.
     */
    private static void mostrarUsuarios() {
        ArrayList<User> users = cargarUsuarios(); // Cargar usuarios
        if (users.isEmpty()) {
            System.out.println("No hay usuarios registrados.");
        } else {
            System.out.println("Usuarios registrados:");
            for (User user : users) {
                System.out.println("Nombre: " + user.getName() + ", Username: " + user.getUser());
            }
        }
    }

    /**
     * Método que permite eliminar un usuario del sistema.
     */
    private static void eliminarUsuario() {
        System.out.println("Introduce el nombre de usuario que deseas eliminar: ");
        String username = sc.nextLine();

        ArrayList<User> users = cargarUsuarios(); // Cargar usuarios existentes
        boolean found = false; // booleano para verificar si se encontró el usuario

        // Iterar sobre la lista de usuarios
        Iterator<User> iterator = users.iterator();
        while (iterator.hasNext()) {
            User user = iterator.next();
            if (user.getUser().equals(username)) { // Verifica si el usuario coincide
                iterator.remove(); // Eliminar el usuario
                found = true;
                break;
            }
        }

        if (found) {
            // Guardar la lista actualizada sin el usuario eliminado
            try (ObjectOutputStream writer = new ObjectOutputStream(new FileOutputStream("users.dat"))) {
                for (User u : users) {
                    writer.writeObject(u); // Escribir todos los usuarios de nuevo
                }
                System.out.println("Usuario " + username + " eliminado correctamente!");
            } catch (IOException ex) {
                System.err.println(ex); 
            }
        } else {
            System.out.println("Usuario no encontrado.");
        }
    }

    /**
     * Método que muestra el menú específico para cada usuario.
     *
     * @param user El objeto User que representa al usuario.
     */
    private static void mostrarMenuUsuario(User user) {
        while (true) {
            System.out.println("1. Afegir paraules (només si l’usuari és admin)");
            System.out.println("2. Jugar");
            System.out.println("3. Logout");
            System.out.print("Choose an option: ");
            int option = sc.nextInt();
            sc.nextLine();

            switch (option) {
                case 1:
                    if (user.isAdmin()) {
                        afegirParaules(); // Permite al admin agregar palabras
                    } else {
                        System.err.println("No tienes permisos de administrador.");
                    }
                    break;
                case 2:
                    jugar(); // Inicia el juego
                    break;
                case 3:
                    System.out.println("Logout exitoso!");
                    return; // Salir del menú del usuario y volver al menú principal
                default:
                    System.err.println("Invalid option");
                    break;
            }
        }
    }
    
    /**
     * Método que permite jugar adivinando palabras.
     */
    private static void jugar() {
        ArrayList<String> palabras = cargarPalabras(); // Cargar las palabras desde el archivo
        if (palabras.isEmpty()) {
            System.out.println("No hay palabras para jugar.");
            return;
        }

        // Elegir una palabra aleatoria
        String palabraSeleccionada = palabras.get((int) (Math.random() * palabras.size()));
        StringBuilder adivinadas = new StringBuilder("_".repeat(palabraSeleccionada.length()));

        System.out.println("Adivina la palabra: " + adivinadas);

        // Comenzar el juego
        while (!adivinadas.toString().equals(palabraSeleccionada)) {
            System.out.print("Introduce una letra: ");
            String letra = sc.next();

            // Verificar si la letra está en la palabra
            boolean acerto = false;
            for (int i = 0; i < palabraSeleccionada.length(); i++) {
                if (palabraSeleccionada.charAt(i) == letra.charAt(0)) {
                    adivinadas.setCharAt(i, letra.charAt(0));
                    acerto = true;
                }
            }

            if (acerto) {
                System.out.println("¡Correcto! Adivina la palabra: " + adivinadas);
            } else {
                System.out.println("Incorrecto. Intenta de nuevo.");
            }
        }

        System.out.println("¡Felicidades! Adivinaste la palabra: " + palabraSeleccionada);
    }
    
    /**
     * Método que permite al administrador agregar nuevas palabras al juego.
     */
    private static void afegirParaules() {
        System.out.println("Palabras actuales:");
        ArrayList<String> palabras = cargarPalabras(); // Cargar palabras actuales
        for (String palabra : palabras) {
            System.out.println(palabra); // Muestra las palabras actuales
        }

        System.out.println("Introduce una nueva palabra: ");
        String nuevaPalabra = sc.nextLine();
        palabras.add(nuevaPalabra);
        guardarPalabras(palabras); // Guarda la lista actualizada
        System.out.println("Palabra añadida correctamente!");
    }
    
    /**
     * Método que carga palabras desde el archivo.
     *
     * @return Lista de palabras cargadas.
     */
    private static ArrayList<String> cargarPalabras() {
        ArrayList<String> palabras = new ArrayList<>();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("paraules.dat"))) {
            while (true) {
                String palabra = (String) ois.readObject(); // Leer la palabra
                palabras.add(palabra); // Agregar a la lista
            }
        } catch (EOFException e) {
            
        } catch (IOException e) {
            System.err.println(e); 
        } catch (ClassNotFoundException e) {
            System.err.println(e);
        }
        return palabras;
    }

    /**
     * Método que guarda una lista de palabras en el archivo.
     *
     * @param palabras La lista de palabras a guardar.
     */
    private static void guardarPalabras(ArrayList<String> palabras) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("paraules.dat"))) {
            for (String palabra : palabras) {
                oos.writeObject(palabra); // Escribir cada palabra en el archivo
            }
        } catch (IOException e) {
            System.err.println(e); 
        }
    }
}
	
	
	


