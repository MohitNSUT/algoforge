import { useState } from 'react'
import { motion } from 'framer-motion'
import { Link } from 'react-router-dom'
import { Search, Filter, Play } from 'lucide-react'

const algorithms = [
  {
    id: 'dijkstra',
    name: "Dijkstra's Algorithm",
    category: 'Graphs',
    description: 'Finds the shortest paths between nodes in a graph.',
    time: 'O((V+E)logV)',
    space: 'O(V)',
    color: 'var(--text-primary)'
  },
  {
    id: 'bellman-ford',
    name: "Bellman-Ford",
    category: 'Graphs',
    description: 'Computes shortest paths from a single source vertex to all of the other vertices. Handles negative weights.',
    time: 'O(V×E)',
    space: 'O(V)',
    color: 'var(--text-primary)'
  },
  {
    id: 'floyd-warshall',
    name: "Floyd-Warshall",
    category: 'Graphs',
    description: 'Finds shortest paths in a directed weighted graph with positive or negative edge weights.',
    time: 'O(V³)',
    space: 'O(V²)',
    color: 'var(--text-primary)'
  },
  {
    id: 'a-star',
    name: "A* Search",
    category: 'Graphs',
    description: 'Finds the shortest path from a start node to a goal node using heuristics.',
    time: 'O(E)',
    space: 'O(V)',
    color: 'var(--text-primary)'
  },
  {
    id: 'kruskal',
    name: "Kruskal's MST",
    category: 'Graphs',
    description: 'Finds a minimum spanning forest of an undirected edge-weighted graph.',
    time: 'O(E log E)',
    space: 'O(V)',
    color: 'var(--text-primary)'
  },
  {
    id: 'prim',
    name: "Prim's MST",
    category: 'Graphs',
    description: 'Finds a minimum spanning tree for a weighted undirected graph.',
    time: 'O((V+E)logV)',
    space: 'O(V)',
    color: 'var(--text-primary)'
  },
  {
    id: 'bfs',
    name: 'Breadth-First Search',
    category: 'Graphs',
    description: 'Explores nodes level by level using a queue.',
    time: 'O(V+E)',
    space: 'O(V)',
    color: 'var(--text-primary)'
  },
  {
    id: 'dfs',
    name: 'Depth-First Search',
    category: 'Graphs',
    description: 'Explores as far as possible along each branch before backtracking.',
    time: 'O(V+E)',
    space: 'O(V)',
    color: 'var(--text-primary)'
  },
  {
    id: 'kahn',
    name: 'Topological Sort (Kahn)',
    category: 'Graphs',
    description: 'Linear ordering of vertices such that for every directed edge u v, vertex u comes before v.',
    time: 'O(V+E)',
    space: 'O(V)',
    color: 'var(--text-primary)'
  },
  {
    id: 'dfs-topo',
    name: 'Topological Sort (DFS)',
    category: 'Graphs',
    description: 'Uses DFS post-order traversal to generate a topological sort.',
    time: 'O(V+E)',
    space: 'O(V)',
    color: 'var(--text-primary)'
  },
  {
    id: 'union-find',
    name: 'Union-Find / DSU',
    category: 'Graphs',
    description: 'Tracks a set of elements partitioned into a number of disjoint subsets.',
    time: 'O(α(n))',
    space: 'O(N)',
    color: 'var(--text-primary)'
  },
  {
    id: 'merge-sort',
    name: 'Merge Sort',
    category: 'Sorting',
    description: 'A divide and conquer algorithm that splits arrays and merges them.',
    time: 'O(n log n)',
    space: 'O(n)',
    color: 'var(--text-primary)'
  },
  {
    id: 'quick-sort',
    name: 'Quick Sort',
    category: 'Sorting',
    description: 'Partitions array around a pivot and recursively sorts subarrays.',
    time: 'O(n log n)',
    space: 'O(log n)',
    color: 'var(--text-primary)'
  },
  {
    id: 'bubble-sort',
    name: 'Bubble Sort',
    category: 'Sorting',
    description: 'A simple sorting algorithm that repeatedly steps through the list.',
    time: 'O(n²)',
    space: 'O(1)',
    color: 'var(--text-primary)'
  },
  {
    id: 'insertion-sort',
    name: 'Insertion Sort',
    category: 'Sorting',
    description: 'Builds the final sorted array one item at a time.',
    time: 'O(n²)',
    space: 'O(1)',
    color: 'var(--text-primary)'
  },
  {
    id: 'selection-sort',
    name: 'Selection Sort',
    category: 'Sorting',
    description: 'Repeatedly finds the minimum element and places it at the beginning.',
    time: 'O(n²)',
    space: 'O(1)',
    color: 'var(--text-primary)'
  },
  {
    id: 'binary-search',
    name: 'Binary Search',
    category: 'Searching',
    description: 'Search a sorted array by repeatedly dividing the search interval in half.',
    time: 'O(log n)',
    space: 'O(1)',
    color: 'var(--text-primary)'
  }
]

