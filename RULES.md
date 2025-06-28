## 🧭 Code Style & Architecture Guidelines

To ensure clean, scalable, and maintainable code across the project, please follow the conventions below:

---

### 🖥 Frontend (React + TypeScript)

#### 🔹 Structure & Organization

- Use **one component per file** (`.tsx`). Exception: small and tightly related subcomponents may be grouped together.
- Follow folder structure:
  ```
  src/
  ├── hooks/
  ├── providers/
  ├── services/
  ├── utils/
  └── view/
  ```
- **No business logic** like API calls inside `view` components – use `services` for logic and `hooks` for state abstraction.
- Only `.ts` files inside `services/` and `hookes/` – do not place UI or JSX here.
- **Avoid `.css` files** – prefer [TailwindCSS](https://tailwindcss.com/docs) utility classes. Only use CSS files if necessary (e.g. complex animations or third-party overrides).

#### 🔹 Code Conventions

- Use **named exports** by default.
  - ✅ Good: `export const GameCard = () => { ... }`
  - ❌ Avoid: `export default GameCard`
- Use `default export` **only** in:
  - Root pages (e.g., `App.tsx`)
  - Service initializers (e.g., `webSocketService.ts`)
- Keep components clean and focused – extract logic into custom hooks or utility functions when needed.

---

### ☕ Backend (Spring Boot + Java)

#### 🔹 Structure & Code Splitting

- Prefer **small, focused classes** over large monoliths.
- Use **Object-Oriented Design (OOP)** – extract shared logic into base controllers/services and extend as needed.
- Example pattern for controllers:

```java
// GameController.java – Base controller
@Controller
@RequestMapping("/api/v1/game")
@MessageMapping("/game")
public class GameController {
    @Autowired protected GameRepository gameRepository;
    @Autowired protected PlayerRepository playerRepository;
    @Autowired protected SimpMessagingTemplate messagingTemplate;
    ...
}
```

```java
// GameStartController.java – Extends base controller
@RestController
public class GameStartController extends GameController {
    ...
}
```

#### 🔹 General Backend Conventions

- **@RestController** for REST endpoints, **@Controller** for WebSocket or hybrid usage.
- Service logic should live inside `service/` packages – avoid logic in controllers.
- Keep **entities clean** – avoid putting logic / methods there.
- Try to use annotations to keep the code cleaner.
- Use **repositories only in controllers/services**, never directly from WebSocket handlers.

---