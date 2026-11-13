# music-kotlin

Base de datos musical con interfaz gráfica, hecha para el Proyecto 2 de Modelado y Programación.

El programa mina una carpeta con archivos MP3, lee sus etiquetas ID3v2.4 y guarda la información en una base de datos SQLite. Desde la interfaz se pueden ver y editar las rolas, los álbumes y los intérpretes (como persona o grupo), dar de alta personas, agregar miembros a los grupos y hacer búsquedas con un lenguaje sencillo, sin escribir SQL. Los archivos MP3 nunca se modifican.

## Lenguaje y versiones

| Herramienta | Versión |
|---|---|
| Kotlin | 2.4.20 |
| Compose Multiplatform | 1.12.1 |
| Gradle (incluido con `gradlew`) | 9.5.1 |
| Java (JDK) | 21 |
| SQLite (driver sqlite-jdbc) | 3.53.2.1 |
| jaudiotagger | 3.0.1 |

## Instalación

Solo hace falta tener **Java 17 o superior** (se recomienda el 21) y **Git**. Gradle viene incluido en el proyecto (`gradlew`) y descarga solo todo lo demás, incluidas las bibliotecas y, si hace falta, el JDK 21 que usa el proyecto. Por eso, **la primera compilación necesita internet** y tarda unos minutos.

Para instalar Java:

```bash
# Fedora
sudo dnf install java-21-openjdk-devel

# Ubuntu / Debian
sudo apt install openjdk-21-jdk
```

Para comprobar que quedó instalado:

```bash
java -version
```

Después, clona el repositorio:

```bash
git clone https://github.com/jonathan3467/music-kotlin.git
cd music-kotlin
```

## Compilar y correr

Para compilar y abrir el programa:

```bash
./gradlew :desktopApp:run
```

Para correr las pruebas unitarias:

```bash
./gradlew :shared:jvmTest
```

Si aparece "Permiso denegado" al usar `./gradlew`, dale permiso de ejecución:

```bash
chmod +x gradlew
```

## Uso

1. Al abrir el programa por primera vez, la lista está vacía. Presiona **Minar carpeta…** y escoge una carpeta con archivos MP3; se recorren también sus subcarpetas.
2. Haz clic en una rola para ver sus datos en el panel de la izquierda. Con **Editar** puedes modificar la rola, su intérprete o su álbum.
3. Con **Nueva persona…** puedes dar de alta personas, y desde la edición de un intérprete de tipo grupo puedes agregar sus **miembros**.
4. Para buscar, escribe en la barra de búsqueda y presiona **Enter**. **Limpiar** vuelve a mostrar todas las rolas.

Volver a minar **reemplaza toda la biblioteca**, incluidos los cambios que se hayan hecho.

### Lenguaje de búsqueda

| Búsqueda | Qué hace |
|---|---|
| `beatles` | Busca la palabra en el título, el intérprete y el álbum |
| `interprete: juanes` | Rolas cuyo intérprete contiene "juanes" |
| `titulo: "let it be"` | Los textos con espacios van entre comillas |
| `año > 2000` | Comparaciones de números: `=`, `!=`, `<`, `<=`, `>`, `>=` |
| `genero: rock o genero: pop` | `o` (también `\|\|`) |
| `interprete: beatles y año > 1965` | `y` (también `&&`) |
| `no album: unknown` | `no` (también `!`) |
| `(genero: rock o genero: pop) y año > 2000` | Los paréntesis agrupan |

Campos de texto: `titulo`, `interprete`, `album`, `genero`. Campos de número: `año`, `pista`. No importan las mayúsculas.

## Base de datos

La base de datos se guarda en:

```
~/.local/share/music-kotlin/musica.db
```

Se crea sola la primera vez que se abre el programa, con el esquema proporcionado en el curso. Para empezar desde cero, basta con borrar ese archivo.

## Estructura del proyecto

```
desktopApp/         Punto de entrada de la aplicación de escritorio (main.kt)
shared/src/
  commonMain/       Código común: modelo, compilador de búsquedas, vista, estado del
                    controlador, interfaz del repositorio e importador
  jvmMain/          Código exclusivo de escritorio: minero (lectura de MP3), conexión
                    a SQLite (BaseDeDatos, RepositorioJdbc) y ControladorPrincipal
  commonTest/       Pruebas del código común
  jvmTest/          Pruebas del minero, la base de datos, la búsqueda y el controlador
Reporte.pdf         Reporte del proyecto
```
