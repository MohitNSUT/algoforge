import { Routes, Route, Link, useLocation } from 'react-router-dom'
import { useState, useEffect } from 'react'
import { Activity, Code2, LayoutDashboard, Cpu } from 'lucide-react'
import axios from 'axios'
import Dashboard from './pages/Dashboard'
import AlgorithmExplorer from './pages/AlgorithmExplorer'
import AlgorithmVisualizer from './pages/AlgorithmVisualizer'
import Playground from './pages/Playground'

function App() {
  const [health, setHealth] = useState<boolean | null>(null)
  const location = useLocation()

  useEffect(() => {
    axios.get('/api/health')
      .then(() => setHealth(true))
      .catch(() => setHealth(false))
  }, [])

  return (
    <div className="d-flex flex-column min-vh-100">
      <nav className="navbar navbar-expand-lg sticky-top px-4">
        <div className="container-fluid">
          <Link className="navbar-brand d-flex align-items-center gap-2" to="/">
            <Cpu size={24} className="text-primary" />
            AlgoForge
          </Link>
          <button className="navbar-toggler border-0" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav">
            <span className="navbar-toggler-icon"></span>
          </button>

          <div className="collapse navbar-collapse" id="navbarNav">
            <ul className="navbar-nav me-auto mb-2 mb-lg-0 ms-4 gap-2">
              <li className="nav-item">
                <Link className={`nav-link d-flex align-items-center gap-2 ${location.pathname === '/' ? 'active text-primary' : ''}`} to="/">
                  <LayoutDashboard size={18} />
                  Dashboard
                </Link>
              </li>
              <li className="nav-item">
                <Link className={`nav-link d-flex align-items-center gap-2 ${location.pathname.startsWith('/algorithms') ? 'active text-primary' : ''}`} to="/algorithms">
                  <Activity size={18} />
                  Algorithms
                </Link>
              </li>
              <li className="nav-item">
                <Link className={`nav-link d-flex align-items-center gap-2 ${location.pathname.startsWith('/playground') ? 'active text-primary' : ''}`} to="/playground">
                  <Code2 size={18} />
                  Playground
                </Link>
              </li>
            </ul>
            <div className="d-flex align-items-center gap-3">
              <div className="d-flex align-items-center gap-2 text-muted small">
                <div className={`rounded-circle ${health === true ? 'bg-success' : health === false ? 'bg-danger' : 'bg-warning'}`} style={{ width: '8px', height: '8px' }}></div>
                {health === true ? 'Engine Online' : health === false ? 'Engine Offline' : 'Connecting...'}
              </div>
              <button className="btn btn-primary btn-sm px-4 rounded-pill">Sign In</button>
            </div>
          </div>
        </div>
      </nav>

      <main className="flex-grow-1 p-4 d-flex flex-column">
        <Routes>
          <Route path="/" element={<Dashboard />} />
          <Route path="/algorithms" element={<AlgorithmExplorer />} />
          <Route path="/algorithms/:slug" element={<AlgorithmVisualizer />} />
          <Route path="/playground" element={<Playground />} />
        </Routes>
      </main>
    </div>
  )
}

export default App
