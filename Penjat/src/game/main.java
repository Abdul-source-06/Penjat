package game;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.RandomAccessFile;
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
    private static final int WORD_SIZE = 50; // Tamaño de la palabra en bytes
    private static final int SCORE_SIZE = 4; // Tamaño del entero de la puntuación
    private static final int RECORD_SIZE = WORD_SIZE + SCORE_SIZE; // Tamaño total del registro
    
    private static final int NAME_SIZE = 20;
    private static final int USERNAME_SIZE = 20;
    private static final int PASSWORD_SIZE = 20;
    private static final int BOOLEAN_SIZE = 1;
    private static final int INT_SIZE = 4;
    private static final int USER_RECORD_SIZE = NAME_SIZE + USERNAME_SIZE + PASSWORD_SIZE + BOOLEAN_SIZE + INT_SIZE;

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
            System.out.print("Choose a option: ");
            while(!sc.hasNextInt()) {
            	System.err.println("Opcion invalida, introduce un numero: ");
            	sc.next();
            }
            int option = sc.nextInt();
            sc.nextLine(); // Consumir nueva línea

            switch(option) {
                case 1:
                    loginUser(); // Llama al método para iniciar sesión
                    break;
                case 2: 
                    registerUser(); // Llama al método para registrar un nuevo usuario
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
    public static void loginUser() {
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
                        if (u.isAdmin()) {
                            mostrarMenuAdmin(u); // Si es admin, muestra el menú del administrador
                        } else {
                            mostrarMenuUsuario(u); // Muestra el menú del usuario
                        }
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
    public static void registerUser() {
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
    public static void guardarUsuari(User user) {
        ArrayList<User> users = cargarUsuarios(); // Cargar usuarios existentes
        
        // Buscar si el usuario ya existe
        boolean userExists = false;
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getUser().equals(user.getUser())) {
                // Si el usuario existe, actualiza sus datos
                users.set(i, user);
                userExists = true;
                break; // Salir del bucle una vez actualizado
            }
        }
        
        // Si el usuario no existe, se agrega el nuevo usuario
        if (!userExists) {
            users.add(user);
        }
        
        // Guardar todos los usuarios nuevamente en el archivo
        try (ObjectOutputStream writer = new ObjectOutputStream(new FileOutputStream("users.dat"))) {
            for (User u : users) {
                writer.writeObject(u); // Escribir todos los usuarios en el archivo
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
    public static ArrayList<User> cargarUsuarios() {
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
    public static boolean userExist(String username) {
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
    public static void mostrarUsuarios() {
        ArrayList<User> users = cargarUsuarios(); // Cargar usuarios
        if (users.isEmpty()) {
            System.out.println("No hay usuarios registrados.");
        } else {
            System.out.println("Usuarios registrados:");
            for (User user : users) {
                System.out.println("Nombre: " + user.getName() + " Username: " + user.getUser() + " Punts: " + user.getPunts());
            }
        }
    }

    /**
     * Método que muestra el menú específico para cada usuario.
     *
     * @param user El objeto User que representa al usuario.
     */
    public static void mostrarMenuUsuario(User user) {
        while (true) {
            System.out.println("1. Jugar");
            System.out.println("2. Logout");
            System.out.print("Choose an option: ");
            int option = sc.nextInt();
            sc.nextLine();

            switch (option) {
                case 1:
                    jugar(user);
                    break;
                case 2:
                    System.out.println("Logout exitoso!");
                    return; // Salir del menú del usuario y volver al menú principal
                default:
                    System.err.println("Invalid option");
                    break;
            }
        }
    }
    
    private static void mostrarMenuAdmin(User admin) {
        while (true) {
            System.out.println("1. Leer usuarios");
            System.out.println("2. Leer palabras");
            System.out.println("3. Editar palabras");
            System.out.println("4. Jugar");
            System.out.println("5. Logout");
            System.out.print("Elige una opción: ");
            while(!sc.hasNextInt()) {
            	System.err.println("Opcion invalida, introduce un numero: ");
            	sc.next();
            }
            	
            int option = sc.nextInt();
            sc.nextLine(); // Consumir nueva línea

            switch (option) {
                case 1:
                    mostrarUsuarios(); // Muestra la lista de usuarios registrados
                    break;
                case 2:
                    leerPalabras(); // Lee las palabras disponibles
                    break;
                case 3:
                    editarPalabras(); // Llama al método para editar palabras
                    break;
                case 4:
                    jugar(admin); // Permite al administrador jugar
                    break;
                case 5:
                	System.out.println("Logout exitoso!");
                    return; // Sale del bucle y regresa al menú principal
                default:
                    System.err.println("Opción inválida");
                    break;
            }
        }
    }
    
    /**
     * Método que permite jugar adivinando palabras.
     */
    public static void jugar(User user) {
        ArrayList<WordScore> palabras = cargarPalabras(); // Cargar las palabras desde el archivo
        if (palabras.isEmpty()) {
            System.out.println("No hay palabras para jugar.");
            return;
        }

        // Elegir una palabra aleatoria
        WordScore palabraSeleccionada = palabras.get((int) (Math.random() * palabras.size()));
        StringBuilder adivinadas = new StringBuilder("_".repeat(palabraSeleccionada.getWord().length()));
        int puntos = 0;
        int restaPunts = 5;
        int intentos = 5;
        System.out.println("Adivina la palabra: " + adivinadas);

        // Comenzar el juego
        while (!adivinadas.toString().equals(palabraSeleccionada.getWord())) {
        	System.out.println("Tienes " + intentos + " intentos");
            System.out.print("Introduce una letra: ");
            String letra = sc.next();
            
            // Verificar si la letra está en la palabra
            boolean acerto = false;
            for (int i = 0; i < palabraSeleccionada.getWord().length(); i++) {
                if (palabraSeleccionada.getWord().charAt(i) == letra.charAt(0)) {
                    adivinadas.setCharAt(i, letra.charAt(0));
                    acerto = true;
                }
            }

            if (acerto) {
                System.out.println("¡Correcto! Has adivinado la letra: " + adivinadas);
                if(adivinadas.toString().equals(palabraSeleccionada.getWord())) {
                	System.out.println("¡Felicidades! Has adivinado la palabra: " + palabraSeleccionada.getWord() + " ");
                	user.setPunts(user.getPunts() + palabraSeleccionada.getPoints());
                           	
                }

            } else {
                System.out.println("Incorrecto. Intenta de nuevo."); 
                intentos--;
            }
            
            if(intentos == 0) {
            	System.out.println("Se han acabado tus intentos!");
            	user.setPunts(user.getPunts() - restaPunts);
            	if(user.getPunts()<0) {
            		 user.setPunts(0);
            	}
            	
            	System.out.println("Tus puntos: " + user.getPunts());
                guardarUsuari(user);
            	return;

            }
            
        }

        
        System.out.println("Tu puntuacion es: " + user.getPunts());
        guardarUsuari(user);
    }
 
    public static void leerPalabras() {
        ArrayList<WordScore> palabras = cargarPalabras(); // Cargar las palabras desde el archivo
        if (palabras.isEmpty()) {
            System.out.println("No hay palabras registradas.");
        } else {
            System.out.println("Palabras registradas:");
            for (WordScore ws : palabras) {
                System.out.println("Palabra: " + ws.getWord() + ", Puntuación: " + ws.getPoints());
            }
        }
    }
    
    public static ArrayList<WordScore> cargarPalabras() {
        ArrayList<WordScore> palabras = new ArrayList<>();
        
     // Verifica si el archivo existe, si no, lo crea
        File archivo = new File("palabras.dat");
        if (!archivo.exists()) {
            try {
                archivo.createNewFile(); // Crea el archivo si no existe
            } catch (IOException e) {
                System.err.println("Error al crear el archivo: " + e.getMessage());
                return palabras; // Devuelve la lista vacía si no se pudo crear el archivo
            }
        }

        try (RandomAccessFile raf = new RandomAccessFile("palabras.dat", "r")) {
            while (raf.getFilePointer() < raf.length()) {
            	
            	//"raf.getFilePointer()" devuelve la posicion actual del puntero en el archivo raf.
            	//"raf.length()" devuelve la longitud actual del archivo en bytes
            	
                byte[] wordBytes = new byte[WORD_SIZE]; //un array de bytes del tamaño de la palabra
                raf.read(wordBytes);//lee word_size bytes del archivo y los almacena en wordBytes.
                String word = new String(wordBytes).trim();
                /*Un nuevo String apartir de wordbytes que convierte la aaray de bytes en String
                 *trim es para eliminar los espacios en blanco que pueden estar al principio o al final
                 * 
                 */
                
                int score = raf.readInt();
                palabras.add(new WordScore(word, score));
            }
        } catch (IOException e) {
            System.err.println(e);
        }
        return palabras; // Devuelve la lista de palabras
    }

    /**
     * Método que guarda una lista de palabras en el archivo.
     *
     * @param palabras La lista de palabras a guardar.
     */
    public static void editarPalabras() {
    	ArrayList<WordScore> palabras = cargarPalabras();
    	for(int i = 0; i < palabras.size();i ++){
    		System.out.println((i+1) + ". " + palabras.get(i).getWord() + " - " + palabras.get(i).getPoints());
    	}
    	
    	  System.out.println("Introduce el número de la palabra que deseas editar o 'N' para añadir una nueva palabra:");
    	  System.out.print("Elige una opción: ");
    	  String option = sc.nextLine();
    	  
    	  if(option.equalsIgnoreCase("N")){
    		  System.out.println("Introduce la nueva palabra: ");
    		  String nuevaPalabra = sc.nextLine();
    		  System.out.println("Introduce la nueva puntuacion: ");
    		  int nuevaPuntuacion = sc.nextInt();
    		  sc.nextLine();
    		  
    		  palabras.add(new WordScore(nuevaPalabra, nuevaPuntuacion));
    		  guardarPalabras(palabras); // Guarda las palabras actualizadas
    	  }else {
    		    int indice = Integer.parseInt(option) - 1;
    		    if (indice >= 0 && indice < palabras.size()) {
    		        System.out.print("Introduce la nueva palabra: ");
    		        String nuevaPalabra = sc.nextLine();
    		        System.out.print("Introduce la nueva puntuación: ");
    		        int nuevaPuntuacion = sc.nextInt();
    		        sc.nextLine();

    		        palabras.get(indice).setWord(nuevaPalabra);
    		        palabras.get(indice).setPoints(nuevaPuntuacion);
    		        guardarPalabras(palabras);
    		    }else {
    		    	System.err.println("Opcion Invalida");
    		    }
    	  }
    	  
    }
    
 // Método para guardar las palabras en el archivo
    public static void guardarPalabras(ArrayList<WordScore> palabras) {
        try (RandomAccessFile raf = new RandomAccessFile("palabras.dat", "rw")) {
            for (WordScore ws : palabras) {
                String paddedWord = String.format("%-" + WORD_SIZE + "s", ws.getWord());
                
                /*%-Indica que la palabra estara alineada a la izquierda 
                 * "WORD_SIZE" indica que la palabra ocupa 50 caracteres.
                 */
                
                raf.write(paddedWord.getBytes());//Escribe la palabra en forma de bytes donde apunta el puntero en el archivo
                raf.writeInt(ws.getPoints());
            }
            System.out.println("Palabras guardadas correctamente!");
        } catch (IOException e) {
            System.err.println("Error al guardar las palabras: " + e.getMessage());
        }
    }
       
        
    
}

	
	


