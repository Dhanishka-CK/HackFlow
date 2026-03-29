import { useState, useEffect } from 'react';

function App() {
  const [tasks, setTasks] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showForm, setShowForm] = useState(false);
  const [selectedTask, setSelectedTask] = useState(null);
  const [message, setMessage] = useState({ text: '', type: '' });

  const [formData, setFormData] = useState({
    title: '',
    description: '',
    status: 'Backlog',
    priority: 'Medium',
    difficulty: 'Medium',
    assigneeId: '',
    assigneeName: '',
    createdBy: 'admin'
  });

  const columns = [
    { id: 'Backlog', label: '📋 Backlog', color: 'from-gray-500 to-gray-600' },
    { id: 'Todo', label: '📝 Todo', color: 'from-yellow-500 to-orange-500' },
    { id: 'In Progress', label: '⚡ In Progress', color: 'from-blue-500 to-cyan-500' },
    { id: 'Done', label: '✅ Done', color: 'from-green-500 to-emerald-500' }
  ];

  const fetchTasks = async () => {
    try {
      const response = await fetch('http://localhost:8080/api/tasks');
      const data = await response.json();
      setTasks(data);
      setLoading(false);
    } catch (error) {
      console.error('Error fetching tasks:', error);
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchTasks();
    const interval = setInterval(fetchTasks, 5000);
    return () => clearInterval(interval);
  }, []);

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setMessage({ text: '', type: '' });

    try {
      const response = await fetch('http://localhost:8080/api/tasks', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(formData),
      });

      if (response.ok) {
        setMessage({ text: '✨ Task created!', type: 'success' });
        setFormData({
          title: '', description: '', status: 'Backlog',
          priority: 'Medium', difficulty: 'Medium',
          assigneeId: '', assigneeName: '', createdBy: 'admin'
        });
        setShowForm(false);
        fetchTasks();
      } else {
        setMessage({ text: '❌ Failed to create task', type: 'error' });
      }
    } catch (error) {
      setMessage({ text: '❌ Error creating task', type: 'error' });
    }
  };

  const updateTaskStatus = async (taskId, newStatus) => {
    try {
      const task = tasks.find(t => t.id === taskId);
      await fetch(`http://localhost:8080/api/tasks/${taskId}`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ ...task, status: newStatus }),
      });
      setMessage({ text: `🔄 Moved to ${newStatus}`, type: 'success' });
      fetchTasks();
    } catch (error) {
      console.error('Error updating task:', error);
    }
  };

  const deleteTask = async (taskId) => {
    if (!confirm('Delete this task?')) return;
    try {
      await fetch(`http://localhost:8080/api/tasks/${taskId}`, { method: 'DELETE' });
      setMessage({ text: '🗑️ Task deleted', type: 'success' });
      fetchTasks();
    } catch (error) {
      console.error('Error deleting task:', error);
    }
  };

  const getPriorityBadge = (priority) => {
    const styles = {
      High: 'bg-red-500/20 text-red-300 border-red-500/30',
      Medium: 'bg-yellow-500/20 text-yellow-300 border-yellow-500/30',
      Low: 'bg-green-500/20 text-green-300 border-green-500/30'
    };
    return styles[priority] || styles.Medium;
  };

  const getDifficultyBadge = (difficulty) => {
    const styles = {
      Hard: 'bg-purple-500/20 text-purple-300 border-purple-500/30',
      Medium: 'bg-blue-500/20 text-blue-300 border-blue-500/30',
      Easy: 'bg-teal-500/20 text-teal-300 border-teal-500/30'
    };
    return styles[difficulty] || styles.Medium;
  };

  const getTasksByStatus = (status) => tasks.filter(t => t.status === status);

  return (
    <div className="min-h-screen bg-gradient-to-br from-slate-900 via-purple-900 to-slate-900">
      {/* Animated background */}
      <div className="fixed inset-0 overflow-hidden pointer-events-none">
        <div className="absolute -top-40 -right-40 w-80 h-80 bg-purple-500/20 rounded-full blur-3xl animate-pulse"></div>
        <div className="absolute -bottom-40 -left-40 w-80 h-80 bg-pink-500/20 rounded-full blur-3xl animate-pulse delay-1000"></div>
      </div>

      {/* Header */}
      <header className="relative bg-white/10 backdrop-blur-xl border-b border-white/20">
        <div className="max-w-7xl mx-auto px-4 py-4">
          <div className="flex justify-between items-center">
            <div>
              <h1 className="text-3xl font-bold text-white flex items-center gap-2">
                <span className="text-4xl">🚀</span> HackFlow
              </h1>
              <p className="text-purple-200/80 text-sm mt-1">
                AI-Powered Project Management
              </p>
            </div>
            <button
              onClick={() => setShowForm(!showForm)}
              className="bg-gradient-to-r from-purple-600 to-pink-600 hover:from-purple-700 hover:to-pink-700 text-white px-6 py-3 rounded-xl font-semibold transition-all transform hover:scale-105 shadow-lg shadow-purple-500/25"
            >
              {showForm ? '✕ Cancel' : '✨ New Task'}
            </button>
          </div>
        </div>
      </header>

      {/* Message Alert */}
      {message.text && (
        <div className={`max-w-7xl mx-auto px-4 mt-4 p-4 rounded-xl backdrop-blur-lg border ${
          message.type === 'success' 
            ? 'bg-green-500/20 border-green-500/50 text-green-200' 
            : 'bg-red-500/20 border-red-500/50 text-red-200'
        } animate-fade-in`}>
          {message.text}
        </div>
      )}

      {/* Task Creation Form */}
      {showForm && (
        <div className="max-w-7xl mx-auto px-4 mt-6 animate-slide-up">
          <div className="bg-white/10 backdrop-blur-xl rounded-2xl p-6 border border-white/20">
            <h2 className="text-xl font-semibold text-white mb-4 flex items-center gap-2">
              <span>✨</span> Create New Task
            </h2>
            <form onSubmit={handleSubmit} className="space-y-4">
              <div>
                <label className="block text-sm font-medium text-purple-200 mb-1">Title *</label>
                <input
                  type="text" name="title" value={formData.title} onChange={handleChange} required
                  className="w-full px-4 py-3 bg-white/10 border border-white/20 rounded-xl text-white placeholder-purple-300/50 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent transition"
                  placeholder="Enter task title"
                />
              </div>
              <div>
                <label className="block text-sm font-medium text-purple-200 mb-1">Description</label>
                <textarea
                  name="description" value={formData.description} onChange={handleChange} rows="3"
                  className="w-full px-4 py-3 bg-white/10 border border-white/20 rounded-xl text-white placeholder-purple-300/50 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent transition"
                  placeholder="Enter task description"
                />
              </div>
              <div className="grid grid-cols-3 gap-4">
                <div>
                  <label className="block text-sm font-medium text-purple-200 mb-1">Status</label>
                  <select name="status" value={formData.status} onChange={handleChange}
                    className="w-full px-4 py-3 bg-white/10 border border-white/20 rounded-xl text-white focus:outline-none focus:ring-2 focus:ring-purple-500">
                    {columns.map(col => <option key={col.id} value={col.id}>{col.label}</option>)}
                  </select>
                </div>
                <div>
                  <label className="block text-sm font-medium text-purple-200 mb-1">Priority</label>
                  <select name="priority" value={formData.priority} onChange={handleChange}
                    className="w-full px-4 py-3 bg-white/10 border border-white/20 rounded-xl text-white focus:outline-none focus:ring-2 focus:ring-purple-500">
                    <option value="Low">🟢 Low</option>
                    <option value="Medium">🟡 Medium</option>
                    <option value="High">🔴 High</option>
                  </select>
                </div>
                <div>
                  <label className="block text-sm font-medium text-purple-200 mb-1">Difficulty</label>
                  <select name="difficulty" value={formData.difficulty} onChange={handleChange}
                    className="w-full px-4 py-3 bg-white/10 border border-white/20 rounded-xl text-white focus:outline-none focus:ring-2 focus:ring-purple-500">
                    <option value="Easy">🟢 Easy</option>
                    <option value="Medium">🔵 Medium</option>
                    <option value="Hard">🟣 Hard</option>
                  </select>
                </div>
              </div>
              <div className="grid grid-cols-2 gap-4">
                <div>
                  <label className="block text-sm font-medium text-purple-200 mb-1">Assignee Name</label>
                  <input type="text" name="assigneeName" value={formData.assigneeName} onChange={handleChange}
                    className="w-full px-4 py-3 bg-white/10 border border-white/20 rounded-xl text-white placeholder-purple-300/50 focus:outline-none focus:ring-2 focus:ring-purple-500"
                    placeholder="e.g., John Doe" />
                </div>
                <div>
                  <label className="block text-sm font-medium text-purple-200 mb-1">Assignee ID</label>
                  <input type="text" name="assigneeId" value={formData.assigneeId} onChange={handleChange}
                    className="w-full px-4 py-3 bg-white/10 border border-white/20 rounded-xl text-white placeholder-purple-300/50 focus:outline-none focus:ring-2 focus:ring-purple-500"
                    placeholder="e.g., user123" />
                </div>
              </div>
              <button type="submit"
                className="w-full bg-gradient-to-r from-green-600 to-emerald-600 hover:from-green-700 hover:to-emerald-700 text-white px-6 py-3 rounded-xl font-semibold transition-all transform hover:scale-105 shadow-lg shadow-green-500/25">
                ✨ Create Task
              </button>
            </form>
          </div>
        </div>
      )}

      {/* Kanban Board */}
      <main className="max-w-7xl mx-auto px-4 py-6">
        {loading ? (
          <div className="flex items-center justify-center h-64">
            <div className="text-white/80 text-xl flex items-center gap-3">
              <div className="w-6 h-6 border-2 border-purple-500 border-t-transparent rounded-full animate-spin"></div>
              Loading tasks...
            </div>
          </div>
        ) : (
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
            {columns.map((column) => (
              <div key={column.id} className="bg-white/10 backdrop-blur-xl rounded-2xl p-4 border border-white/20">
                {/* Column Header */}
                <div className="flex items-center justify-between mb-4">
                  <div className="flex items-center gap-2">
                    <div className={`w-3 h-3 rounded-full bg-gradient-to-r ${column.color}`}></div>
                    <h3 className="text-lg font-semibold text-white">{column.label}</h3>
                  </div>
                  <span className="bg-white/20 text-white px-3 py-1 rounded-full text-sm font-medium">
                    {getTasksByStatus(column.id).length}
                  </span>
                </div>

                {/* Tasks */}
                <div className="space-y-3 min-h-[200px]">
                  {getTasksByStatus(column.id).length === 0 ? (
                    <div className="text-purple-300/40 text-center py-8 text-sm border-2 border-dashed border-white/10 rounded-xl">
                      Drop tasks here
                    </div>
                  ) : (
                    getTasksByStatus(column.id).map((task) => (
                      <div
                        key={task.id}
                        className="group bg-white/10 rounded-xl p-4 border border-white/20 hover:bg-white/20 hover:border-purple-500/50 transition-all cursor-pointer"
                        onClick={() => setSelectedTask(task)}
                      >
                        <div className="flex items-start justify-between mb-2">
                          <h4 className="font-semibold text-white text-sm line-clamp-2">{task.title}</h4>
                          <button
                            onClick={(e) => { e.stopPropagation(); deleteTask(task.id); }}
                            className="opacity-0 group-hover:opacity-100 text-red-400 hover:text-red-300 transition"
                          >
                            🗑️
                          </button>
                        </div>
                        
                        <p className="text-purple-200/80 text-xs mb-3 line-clamp-2">{task.description}</p>

                        <div className="flex flex-wrap gap-2 mb-3">
                          <span className={`px-2 py-1 rounded-lg text-xs border ${getPriorityBadge(task.priority)}`}>
                            {task.priority}
                          </span>
                          <span className={`px-2 py-1 rounded-lg text-xs border ${getDifficultyBadge(task.difficulty)}`}>
                            {task.difficulty}
                          </span>
                        </div>

                        {task.assigneeName && (
                          <div className="flex items-center gap-2 text-purple-200/80 text-xs">
                            <div className="w-5 h-5 rounded-full bg-gradient-to-r from-purple-500 to-pink-500 flex items-center justify-center text-xs">
                              {task.assigneeName.charAt(0)}
                            </div>
                            <span>{task.assigneeName}</span>
                          </div>
                        )}

                        {/* Quick Move */}
                        <div className="mt-3 pt-3 border-t border-white/10">
                          <select
                            value={task.status}
                            onChange={(e) => updateTaskStatus(task.id, e.target.value)}
                            className="w-full bg-white/10 border border-white/20 rounded-lg text-white text-xs px-2 py-1 focus:outline-none focus:ring-2 focus:ring-purple-500"
                            onClick={(e) => e.stopPropagation()}
                          >
                            {columns.map(col => <option key={col.id} value={col.id}>{col.label}</option>)}
                          </select>
                        </div>
                      </div>
                    ))
                  )}
                </div>
              </div>
            ))}
          </div>
        )}
      </main>

      {/* Task Detail Modal */}
      {selectedTask && (
        <div className="fixed inset-0 bg-black/60 backdrop-blur-sm flex items-center justify-center z-50 p-4 animate-fade-in">
          <div className="bg-slate-900/90 rounded-2xl p-6 max-w-lg w-full border border-white/20 max-h-[80vh] overflow-y-auto">
            <div className="flex justify-between items-start mb-4">
              <h3 className="text-xl font-bold text-white">{selectedTask.title}</h3>
              <button onClick={() => setSelectedTask(null)} className="text-purple-300 hover:text-white transition">✕</button>
            </div>
            <p className="text-purple-200/90 mb-4">{selectedTask.description}</p>
            <div className="grid grid-cols-2 gap-4 mb-4">
              <div><label className="text-purple-300/80 text-sm">Status</label><div className="text-white font-medium">{selectedTask.status}</div></div>
              <div><label className="text-purple-300/80 text-sm">Priority</label><div className="text-white font-medium">{selectedTask.priority}</div></div>
              <div><label className="text-purple-300/80 text-sm">Difficulty</label><div className="text-white font-medium">{selectedTask.difficulty}</div></div>
              <div><label className="text-purple-300/80 text-sm">Assignee</label><div className="text-white font-medium">{selectedTask.assigneeName || 'Unassigned'}</div></div>
            </div>
            <div className="flex gap-3">
              <button onClick={() => { deleteTask(selectedTask.id); setSelectedTask(null); }}
                className="flex-1 bg-red-600 hover:bg-red-700 text-white px-4 py-2 rounded-xl transition">🗑️ Delete</button>
              <button onClick={() => setSelectedTask(null)}
                className="flex-1 bg-white/10 hover:bg-white/20 text-white px-4 py-2 rounded-xl transition">Close</button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}

export default App;