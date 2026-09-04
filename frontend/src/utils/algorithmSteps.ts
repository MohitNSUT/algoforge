export const ALGORITHM_STEPS: Record<string, string[]> = {
  'bubble-sort': [
    "1. Loop through the array from i = 0 to n-1.",
    "2. Inner loop from j = 0 to n-i-1.",
    "3. If array[j] > array[j+1], swap them.",
    "4. After each inner loop, the largest unsorted element bubbles to the end.",
    "5. Repeat until the array is fully sorted."
  ],
  'merge-sort': [
    "1. Divide the array into two halves until each subarray has 1 element.",
    "2. Merge two sorted subarrays by comparing their smallest elements.",
    "3. Copy the smaller element to a temporary array.",
    "4. Repeat step 2-3 until both subarrays are exhausted.",
    "5. Copy the merged temporary array back to the original array."
  ],
  'quick-sort': [
    "1. Pick a pivot element from the array.",
    "2. Partition the array so that elements smaller than the pivot are on the left, and larger are on the right.",
    "3. Recursively apply Quick Sort to the left and right subarrays.",
    "4. Combine the sorted subarrays and the pivot.",
    "5. Base case: an array of size 0 or 1 is already sorted."
  ],
  'insertion-sort': [
    "1. Iterate from the second element (i = 1) to the end of the array.",
    "2. Store the current element as 'key'.",
    "3. Compare 'key' with elements before it (j = i-1).",
    "4. Shift elements greater than 'key' one position to the right.",
    "5. Insert 'key' into its correct sorted position."
  ],
  'selection-sort': [
    "1. Loop from i = 0 to n-1.",
    "2. Assume the element at index i is the minimum.",
    "3. Iterate through the rest of the array (j = i+1 to n) to find the actual minimum.",
    "4. If a smaller element is found, update the minimum index.",
    "5. Swap the minimum element with the element at index i."
  ],
  'binary-search': [
    "1. Initialize pointers: left = 0, right = n-1.",
    "2. Loop while left <= right.",
    "3. Calculate the middle index: mid = left + (right - left) / 2.",
    "4. If array[mid] == target, return mid.",
    "5. If array[mid] < target, search the right half (left = mid + 1).",
    "6. If array[mid] > target, search the left half (right = mid - 1).",
    "7. If loop ends without finding target, return -1."
  ],
  'dijkstra': [
    "1. Initialize distances to all nodes as Infinity, and distance to start node as 0.",
    "2. Insert the start node into a priority queue (min-heap).",
    "3. While the queue is not empty, extract the node 'u' with the minimum distance.",
    "4. Mark 'u' as visited.",
    "5. For each unvisited neighbor 'v' of 'u', calculate new distance: dist[u] + weight(u, v).",
    "6. If new distance < dist[v], update dist[v] and insert 'v' into the queue."
  ],
  'prim': [
    "1. Initialize a min-heap, a boolean array for visited nodes, and a list for the MST.",
    "2. Start with an arbitrary node, mark it as visited.",
    "3. Add all edges from the start node to the min-heap.",
    "4. While the heap is not empty, extract the edge with the minimum weight.",
    "5. If the destination node is unvisited, add the edge to the MST.",
    "6. Mark the destination node as visited and add its edges to the heap.",
    "7. Repeat until all nodes are visited."
  ],
  'bfs': [
    "1. Initialize a queue and a visited set (or boolean array).",
    "2. Enqueue the start node and mark it as visited.",
    "3. While the queue is not empty, dequeue a node 'u'.",
    "4. Process node 'u'.",
    "5. For each unvisited neighbor 'v' of 'u', mark 'v' as visited and enqueue it.",
    "6. Repeat until the queue is empty."
  ],
  'dfs': [
    "1. Initialize a stack (or use recursion) and a visited set.",
    "2. Push the start node onto the stack.",
    "3. While the stack is not empty, pop a node 'u'.",
    "4. If 'u' is not visited, mark it as visited and process it.",
    "5. Push all unvisited neighbors of 'u' onto the stack.",
    "6. Repeat until the stack is empty."
  ],
  'kahn': [
    "1. Compute in-degree (number of incoming edges) for each node.",
    "2. Initialize a queue and enqueue all nodes with in-degree 0.",
    "3. While the queue is not empty, dequeue a node 'u' and add it to the topological order.",
    "4. For each neighbor 'v' of 'u', decrement its in-degree by 1.",
    "5. If in-degree of 'v' becomes 0, enqueue 'v'.",
    "6. If the topological order contains all nodes, it's a DAG; otherwise, there's a cycle."
  ],
  'dfs-topo': [
    "1. Initialize a visited set and an empty stack.",
    "2. For each unvisited node in the graph, call a recursive DFS helper.",
    "3. In the DFS helper, mark the current node as visited.",
    "4. Recursively call DFS for all unvisited neighbors of the current node.",
    "5. After exploring all neighbors, push the current node onto the stack.",
    "6. The topological order is formed by popping elements from the stack."
  ],
  'bellman-ford': [
    "1. Initialize distances from start node to all other nodes as Infinity, start node as 0.",
    "2. Repeat |V| - 1 times (where |V| is the number of vertices):",
    "3. For every edge (u, v) with weight w, if dist[u] + w < dist[v], update dist[v] = dist[u] + w.",
    "4. Run one more iteration to check for negative weight cycles.",
    "5. If any dist[v] can still be updated, a negative cycle exists."
  ],
  'kruskal': [
    "1. Sort all edges in the graph in non-decreasing order of their weight.",
    "2. Initialize a Disjoint Set (Union-Find) for all nodes.",
    "3. Iterate through the sorted edges.",
    "4. For each edge (u, v), check if 'u' and 'v' belong to the same set using Find.",
    "5. If they are in different sets, add the edge to the MST and Union the sets.",
    "6. Stop when the MST has |V| - 1 edges."
  ],
  'floyd-warshall': [
    "1. Initialize a 2D distance matrix with graph edge weights. Set dist[i][i] = 0.",
    "2. Set dist[i][j] = Infinity for missing edges.",
    "3. Use three nested loops: k (intermediate), i (source), j (destination).",
    "4. If dist[i][k] + dist[k][j] < dist[i][j], update dist[i][j].",
    "5. The matrix will eventually contain shortest paths between all pairs of nodes.",
    "6. A negative cycle exists if dist[i][i] < 0 for any i."
  ],
  'a-star': [
    "1. Initialize a priority queue (min-heap) prioritizing f-score = g-score + h-score (heuristic).",
    "2. g-score is the exact cost from start. h-score is the estimated cost to target.",
    "3. Enqueue the start node with f-score = h(start).",
    "4. While queue is not empty, extract node 'u' with lowest f-score.",
    "5. If 'u' is the target, reconstruct and return the path.",
    "6. For each neighbor 'v', calculate tentative g-score. If better than known, update 'v' and enqueue."
  ],
  'union-find': [
    "1. Initialize two arrays: 'parent' (where parent[i]=i) and 'rank' (or size).",
    "2. FIND(x): traverse 'parent' array to find the root of x.",
    "3. Apply path compression during FIND: make nodes point directly to the root.",
    "4. UNION(x, y): Find roots of x and y.",
    "5. If roots are different, attach the tree with smaller rank to the root of the larger tree (Union by Rank).",
    "6. This ensures near constant time complexity for operations."
  ]
}
