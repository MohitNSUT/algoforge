import { motion, AnimatePresence } from 'framer-motion'

interface ArrayVisualizerProps {
  state: any;
  slug: string;
}

export default function ArrayVisualizer({ state, slug }: ArrayVisualizerProps) {
  const array: number[] = state.array || []
  const maxVal = Math.max(...array, 1)

  // Derive colors based on state and algorithm
  const getColor = (index: number) => {
    if (slug === 'binary-search') {
      if (index === state.foundIndex) return 'var(--success)'
      if (index === state.mid) return 'var(--warning)'
      if (state.left !== undefined && state.right !== undefined) {
        if (index >= state.left && index <= state.right) return 'var(--accent-primary)'
      }
      return 'var(--text-secondary)'
    }

    if (slug === 'merge-sort') {
      if (state.writtenIndex === index) return 'var(--success)'
      if (state.comparingIndices?.includes(index)) return 'var(--warning)'
      if (state.activeRange && index >= state.activeRange[0] && index <= state.activeRange[1]) return 'var(--accent-primary)'
      return 'var(--text-secondary)'
    }

    if (slug === 'quick-sort') {
      if (state.pivotIndex === index) return 'var(--success)' // Pivot placed
      if (state.comparingIndices?.includes(index)) return 'var(--warning)'
      if (state.activeIndices?.includes(index)) return 'var(--danger)' // Swapping
      if (state.activeRange && index >= state.activeRange[0] && index <= state.activeRange[1]) return 'var(--accent-primary)'
      return 'var(--text-secondary)'
    }

    if (slug === 'insertion-sort') {
      if (state.insertedIndex === index) return 'var(--success)'
      if (state.keyIndex === index) return 'var(--danger)'
      if (state.comparingIndices?.includes(index) || state.activeIndices?.includes(index)) return 'var(--warning)'
      return 'var(--accent-primary)'
    }

    if (slug === 'selection-sort') {
      if (state.minIdx === index) return 'var(--success)' // Current minimum
      if (state.comparingIndices?.includes(index)) return 'var(--warning)'
      if (state.activeIndices?.includes(index)) return 'var(--danger)' // Swapping
      if (state.sortedBoundary !== undefined && index < state.sortedBoundary) return 'var(--text-secondary)' // Already sorted
      return 'var(--accent-primary)'
    }

    if (slug === 'bubble-sort') {
      if (state.activeIndices?.includes(index)) {
        return state.swapped ? 'var(--danger)' : 'var(--warning)'
      }
      return 'var(--accent-primary)'
    }

    return 'var(--accent-primary)'
  }

  return (
    <div className="w-100 h-100 d-flex flex-column justify-content-end align-items-center">
      <div className="d-flex align-items-end justify-content-center gap-1 w-100" style={{ height: '300px' }}>
        <AnimatePresence>
          {array.map((value, index) => (
            <motion.div
              key={`${index}-${value}`} // Keys to allow layout animation if positions change
              layout
              initial={{ height: 0, opacity: 0 }}
              animate={{ 
                height: `${(value / maxVal) * 100}%`,
                opacity: 1,
                backgroundColor: getColor(index)
              }}
              transition={{
                type: 'spring',
                stiffness: 300,
                damping: 20
              }}
              className="rounded-top position-relative d-flex justify-content-center"
              style={{ width: '40px', minWidth: '20px' }}
            >
              <div 
                className="position-absolute" 
                style={{ bottom: '-30px', fontSize: '12px', fontWeight: 'bold', color: 'var(--text-secondary)' }}
              >
                {value}
              </div>
            </motion.div>
          ))}
        </AnimatePresence>
      </div>
      <div style={{ height: '40px' }} /> {/* Spacer for labels */}
    </div>
  )
}
