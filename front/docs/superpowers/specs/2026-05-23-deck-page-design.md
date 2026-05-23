# Deck Page — Design Spec

**Fecha:** 2026-05-23  
**Feature:** Gestión y selección de mazos en el Lobby

## Contexto

El jugador necesita una página donde pueda ver, crear, editar, eliminar y seleccionar su mazo activo para jugar al Pokemon TCG. La ruta `/decks` dentro del lobby es el punto de entrada.

## Modelo de datos

```typescript
interface Deck {
  id: string;
  name: string;
  cardCount: number;
  coverCardImage: string; // URL de la imagen de portada
}
```

## Arquitectura

### `DeckService` — `src/app/shared/services/deck.service.ts`

Servicio singleton (`providedIn: 'root'`) que centraliza todo el estado y las operaciones sobre mazos.

**Signals:**
- `decks: Signal<Deck[]>` — lista de mazos del jugador
- `selectedDeckId: Signal<string | null>` — id del deck activo para jugar

**Métodos (simulados con mock, listos para reemplazarse con llamadas HTTP):**
- `loadDecks(): Promise<void>` — carga la lista de mazos
- `createDeck(name: string): Promise<void>` — crea un nuevo mazo
- `updateDeck(id: string, name: string): Promise<void>` — edita el nombre
- `deleteDeck(id: string): Promise<void>` — elimina el mazo
- `selectDeck(id: string): Promise<void>` — POST al backend con el deck seleccionado, actualiza `selectedDeckId`

Los datos mock incluyen al menos 3 mazos de ejemplo con imágenes de cartas Pokémon reales (URLs públicas).

---

### `DeckCreateButtonComponent` — `src/app/pages/lobby/decks/components/deck-create-button/`

Primer elemento del flex wrap en la página. Mismo tamaño que las tarjetas de mazo.

**Visual:**
- Borde punteado, fondo sutil diferente (no imagen)
- Ícono `add` de Material grande centrado
- Texto "Nuevo Mazo" debajo
- Hover: escala suave (transform scale) como micro-animación

**Output:**
- `onCreate: EventEmitter<void>` — el padre decide qué hacer (abrir formulario, etc.)

---

### `DeckComponent` — `src/app/pages/lobby/decks/components/deck/` (refactor)

Refactor del componente existente para recibir el modelo completo y soporte de selección.

**Inputs:**
- `deck: Deck` — reemplaza el antiguo `text: string`
- `isSelected: boolean` — controla el estilo de selección activa

**Template:**
- `coverCardImage` como imagen de portada (parte superior de la tarjeta)
- `name` como título
- `cardCount` como subtítulo ("X cartas")
- Botones de editar (`edit`) y borrar (`delete`) con `matMiniFab`
- Botón/área clickeable para seleccionar el deck como activo

**Estado seleccionado (`isSelected === true`):**
- Borde dorado/amarillo grueso (color temático Pokémon)
- Badge con ícono `check_circle` superpuesto en la esquina superior derecha

**Outputs:**
- `onEdit: EventEmitter<Deck>`
- `onDelete: EventEmitter<string>` — emite el id
- `onSelect: EventEmitter<string>` — emite el id

---

### `DecksPage` — `src/app/pages/lobby/decks/`

Página orquestadora que consume el `DeckService`.

**Responsabilidades:**
- Llama a `loadDecks()` en `ngOnInit`
- Renderiza `DeckCreateButtonComponent` como primer elemento
- Renderiza un `@for` de `DeckComponent`, pasando `isSelected` comparando `deck.id` con `selectedDeckId()`
- Maneja los outputs: llama a los métodos del service correspondientes

**Layout:** `flex-wrap`, gap uniforme, `margin-top`.

---

## Comportamiento de selección

1. El jugador hace click sobre un `DeckComponent` (no en editar/borrar)
2. Se emite `onSelect` con el `id`
3. `DecksPage` llama a `DeckService.selectDeck(id)`
4. El service simula un POST al backend y actualiza `selectedDeckId`
5. El `DeckComponent` correspondiente reactiva su estado `isSelected`

Solo un deck puede estar seleccionado a la vez.

---

## Visual

### DeckCreateButtonComponent
```
┌──────────────────────┐
│                      │
│          ➕          │  ← mat-icon 'add' grande
│                      │
│     Nuevo Mazo       │
│                      │
└ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ┘  ← borde punteado
```

### DeckComponent (normal)
```
┌──────────────────────┐
│  [coverCardImage]    │
│──────────────────────│
│  Nombre del Mazo     │
│  60 cartas           │
│   [✏️]      [🗑️]    │
└──────────────────────┘
```

### DeckComponent (seleccionado)
```
╔══════════════════[✅]╗  ← badge check_circle top-right
║  [coverCardImage]    ║
║──────────────────────║
║  Nombre del Mazo     ║
║  60 cartas           ║
║   [✏️]      [🗑️]    ║
╚══════════════════════╝  ← borde dorado grueso
```

---

## Fuera de scope

- Formulario de creación/edición de mazo (solo se emite el evento `onCreate`/`onEdit`)
- Paginación de mazos
- Filtros o búsqueda
