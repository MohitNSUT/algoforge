import { ArrowRight, Code2, GitMerge, Activity, CheckCircle2 } from 'lucide-react'
import { Link } from 'react-router-dom'
import { motion } from 'framer-motion'

export default function Dashboard() {
  return (
    <div className="container mx-auto py-5">
      <motion.div
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        className="row align-items-center mb-5 pb-5"
      >
        <div className="col-lg-7">
          <h1 className="display-3 fw-bold mb-4" style={{ letterSpacing: '-1px' }}>
            Understand Algorithms.<br />
            <span style={{ color: 'var(--accent-primary)' }}>Don't Just Run Them.</span>
          </h1>
          <p className="lead text-muted mb-5 w-75">
            AlgoForge is an interactive engineering platform to visualize, execute, and benchmark data structures and algorithms with step-by-step state representations.
          </p>
          <div className="d-flex gap-3">
            <Link to="/algorithms" className="btn btn-primary btn-lg rounded-pill px-5 d-flex align-items-center gap-2">
              Explore Algorithms <ArrowRight size={20} />
            </Link>
            <Link to="/playground" className="btn btn-outline-primary btn-lg rounded-pill px-5">
              Open Compiler
            </Link>
          </div>
        </div>
      </motion.div>

      <div className="row g-4 mt-5">
        {[
          { title: 'Algorithms', count: '17', icon: <Activity className="text-primary" /> },
          { title: 'Categories', count: '3', icon: <GitMerge className="text-success" /> },
          { title: 'Data Structures', count: '2', icon: <Code2 className="text-warning" /> },
          { title: 'Executions', count: '10k+', icon: <CheckCircle2 className="text-info" /> },
        ].map((stat, idx) => (
          <div key={idx} className="col-md-3">
            <motion.div
              initial={{ opacity: 0, scale: 0.9 }}
              animate={{ opacity: 1, scale: 1 }}
              transition={{ delay: 0.2 + (idx * 0.1) }}
              className="card h-100 p-4 border-0"
              style={{ backgroundColor: 'var(--bg-secondary)', border: '1px solid var(--border-color)' }}
            >
              <div className="d-flex align-items-center justify-content-between mb-3">
                <h6 className="text-muted mb-0 fw-semibold text-uppercase" style={{ letterSpacing: '1px', fontSize: '12px' }}>{stat.title}</h6>
                <div className="p-2 bg-dark rounded">{stat.icon}</div>
              </div>
              <h2 className="display-6 fw-bold mb-0 text-primary">{stat.count}</h2>
            </motion.div>
          </div>
        ))}
      </div>
    </div>
  )
}
