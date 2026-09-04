import { useState } from 'react'
import Editor from '@monaco-editor/react'
import { Play, Code2, Terminal as TerminalIcon, Eye, Sparkles } from 'lucide-react'
import axios from 'axios'
import ArrayVisualizer from '../components/ArrayVisualizer.tsx'
import GraphVisualizer from '../components/GraphVisualizer.tsx'

const LANGUAGES = {
  python: {
    name: 'Python',
    version: '3.10.0',
    defaultCode: 'print("Hello from AlgoForge!")\n',
    vizCode: `import json
# Example: Bubble Sort custom visualization
def bubble_sort(arr):
    steps = []
    step_count = 1
    
    # Init step
    steps.append({
        "step": step_count, "operation": "INIT",
        "description": "Initial array", "state": {"array": arr.copy()}
    })
    step_count += 1
    
    n = len(arr)
    for i in range(n):
        for j in range(0, n-i-1):
            if arr[j] > arr[j+1]:
                arr[j], arr[j+1] = arr[j+1], arr[j]
                steps.append({
                    "step": step_count, "operation": "SWAP",
                    "description": f"Swapped {arr[j+1]} and {arr[j]}",
                    "state": {"array": arr.copy(), "activeIndices": [j, j+1], "swapped": True}
                })
                step_count += 1
                
    steps.append({
        "step": step_count, "operation": "COMPLETE",
        "description": "Sorted", "state": {"array": arr.copy()}
    })
    
    # Output specific magic string
    print("__ALGOFORGE_VISUALIZE__ =" + json.dumps({"type": "array", "steps": steps}))

bubble_sort([5, 2, 8, 1, 9])
`,
  },
  java: {
    name: 'Java',
    version: '15.0.2',
    defaultCode: 'public class Main {\n    public static void main(String[] args) {\n        System.out.println("Hello from AlgoForge!");\n    }\n}\n',
    vizCode: `import java.util.*;\nimport com.google.gson.Gson;\n// Add GSON logic or manually construct JSON to output __ALGOFORGE_VISUALIZE__ = { ... }\npublic class Main {\n    public static void main(String[] args) {\n        System.out.println("Hello! Print the JSON step array to visualize.");\n    }\n}\n`,
  },
  javascript: {
    name: 'JavaScript',
    version: '18.15.0',
    defaultCode: 'console.log("Hello from AlgoForge!");\n',
    vizCode: `// Example JS Visualization
const steps = [{ step: 1, operation: 'INIT', description: 'Start', state: { array: [1, 2, 3] } }];
console.log('__ALGOFORGE_VISUALIZE__ =' + JSON.stringify({ type: 'array', steps }));
`,
  },
  cpp: {
    name: 'C++',
    version: '10.2.0',
    defaultCode: '#include <iostream>\n\nint main() {\n    std::cout << "Hello from AlgoForge!" << std::endl;\n    return 0;\n}\n',
    vizCode: `#include <iostream>\nint main() {\n    // Print JSON payload matching __ALGOFORGE_VISUALIZE__ =\n    std::cout << "__ALGOFORGE_VISUALIZE__ ={\\"type\\":\\"array\\", \\"steps\\":[{\\"step\\":1,\\"operation\\":\\"INIT\\",\\"description\\":\\"Start\\",\\"state\\":{\\"array\\":[1,2,3]}}]}" << std::endl;\n    return 0;\n}\n`,
  },
}

