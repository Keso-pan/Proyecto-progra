# Aventura RPG - Proyecto POO

Juego de rol por turnos (Guerrero/Arquero vs Goblins, Esqueletos, Magos y
Jefes) que implementa Herencia, Polimorfismo, Encapsulamiento, Clases
Abstractas, Composición y persistencia de datos, siguiendo la propuesta de
diseño UML del primer bimestre.

## Como abrir el proyecto

### Opcion A: NetBeans
1. Archivo → Nuevo Proyecto → Java with Existing Sources.
2. Selecciona esta carpeta (`rpg-proyecto`) y apunta la carpeta de fuentes a `src`.
3. Botón derecho sobre `com.rpg.Main` → Run File (para probar la lógica por consola).
4. Botón derecho sobre `com.rpg.gui.VentanaJuego` → Run File (para la GUI).

### Opcion B: IntelliJ IDEA
1. File → Open → selecciona `rpg-proyecto`.
2. Marca la carpeta `src` como "Sources Root" (botón derecho → Mark Directory as → Sources Root).
3. Ejecuta `com.rpg.Main` o `com.rpg.gui.VentanaJuego` con el botón ▶ verde.

### Opcion C: linea de comandos (si tienes JDK instalado)
```
cd rpg-proyecto
javac -d bin $(find src -name "*.java")
java -cp bin com.rpg.Main          # prueba de consola
java -cp bin com.rpg.gui.VentanaJuego   # GUI
```

## Estructura de paquetes

```
com.rpg
 ├── model         → Personaje (abstracta), Heroe/Enemigo (abstractas) y sus subclases
 ├── items         → Item, Inventario (composicion), ItemCatalogo (10 items)
 ├── combate       → Batalla (motor de combate), EfectoEstado, TipoAtaque
 ├── excepciones   → Excepciones personalizadas
 ├── persistencia  → GestorArchivos (guardado/carga por serializacion)
 ├── juego         → Juego (flujo general, generacion de enemigos)
 ├── gui           → VentanaJuego (Swing)
 └── Main          → prueba de consola sin GUI
```

## Estado actual (lo que YA esta implementado)

- Jerarquia completa: `Personaje` → `Heroe`/`Enemigo` → subclases concretas.
- Guerrero: critico (nivel 5), +20% vida / +10% danio (nivel 10), sangrado (nivel 15).
- Arquero: sangrado (nivel 5), flecha de hielo / congelar (nivel 10), triple disparo (nivel 15).
- Goblin debil a critico, Esqueleto debil a hielo, Mago sin debilidades.
- Rey Goblin: doble turno inicial + invoca refuerzos. Rey Mago: probabilidad de curacion.
- Inventario maximo 5 items, catalogo de 10 items distintos.
- Persistencia por serializacion (`guardado/heroe.dat`).
- Excepciones propias: `InventarioLlenoException`, `ItemInvalidoException`, `NivelInsuficienteException`.
- GUI funcional de un solo panel (`VentanaJuego`): elegir clase, atacar, ver barras de vida,
  usar inventario, guardar y cargar.

## Pendiente / siguientes pasos sugeridos

1. **Separar la GUI** en las 4 ventanas del diseño original (`VentanaInicio`,
   `VentanaBatalla`, `VentanaInventario`) si quieren fidelidad total al diagrama —
   ahora mismo todo vive en `VentanaJuego` para tener algo jugable rapido.
2. Agregar jefes al flujo de `Juego` (por ejemplo, cada 5 rondas llamar a `generarJefe()`).
3. Actualizar el diagrama de clases UML final reflejando el codigo real (agregar
   `TipoAtaque`, `EfectoEstado`, las excepciones, etc.) para la entrega.
4. Escribir el README de entrega con capturas de pantalla y explicacion de
   SOLID aplicado (Open/Closed en `Item`/`ItemCatalogo`, Single Responsibility
   en `GestorArchivos` vs `Batalla`, etc.).

## Nota sobre compilacion

Este proyecto fue escrito y revisado cuidadosamente pero **no pudo compilarse
en este entorno** (solo cuenta con JRE, sin acceso a internet para instalar el
JDK). Compilalo en NetBeans o IntelliJ como se indica arriba antes de la
entrega, y avisame si aparece algun error para corregirlo.
