import { useState, useEffect, useRef } from 'react'
import { useParams, Link } from 'react-router-dom'
import { ArrowLeft, Play, Pause, SkipBack, SkipForward, Settings, RotateCcw } from 'lucide-react'
import axios from 'axios'
import ArrayVisualizer from '../components/ArrayVisualizer.tsx'
import GraphVisualizer from '../components/GraphVisualizer.tsx'
import { ALGORITHM_STEPS } from '../utils/algorithmSteps.ts'

// Interfaces
interface ExecutionStep {
  step: number;
  operation: string;
  description: string;
  state: any;
}

export default function AlgorithmVisualizer() {
  const { slug } = useParams<{ slug: string }>()
  const [steps, setSteps] = useState<ExecutionStep[]>([])
  const [currentStepIndex, setCurrentStepIndex] = useState(0)
  const [isPlaying, setIsPlaying] = useState(false)
  const [speed, setSpeed] = useState(1000)
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)
  const [activeTab, setActiveTab] = useState<'steps' | 'input' | 'state'>('steps')
  
  const timerRef = useRef<number | null>(null)

  const getDefaultPayload = (slug: string) => {
    if (['bubble-sort', 'merge-sort', 'quick-sort', 'insertion-sort', 'selection-sort'].includes(slug)) {
      return { array: [64, 34, 25, 12, 22, 11, 90, 45, 78, 5, 55, 39, 81] }
    } else if (slug === 'binary-search') {
      return { array: [5, 11, 12, 22, 25, 34, 39, 45, 55, 64, 78, 81, 90], target: 55 }
    } else if (slug === 'dijkstra' || slug === 'prim') {
      return {
        numNodes: 6,
        startNode: 0,
        adjacencyList: {
          0: [[1, 4], [2, 2]],
          1: [[3, 5], [2, 1]],
          2: [[1, 1], [3, 8], [4, 10]],
          3: [[4, 2], [5, 6]],
          4: [[5, 3]],
          5: []
        }
      }
    } else if (['bfs', 'dfs', 'kahn', 'dfs-topo'].includes(slug)) {
      return {
        numNodes: 6,
        startNode: 0,
        adjacencyList: {
          0: [1, 2],
          1: [3],
          2: [1, 4],
          3: [4, 5],
          4: [5],
          5: []
        }
      }
    } else if (slug === 'bellman-ford') {
      return {
        numNodes: 5,
        startNode: 0,
        edges: [
          [0, 1, -1], [0, 2, 4],
          [1, 2, 3], [1, 3, 2], [1, 4, 2],
          [3, 2, 5], [3, 1, 1], [4, 3, -3]
        ]
      }
    } else if (slug === 'kruskal') {
      return {
         numNodes: 4,
         edges: [
           [0, 1, 10], [0, 2, 6], [0, 3, 5],
           [1, 3, 15], [2, 3, 4]
         ]
      }
    } else if (slug === 'floyd-warshall') {
      const INF = 99999;
      return {
        numNodes: 4,
        graph: [
          [0, 5, INF, 10],
          [INF, 0, 3, INF],
          [INF, INF, 0, 1],
          [INF, INF, INF, 0]
        ]
      }
    } else if (slug === 'a-star') {
      return {
        numNodes: 6,
        startNode: 0,
        targetNode: 5,
        adjacencyList: {
          0: [[1, 4], [2, 2]],
          1: [[3, 5], [2, 1]],
          2: [[1, 1], [3, 8], [4, 10]],
          3: [[4, 2], [5, 6]],
          4: [[5, 3]],
          5: []
        },
        coordinates: {
          0: [0, 0], 1: [2, 4], 2: [2, 1], 
          3: [5, 4], 4: [5, 1], 5: [8, 2]
        }
      }
    } else if (slug === 'union-find') {
      return {
        numNodes: 5,
        operations: [
          [0, 1], [2, 3], [1, 2], [0, 4]
        ]
      }
    }
    return {}
  }

  const [payloadJson, setPayloadJson] = useState(() => JSON.stringify(getDefaultPayload(slug || ''), null, 2))

  useEffect(() => {
    const initialPayload = getDefaultPayload(slug || '')
    setPayloadJson(JSON.stringify(initialPayload, null, 2))
    fetchExecutionData(initialPayload)
    return () => stopPlayback()
  }, [slug])

  useEffect(() => {
    if (isPlaying) {
      timerRef.current = window.setTimeout(() => {
        if (currentStepIndex < steps.length - 1) {
          setCurrentStepIndex(prev => prev + 1)
        } else {
          setIsPlaying(false)
        }
      }, speed)
    }
    return () => {
      if (timerRef.current) clearTimeout(timerRef.current)
    }
  }, [isPlaying, currentStepIndex, speed, steps.length])

  const fetchExecutionData = async (payloadToUse?: any) => {
    setLoading(true)
    setError(null)
    setIsPlaying(false)
    setCurrentStepIndex(0)
    
    try {
      let payload = payloadToUse
      if (!payload) {
        try {
          payload = JSON.parse(payloadJson)
        } catch (e) {
          setError("Invalid JSON format in payload configuration")
          setLoading(false)
          return
        }
      }

      const res = await axios.post(`/api/algorithms/${slug}/execute`, payload)
      if (res.data.success) {
        setSteps(res.data.data)
      } else {
        setError(res.data.message)
      }
    } catch (err: any) {
      setError(err.response?.data?.message || err.message || 'Failed to execute algorithm')
    } finally {
      setLoading(false)
    }
  }

  const togglePlayback = () => {
    if (currentStepIndex >= steps.length - 1) {
      setCurrentStepIndex(0)
    }
    setIsPlaying(!isPlaying)
  }

  const stopPlayback = () => {
    if (timerRef.current) clearTimeout(timerRef.current)
    setIsPlaying(false)
  }

  const stepForward = () => {
    stopPlayback()
    if (currentStepIndex < steps.length - 1) setCurrentStepIndex(prev => prev + 1)
  }

  const stepBackward = () => {
    stopPlayback()
    if (currentStepIndex > 0) setCurrentStepIndex(prev => prev - 1)
  }
  
  const reset = () => {
    stopPlayback()
    setCurrentStepIndex(0)
  }

  const currentStep = steps[currentStepIndex]

  return (
    <div className="container-fluid py-2 h-100 d-flex flex-column" style={{ minHeight: 'calc(100vh - 80px)' }}>
      <div className="d-flex justify-content-between align-items-center mb-4">
        <div className="d-flex align-items-center gap-3">
          <Link to="/algorithms" className="btn btn-outline-secondary rounded-circle p-2 border-0">
            <ArrowLeft size={20} />
          </Link>
          <div>
            <h3 className="mb-0 fw-bold text-capitalize">{slug?.replace('-', ' ')}</h3>
            <span className="text-muted small">Algorithm Visualization</span>
          </div>
        </div>
        
        <div className="bg-dark p-2 d-flex align-items-center gap-2 rounded border border-secondary" style={{ backgroundColor: 'var(--bg-secondary) !important' }}>
          <button className="btn btn-dark btn-sm" onClick={stepBackward} disabled={currentStepIndex === 0}>
            <SkipBack size={16} />
          </button>
          <button 
            className={`btn btn-sm ${isPlaying ? 'btn-danger' : 'btn-primary'} px-4`} 
            onClick={togglePlayback}
            disabled={steps.length === 0}
          >
            {isPlaying ? <Pause size={16} /> : <Play size={16} className="ms-1" />}
          </button>
          <button className="btn btn-dark btn-sm" onClick={stepForward} disabled={currentStepIndex >= steps.length - 1}>
            <SkipForward size={16} />
          </button>
          <div className="vr mx-2 bg-secondary opacity-25"></div>
          <button className="btn btn-dark btn-sm" onClick={reset}>
            <RotateCcw size={16} />
          </button>
          <div className="dropdown">
            <button className="btn btn-dark btn-sm dropdown-toggle" data-bs-toggle="dropdown">
              <Settings size={16} />
            </button>
            <ul className="dropdown-menu dropdown-menu-dark dropdown-menu-end">
              <li><button className="dropdown-item" onClick={() => setSpeed(1000)}>0.5x Speed</button></li>
              <li><button className="dropdown-item" onClick={() => setSpeed(500)}>1.0x Speed</button></li>
              <li><button className="dropdown-item" onClick={() => setSpeed(200)}>2.0x Speed</button></li>
              <li><button className="dropdown-item" onClick={() => setSpeed(50)}>Max Speed</button></li>
            </ul>
          </div>
        </div>
      </div>

      <div className="row flex-grow-1 g-4">
        <div className="col-lg-8 d-flex flex-column">
          <div className="bg-dark rounded border border-secondary flex-grow-1 p-4 d-flex flex-column position-relative overflow-hidden" style={{ backgroundColor: 'var(--bg-secondary) !important' }}>
            {loading && (
              <div className="position-absolute top-0 start-0 w-100 h-100 d-flex justify-content-center align-items-center bg-dark bg-opacity-75 z-3">
                <div className="spinner-border text-primary" role="status">
                  <span className="visually-hidden">Loading...</span>
                </div>
              </div>
            )}
            
            {error && (
              <div className="alert alert-danger m-4">
                <strong>Error:</strong> {error}
              </div>
            )}

            {!loading && !error && steps.length > 0 && currentStep && (
              <div className="w-100 h-100 d-flex flex-column">
                <div className="mb-4">
                  <span className="badge bg-primary mb-2">Step {currentStep.step} / {steps.length}</span>
                  <h5 className="fw-bold mb-1">{currentStep.operation.replace(/_/g, ' ')}</h5>
                  <p className="text-muted mb-0">{currentStep.description}</p>
                </div>
                
                <div className="flex-grow-1 d-flex justify-content-center align-items-center">
                  {(['bubble-sort', 'merge-sort', 'quick-sort', 'insertion-sort', 'selection-sort', 'binary-search'].includes(slug || '')) && (
                    <ArrayVisualizer state={currentStep.state} slug={slug || ''} />
                  )}
                  {(['dijkstra', 'bfs', 'dfs', 'prim', 'bellman-ford', 'kruskal', 'floyd-warshall', 'a-star', 'union-find', 'kahn', 'dfs-topo'].includes(slug || '')) && (
                    <GraphVisualizer state={currentStep.state} />
                  )}
                </div>
              </div>
            )}
          </div>
          
          <div className="mt-4">
            <input 
              type="range" 
              className="form-range" 
              min="0" 
              max={steps.length > 0 ? steps.length - 1 : 0} 
              value={currentStepIndex}
              onChange={(e) => {
                stopPlayback()
                setCurrentStepIndex(parseInt(e.target.value))
              }}
              disabled={steps.length === 0}
            />
          </div>
        </div>
        
        <div className="col-lg-4 d-flex flex-column h-100">
          <div className="bg-dark rounded border border-secondary flex-grow-1 p-4 d-flex flex-column overflow-hidden" style={{ backgroundColor: 'var(--bg-secondary) !important', maxHeight: 'calc(100vh - 120px)' }}>
            
            <ul className="nav nav-pills mb-4 border-bottom border-secondary pb-3 flex-nowrap" style={{ overflowX: 'auto', whiteSpace: 'nowrap' }}>
              <li className="nav-item">
                <button className={`nav-link px-3 py-1 me-2 rounded-pill ${activeTab === 'steps' ? 'active' : 'text-light bg-secondary opacity-75'}`} onClick={() => setActiveTab('steps')} style={{ fontSize: '12px' }}>Steps</button>
              </li>
              <li className="nav-item">
                <button className={`nav-link px-3 py-1 me-2 rounded-pill ${activeTab === 'input' ? 'active' : 'text-light bg-secondary opacity-75'}`} onClick={() => setActiveTab('input')} style={{ fontSize: '12px' }}>Input</button>
              </li>
              <li className="nav-item">
                <button className={`nav-link px-3 py-1 rounded-pill ${activeTab === 'state' ? 'active' : 'text-light bg-secondary opacity-75'}`} onClick={() => setActiveTab('state')} style={{ fontSize: '12px' }}>State</button>
              </li>
            </ul>

            <div className="flex-grow-1 overflow-auto pe-2" style={{ scrollbarWidth: 'thin' }}>
              {activeTab === 'steps' && (
                <div>
                  <h6 className="fw-bold mb-3">Algorithm Logic</h6>
                  {ALGORITHM_STEPS[slug || ''] ? ALGORITHM_STEPS[slug || ''].map((step, idx) => (
                    <div key={idx} className="mb-2 p-2 bg-secondary bg-opacity-25 rounded border border-secondary text-light" style={{ fontSize: '13px' }}>
                      {step}
                    </div>
                  )) : (
                    <div className="text-muted fst-italic">Steps not available.</div>
                  )}
                </div>
              )}

              {activeTab === 'input' && (
                <div className="d-flex flex-column h-100">
                  <h6 className="fw-bold mb-3">JSON Configuration</h6>
                  <textarea 
                    className="form-control bg-dark text-light border-secondary font-monospace small mb-3 p-3 flex-grow-1" 
                    value={payloadJson}
                    onChange={(e) => setPayloadJson(e.target.value)}
                    style={{ resize: 'none', minHeight: '300px' }}
                  />
                  <button className="btn btn-primary w-100 fw-bold d-flex justify-content-center align-items-center gap-2 mt-auto" onClick={() => fetchExecutionData()}>
                    <Play size={16} /> Run Custom Input
                  </button>
                </div>
              )}

              {activeTab === 'state' && (
                <div>
                  <h6 className="fw-bold mb-3">Live Memory State</h6>
                  {currentStep ? (
                    <div className="font-monospace small">
                      {Object.entries(currentStep.state).map(([key, value]) => {
                        if (key === 'array') return null; // Array visualizer handles this
                        return (
                          <div key={key} className="mb-3">
                            <div className="text-muted mb-1 text-uppercase" style={{ fontSize: '10px' }}>{key}</div>
                            <div className="bg-dark p-2 rounded text-light border border-secondary" style={{ overflowX: 'auto' }}>
                              {typeof value === 'object' ? JSON.stringify(value, null, 2) : String(value)}
                            </div>
                          </div>
                        )
                      })}
                    </div>
                  ) : (
                    <div className="text-muted fst-italic">Run algorithm to see state</div>
                  )}
                </div>
              )}
            </div>
          </div>
        </div>
      </div>
    </div>
  )
}