export default function Playground() {
  const [language, setLanguage] = useState<keyof typeof LANGUAGES>('python')
  const [code, setCode] = useState(LANGUAGES['python'].defaultCode)
  const [output, setOutput] = useState<string>('Ready. Click Run Code to compile & execute.')
  const [isRunning, setIsRunning] = useState(false)
  
  const [visualData, setVisualData] = useState<{type: string, steps: any[]}|null>(null)
  const [activeStep, setActiveStep] = useState(0)
  
  const [aiAnalysis, setAiAnalysis] = useState<string | null>(null)
  const [isAnalyzing, setIsAnalyzing] = useState(false)

  const handleLanguageChange = (e: React.ChangeEvent<HTMLSelectElement>) => {
    const newLang = e.target.value as keyof typeof LANGUAGES
    setLanguage(newLang)
    setCode(LANGUAGES[newLang].defaultCode)
  }

  const runCode = async () => {
    if (!code.trim()) return
    setIsRunning(true)
    setOutput('Compiling and executing on remote server...')
    setVisualData(null)

    try {
      const res = await axios.post('/api/compiler/execute', {
        language,
        version: LANGUAGES[language].version,
        code
      })

      if (res.data.success) {
        const result = res.data.data
        if (result.compile && result.compile.code !== 0) {
          setOutput(`--- COMPILATION ERROR ---\n${result.compile.output}`)
        } else if (result.run) {
          const runOutput = result.run.output || result.run.stderr;
          
          // Check for visualization payload
          const magicString = '__ALGOFORGE_VISUALIZE__ ='
          if (runOutput.includes(magicString)) {
             try {
                const parts = runOutput.split(magicString)
                const jsonStr = parts[1].trim()
                const data = JSON.parse(jsonStr)
                setVisualData(data)
                setActiveStep(0)
                setOutput(`${parts[0]}\n--- Visualization Loaded! Check the Visualizer pane ---`)
             } catch (e) {
                setOutput(`Error parsing visualization JSON:\n${e}\n\nRaw Output:\n${runOutput}`)
             }
          } else {
             setOutput(result.run.code !== 0 ? `--- RUNTIME ERROR (Exit Code ${result.run.code}) ---\n${runOutput}` : runOutput)
          }
        } else {
          setOutput(JSON.stringify(result, null, 2))
        }
      } else {
        setOutput(`Error: ${res.data.message}`)
      }
    } catch (err: any) {
      setOutput(`Error connecting to compiler service: ${err.message}`)
    } finally {
      setIsRunning(false)
    }
  }

  const handleAnalyze = async () => {
    if (!code.trim()) return
    setIsAnalyzing(true)
    setAiAnalysis("Analyzing code complexity with Gemini AI...")
    
    try {
      const res = await axios.post('/api/compiler/analyze', { code })
      if (res.data.success) {
        setAiAnalysis(res.data.data)
      } else {
        setAiAnalysis(`Error: ${res.data.message}`)
      }
    } catch (err: any) {
      setAiAnalysis(`Error connecting to AI service: ${err.message}`)
    } finally {
      setIsAnalyzing(false)
    }
  }

  return (
    <div className="container-fluid py-3 flex-grow-1 d-flex flex-column w-100">
      <div className="d-flex justify-content-between align-items-center mb-4">
        <div>
          <h2 className="fw-bold mb-1 d-flex align-items-center gap-2">
            <Code2 size={24} className="text-primary" /> Code Playground
          </h2>
          <p className="text-muted mb-0">Write, compile, and execute code in multiple languages directly in the browser.</p>
        </div>

        <div className="d-flex align-items-center gap-3">
          <select 
            className="form-select bg-dark text-light border-secondary shadow-sm"
            value={language}
            onChange={handleLanguageChange}
            style={{ width: '150px' }}
          >
            {Object.entries(LANGUAGES).map(([key, lang]) => (
              <option key={key} value={key}>{lang.name}</option>
            ))}
          </select>
          <button 
            className="btn btn-outline-info d-flex align-items-center gap-2 px-3 shadow-sm"
            onClick={handleAnalyze}
            disabled={isAnalyzing || isRunning}
          >
            {isAnalyzing ? (
              <span className="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span>
            ) : (
              <Sparkles size={16} />
            )}
            Analyze Complexity
          </button>
          
          <button 
            className="btn btn-primary d-flex align-items-center gap-2 px-4 shadow"
            onClick={runCode}
            disabled={isRunning || isAnalyzing}
          >
            {isRunning ? (
              <span className="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span>
            ) : (
              <Play size={18} />
            )}
            Run Code
          </button>
        </div>
      </div>

      <div className="row flex-grow-1 g-4" style={{ minHeight: 'calc(100vh - 180px)' }}>
        <div className={`d-flex flex-column ${visualData ? 'col-lg-6' : 'col-lg-8'}`}>
          <div className="bg-dark rounded border border-secondary flex-grow-1 position-relative overflow-hidden p-1 shadow-lg" style={{ minHeight: '500px', backgroundColor: 'var(--bg-secondary) !important' }}>
            <Editor
              height="100%"
              defaultLanguage={language}
              language={language === 'cpp' ? 'cpp' : language}
              theme="vs-dark"
              value={code}
              onChange={(val) => setCode(val || '')}
              options={{
                minimap: { enabled: false },
                fontSize: 14,
                fontFamily: "'JetBrains Mono', 'Fira Code', monospace",
                scrollBeyondLastLine: false,
                smoothScrolling: true,
                padding: { top: 16 }
              }}
            />
          </div>
        </div>
        
        <div className={`d-flex flex-column h-100 ${visualData ? 'col-lg-6' : 'col-lg-4'}`}>
          {visualData && (
            <div className="bg-dark rounded border border-secondary flex-grow-1 p-4 d-flex flex-column mb-3" style={{ backgroundColor: 'var(--bg-secondary) !important' }}>
              <div className="d-flex align-items-center justify-content-between border-bottom border-secondary pb-2 mb-2">
                <div className="d-flex align-items-center gap-2 text-primary">
                  <Eye size={18} />
                  <h6 className="mb-0 fw-semibold tracking-wider text-uppercase" style={{ fontSize: '13px' }}>
                    Algorithm Visualizer
                  </h6>
                </div>
                <div className="text-muted small">
                  Step {activeStep + 1} of {visualData.steps.length}
                </div>
              </div>
              <div className="flex-grow-1 position-relative overflow-hidden d-flex justify-content-center align-items-center bg-dark rounded">
                {visualData.type === 'array' ? (
                  <ArrayVisualizer state={visualData.steps[activeStep].state} slug="bubble-sort" />
                ) : (
                  <GraphVisualizer state={visualData.steps[activeStep].state} />
                )}
              </div>
              
              <div className="mt-3 p-3 bg-dark rounded border border-secondary text-light small">
                <strong>{visualData.steps[activeStep].operation}:</strong> {visualData.steps[activeStep].description}
              </div>

              <div className="d-flex justify-content-center gap-2 mt-3">
                <button className="btn btn-outline-secondary btn-sm" onClick={() => setActiveStep(0)} disabled={activeStep === 0}>First</button>
                <button className="btn btn-outline-secondary btn-sm" onClick={() => setActiveStep(prev => Math.max(0, prev - 1))} disabled={activeStep === 0}>Previous</button>
                <button className="btn btn-primary btn-sm px-4" onClick={() => setActiveStep(prev => Math.min(visualData.steps.length - 1, prev + 1))} disabled={activeStep === visualData.steps.length - 1}>Next</button>
                <button className="btn btn-outline-secondary btn-sm" onClick={() => setActiveStep(visualData.steps.length - 1)} disabled={activeStep === visualData.steps.length - 1}>Last</button>
              </div>
            </div>
          )}

          {aiAnalysis && (
            <div className="bg-dark rounded border border-info p-3 d-flex flex-column shadow-lg mb-3" style={{ backgroundColor: 'var(--bg-secondary) !important' }}>
              <div className="d-flex align-items-center justify-content-between border-bottom border-info pb-2 mb-2">
                <div className="d-flex align-items-center gap-2 text-info">
                  <Sparkles size={18} />
                  <h6 className="mb-0 fw-semibold tracking-wider text-uppercase" style={{ fontSize: '13px' }}>AI Complexity Analysis</h6>
                </div>
                <button className="btn btn-sm btn-outline-secondary py-0 px-2" onClick={() => setAiAnalysis(null)} style={{ fontSize: '12px' }}>Close</button>
              </div>
              <div className="flex-grow-1 position-relative overflow-auto">
                <pre className="text-light m-0" style={{ fontSize: '14px', whiteSpace: 'pre-wrap', fontFamily: 'inherit' }}>
                  {aiAnalysis}
                </pre>
              </div>
            </div>
          )}

          <div className={`${visualData ? 'h-25' : 'flex-grow-1'} bg-dark rounded border border-secondary p-3 d-flex flex-column shadow-lg`} style={{ backgroundColor: 'var(--bg-secondary) !important' }}>
            <div className="d-flex align-items-center gap-2 border-bottom border-secondary pb-2 mb-2 text-muted">
              <TerminalIcon size={18} />
              <h6 className="mb-0 fw-semibold tracking-wider text-uppercase" style={{ fontSize: '13px' }}>Terminal Output</h6>
            </div>
            
            <div className="flex-grow-1 position-relative overflow-auto">
              <pre className="text-light font-monospace m-0" style={{ fontSize: '13px', whiteSpace: 'pre-wrap' }}>
                {output}
              </pre>
            </div>
          </div>
        </div>
      </div>
    </div>
  )
}
