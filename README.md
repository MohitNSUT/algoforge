# AlgoForge 

**AlgoForge** is an interactive, full-stack engineering platform designed to visualize, execute, and benchmark advanced data structures and algorithms in real-time. 

Unlike standard visualization tools, AlgoForge computes algorithms step-by-step on a Java Spring Boot backend, serializes the memory state, and streams the execution timeline to a dynamic React frontend.

<img width="2940" height="1410" alt="image" src="https://github.com/user-attachments/assets/2f867359-da6c-49d4-b5be-60dae7dd7ad6" />


---

## Key Features

*   **17 Advanced Algorithms**: Visualizes complex algorithms including Dijkstra's, A*, Kahn’s Topological Sort, Kruskal's, Bellman-Ford, and various sorting/searching arrays.
*   **Dynamic JSON Configuration**: Users can input custom, raw JSON payloads to dynamically construct complex graphs (nodes, edges, weights) or arrays to test edge cases.
*   **Step-by-Step Playback**: Features a fully scrubbable timeline with adjustable playback speeds, rendering memory state transitions in real-time using Framer Motion.
*   **Remote Code Playground**: An integrated Monaco Editor IDE that utilizes Java `ProcessBuilder` to dynamically compile and execute arbitrary C++, Python, and Java scripts on the backend host.

---

## Architecture & Tech Stack

AlgoForge follows a decoupled Client-Server Microservice architecture.

### Frontend (Client)
*   **Framework**: React 18, Vite
*   **Language**: TypeScript
*   **Styling**: Vanilla CSS (Custom dark-mode variables), Bootstrap (Grid)
*   **Animation**: Framer Motion
*   **Editor**: Monaco Editor

### Backend (Computation Engine)
*   **Framework**: Spring Boot 3.x
*   **Language**: Java 21
*   **Build Tool**: Gradle
*   **Core Logic**: Object-Oriented algorithm runners implementing a generic `AlgorithmRunner` interface. Jackson is used for complex state serialization.

---

## Getting Started

### Prerequisites
*   [Node.js](https://nodejs.org/) (v18+)
*   [Java Development Kit (JDK)](https://adoptium.net/) (v21+)
*   Python 3 & g++ (For the Code Playground execution)

### Running the Backend (Spring Boot)
1. Open a terminal and navigate to the backend directory:
   ```bash
   cd backend
   ```
2. Start the Spring Boot server using Gradle:
   ```bash
   ./gradlew bootRun
   ```
   *The server will start on `http://localhost:8080`*

### Running the Frontend (React + Vite)
1. Open a second terminal and navigate to the frontend directory:
   ```bash
   cd frontend
   ```
2. Install dependencies:
   ```bash
   npm install
   ```
3. Start the Vite development server:
   ```bash
   npm run dev
   ```
   *The application will be accessible at `http://localhost:5173`*

---

## Project Structure

```text
algoforge/
├── backend/                  # Spring Boot Computation Engine
│   └── src/main/java/com/algoforge/
│       ├── algorithm/        # Core DSA implementations (Graph, Sorting)
│       ├── controller/       # REST API Endpoints
│       └── service/          # Business logic and JSON routing
└── frontend/                 # React UI
    └── src/
        ├── components/       # Graph and Array Visualizer components
        ├── pages/            # Dashboard, Visualizer, and Playground
        └── utils/            # Algorithm step definitions
```

---

## Contributing
Contributions, issues, and feature requests are welcome! Feel free to check the [issues page](https://github.com/MohitNSUT/algoforge/issues).

##  License
This project is open-source and available under the [MIT License](LICENSE).
