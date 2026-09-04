import { motion } from 'framer-motion'

interface GraphVisualizerProps {
  state: any;
}

// Hardcoded defaults for built-in algorithms
const DEFAULT_NODE_POSITIONS: Record<number, { x: number, y: number }> = {
  0: { x: 50, y: 150 },
  1: { x: 200, y: 50 },
  2: { x: 200, y: 250 },
  3: { x: 350, y: 50 },
  4: { x: 350, y: 250 },
  5: { x: 500, y: 150 },
}

const DEFAULT_EDGES = [
  { from: 0, to: 1, weight: 4 },
  { from: 0, to: 2, weight: 2 },
  { from: 1, to: 3, weight: 5 },
  { from: 1, to: 2, weight: 1 },
  { from: 2, to: 1, weight: 1 },
  { from: 2, to: 3, weight: 8 },
  { from: 2, to: 4, weight: 10 },
  { from: 3, to: 4, weight: 2 },
  { from: 3, to: 5, weight: 6 },
  { from: 4, to: 5, weight: 3 },
]

export default function GraphVisualizer({ state }: GraphVisualizerProps) {
  const visited = state.visited || []
  
  // Support both currentNode/neighbor (BFS/DFS) and activeEdge (Bellman-Ford, Kruskal)
  const currentNode = state.currentNode !== undefined ? state.currentNode : (state.activeEdge ? state.activeEdge[0] : undefined)
  const neighbor = state.neighbor !== undefined ? state.neighbor : (state.activeEdge ? state.activeEdge[1] : undefined)
  
  const distances = state.distances || {}
  
  const nodePositions = state.nodes || DEFAULT_NODE_POSITIONS
  const edges = state.edges || DEFAULT_EDGES

  const getNodeColor = (nodeId: number) => {
    if (nodeId === currentNode) return 'var(--warning)'
    if (nodeId === neighbor) return 'var(--accent-primary)'
    if (visited.includes(nodeId)) return 'var(--success)'
    if (state.mst && state.mst.some((e: any[]) => e[0] === nodeId || e[1] === nodeId)) return 'var(--success)'
    return 'var(--bg-tertiary)'
  }

  const getEdgeColor = (from: number, to: number) => {
    if ((currentNode === from && neighbor === to) || (currentNode === to && neighbor === from)) return 'var(--warning)'
    if (state.mst && state.mst.some((e: any[]) => (e[0] === from && e[1] === to) || (e[0] === to && e[1] === from))) return 'var(--success)'
    if (visited.includes(from) && visited.includes(to)) return 'var(--success)'
    return 'var(--text-secondary)'
  }

  return (
    <div className="w-100 h-100 position-relative d-flex justify-content-center align-items-center bg-dark bg-opacity-25 rounded border border-secondary" style={{ minHeight: '400px' }}>
      
      {/* Edges */}
      <svg className="position-absolute top-0 start-0 w-100 h-100" style={{ pointerEvents: 'none' }}>
        {edges.map((edge: any, i: number) => {
          const fromPos = nodePositions[edge.from]
          const toPos = nodePositions[edge.to]
          
          return (
            <g key={`edge-${i}`}>
              <motion.line
                x1={fromPos.x + 25} // Offset for node radius
                y1={fromPos.y + 25}
                x2={toPos.x + 25}
                y2={toPos.y + 25}
                stroke={getEdgeColor(edge.from, edge.to)}
                strokeWidth={3}
                animate={{ stroke: getEdgeColor(edge.from, edge.to) }}
              />
              <text 
                x={(fromPos.x + toPos.x) / 2 + 25} 
                y={(fromPos.y + toPos.y) / 2 + 20}
                fill="var(--text-primary)"
                fontSize="12"
                fontWeight="bold"
                textAnchor="middle"
              >
                {edge.weight}
              </text>
            </g>
          )
        })}
      </svg>

      {/* Nodes */}
      {Object.entries(nodePositions).map(([idStr, pos]: [string, any]) => {
        const id = parseInt(idStr)
        const dist = distances[id]
        const displayDist = dist === 2147483647 || dist === undefined ? '∞' : dist

        return (
          <motion.div
            key={`node-${id}`}
            className="position-absolute rounded-circle d-flex justify-content-center align-items-center text-light fw-bold shadow"
            style={{
              width: '50px',
              height: '50px',
              left: `${pos.x}px`,
              top: `${pos.y}px`,
              border: '2px solid var(--border-color)',
            }}
            animate={{
              backgroundColor: getNodeColor(id),
              scale: id === currentNode || id === neighbor ? 1.2 : 1
            }}
          >
            {id}
            
            {/* Distance badge */}
            <div 
              className="position-absolute badge rounded-pill bg-dark border border-secondary"
              style={{ top: '-15px', right: '-15px' }}
            >
              {displayDist}
            </div>
          </motion.div>
        )
      })}
    </div>
  )
}