export default function AlgorithmExplorer() {
  const [searchQuery, setSearchQuery] = useState('')
  const [selectedCategory, setSelectedCategory] = useState<string | null>(null)

  const categories = Array.from(new Set(algorithms.map(a => a.category)))

  const filteredAlgorithms = algorithms.filter(algo => {
    const matchesSearch = algo.name.toLowerCase().includes(searchQuery.toLowerCase()) || 
                          algo.description.toLowerCase().includes(searchQuery.toLowerCase())
    const matchesCategory = selectedCategory ? algo.category === selectedCategory : true
    return matchesSearch && matchesCategory
  })

  return (
    <div className="container mx-auto py-5">
      <div className="d-flex justify-content-between align-items-center mb-5 border-bottom border-secondary pb-4">
        <div>
          <h2 className="fw-bold mb-1 letter-spacing-tight">Algorithm Explorer</h2>
          <p className="text-muted mb-0">Select an algorithm to visualize and execute.</p>
        </div>
        
        <div className="d-flex gap-3">
          <div className="position-relative">
            <Search className="position-absolute top-50 translate-middle-y ms-3 text-muted" size={16} />
            <input 
              type="text" 
              className="form-control bg-dark border-secondary text-light rounded-pill ps-5 pe-4 py-2" 
              placeholder="Search algorithms..."
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              style={{ width: '250px', fontSize: '14px' }}
            />
          </div>
          
          <div className="dropdown">
            <button className="btn btn-dark rounded-pill d-flex align-items-center gap-2" data-bs-toggle="dropdown" aria-expanded="false" style={{ fontSize: '14px' }}>
              <Filter size={16} /> {selectedCategory || 'All Categories'}
            </button>
            <ul className="dropdown-menu dropdown-menu-dark border-secondary shadow-lg">
              <li><button className="dropdown-item" onClick={() => setSelectedCategory(null)}>All Categories</button></li>
              <li><hr className="dropdown-divider border-secondary" /></li>
              {categories.map(cat => (
                <li key={cat}>
                  <button className="dropdown-item" onClick={() => setSelectedCategory(cat)}>{cat}</button>
                </li>
              ))}
            </ul>
          </div>
        </div>
      </div>

      {filteredAlgorithms.length === 0 ? (
        <div className="text-center text-muted py-5 mt-5">
          <Filter size={48} className="mb-3 opacity-50" />
          <h5>No algorithms found</h5>
          <p>Try adjusting your search or category filter.</p>
          <button className="btn btn-outline-primary mt-3" onClick={() => { setSearchQuery(''); setSelectedCategory(null); }}>
            Clear Filters
          </button>
        </div>
      ) : (
        <div className="row g-4">
          {filteredAlgorithms.map((algo, i) => (
            <div key={algo.id} className="col-md-6 col-lg-4">
              <motion.div 
                initial={{ opacity: 0, y: 10 }}
                animate={{ opacity: 1, y: 0 }}
                transition={{ delay: Math.min(i * 0.05, 0.3) }}
                className="card h-100 border-0"
                style={{ backgroundColor: 'var(--bg-secondary)', border: '1px solid var(--border-color)' }}
              >
                <div className="card-body p-4 d-flex flex-column">
                  <div className="d-flex justify-content-between align-items-start mb-3">
                    <span className="badge bg-dark border border-secondary text-uppercase" style={{ fontSize: '10px', letterSpacing: '0.5px' }}>
                      {algo.category}
                    </span>
                  </div>
                  
                  <h5 className="card-title fw-bold mb-2 text-primary">{algo.name}</h5>
                  <p className="card-text text-muted mb-4 flex-grow-1" style={{ fontSize: '14px' }}>{algo.description}</p>
                  
                  <div className="d-flex gap-4 mb-4 small border-top border-secondary pt-3">
                    <div>
                      <div className="text-muted mb-1 text-uppercase" style={{ fontSize: '10px', letterSpacing: '1px' }}>Time</div>
                      <div className="fw-semibold font-monospace" style={{ fontSize: '12px' }}>{algo.time}</div>
                    </div>
                    <div>
                      <div className="text-muted mb-1 text-uppercase" style={{ fontSize: '10px', letterSpacing: '1px' }}>Space</div>
                      <div className="fw-semibold font-monospace" style={{ fontSize: '12px' }}>{algo.space}</div>
                    </div>
                  </div>
                  
                  <div className="mt-auto d-flex gap-2">
                    <Link to={`/algorithms/${algo.id}`} className="btn btn-primary btn-sm flex-grow-1 d-flex justify-content-center align-items-center gap-2">
                      <Play size={14} /> Visualize
                    </Link>
                  </div>
                </div>
              </motion.div>
            </div>
          ))}
        </div>
      )}
    </div>
  )
}
