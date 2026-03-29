
🚀 HackFlow
AI-Powered Project Management System with Discord Integration
<img width="82" height="20" alt="image" src="https://github.com/user-attachments/assets/eb22cb1f-e05a-42ee-82a0-b8eedcc323ff" />
<img width="58" height="20" alt="image" src="https://github.com/user-attachments/assets/63526a62-74bc-43eb-97da-3fb4873d8032" />
<img width="120" height="20" alt="image" src="https://github.com/user-attachments/assets/c409dbf4-6a9b-45b7-974d-42266292d286" />
<img width="64" height="20" alt="image" src="https://github.com/user-attachments/assets/c8464974-d1b3-45a0-8e2c-dd2ad49e82f1" />
<img width="80" height="20" alt="image" src="https://github.com/user-attachments/assets/26464321-4c97-446e-bc72-4b085028f230" />

✨ Overview
HackFlow is a full-stack, AI-powered project management system designed for hackathon teams and agile developers. It seamlessly integrates with Discord, allowing teams to create, manage, and track tasks using natural language commands while maintaining a beautiful Kanban-style web interface.
🔑 Key Value Proposition
🗣️ Natural Language Task Creation: Create tasks in Discord using everyday language — no complex syntax required
🤖 AI-Powered Intelligence: Google Gemini AI extracts task details, priority, difficulty, and assignees automatically
🔄 Two-Way Synchronization: Changes in Discord reflect in the web app instantly, and vice versa
🎨 Beautiful UI: Modern, responsive Kanban board built with React and Tailwind CSS
🔐 Secure by Design: Sensitive credentials managed via environment variables, never committed to source control
🎯 Features
🤖 Discord Bot Integration
✅ Create tasks using !task commands with natural language
✅ AI-powered extraction of priority, difficulty, assignee, and description
✅ View all tasks with !tasks command (grouped by status)
✅ Get help and command reference with !botinfo
✅ Real-time notifications when tasks are created/updated in the web app
🎨 Web Application
✅ Beautiful Kanban board with drag-and-drop support (Backlog → Todo → In Progress → Done)
✅ Real-time task updates with auto-refresh polling
✅ Responsive design that works on desktop, tablet, and mobile
✅ Task filtering by status, priority, and assignee
✅ Create, edit, and delete tasks with a user-friendly form
🧠 AI Intelligence
✅ Google Gemini AI analyzes Discord messages to extract structured task data
✅ Smart fallback parser when AI is unavailable (pattern-based extraction)
✅ Automatic priority/difficulty detection from keywords ("urgent" → High, "quick" → Easy)
✅ Assignee extraction from mentions ("for Sarah", "assign to John")
🔗 Two-Way Sync
✅ Discord → Web: Tasks created via Discord appear in Kanban board within seconds
✅ Web → Discord: Tasks created/updated in web app trigger Discord webhook notifications
✅ Status changes sync bidirectionally with visual feedback
🔐 Security & Best Practices
✅ Sensitive credentials stored in .env files (excluded via .gitignore)
✅ .env.example templates provided for easy setup
✅ MongoDB Atlas with SSL/TLS encryption
✅ Discord webhook notifications with proper error handling
🏗️ Architecture
┌─────────────────────────────────────────────────────────┐
│                    HackFlow System                       │
├─────────────────────────────────────────────────────────┤
│                                                         │
│  ┌─────────────┐     ┌─────────────┐     ┌───────────┐ │
│  │   Discord   │────▶│  Python     │────▶│   Java    │ │
│  │   Client    │     │  Discord    │     │  Backend  │ │
│  │   (User)    │     │  Bot        │     │(Spring Boot)│ │
│  └─────────────┘     └─────────────┘     └─────┬─────┘ │
│                                                │        │
│                                                ▼        │
│  ┌─────────────┐     ┌─────────────┐     ┌───────────┐ │
│  │   React     │◀────│   REST      │◀────│  MongoDB  │ │
│  │   Frontend  │     │   API       │     │   Atlas   │ │
│  │  (Kanban)   │     │  (JSON)     │     │(Database) │ │
│  └─────────────┘     └─────────────┘     └───────────┘ │
│                                                │        │
│                                                ▼        │
│                                   ┌───────────────────┐ │
│                                   │ Discord Webhook   │ │
│                                   │ (Notifications)   │ │
│                                   └───────────────────┘ │
│                                                         │
└─────────────────────────────────────────────────────────┘


🌐 Web App Usage
🎨 Kanban Board
View Tasks: Tasks are displayed in four columns:
📦 Backlog: Ideas and future work
📝 Todo: Ready to start
⚡ In Progress: Currently being worked on
✅ Done: Completed tasks
Create Task: Click the "+ New Task" button and fill the form:
Title (required)
Description
Status (default: Backlog)
Priority (Low/Medium/High)
Difficulty (Easy/Medium/Hard)
Assignee Name
Update Task: Click the dropdown at the bottom of any task card to change its status.
Delete Task: Click the 🗑️ icon to remove a task permanently.
🔔 Real-Time Updates
The frontend polls the backend every 5 seconds for task updates
Changes made in Discord appear in the web app automatically
Changes made in the web app trigger Discord webhook notifications

📁 Project Structure

HackFlow/
├── 📁 backend/                    # Java Spring Boot Backend
│   ├── 📄 pom.xml                 # Maven dependencies
│   ├── 📁 src/
│   │   ├── 📁 main/
│   │   │   ├── 📁 java/com/hackflow/backend/
│   │   │   │   ├── 📄 HackflowBackendApplication.java  # Entry point
│   │   │   │   ├── 📁 controller/   # REST API controllers
│   │   │   │   │   ├── 📄 TaskController.java
│   │   │   │   │   ├── 📄 UserController.java
│   │   │   │   │   └── 📄 HealthController.java
│   │   │   │   ├── 📁 model/        # Data models
│   │   │   │   │   ├── 📄 Task.java
│   │   │   │   │   └── 📄 User.java
│   │   │   │   ├── 📁 repository/   # MongoDB repositories
│   │   │   │   │   ├── 📄 TaskRepository.java
│   │   │   │   │   └── 📄 UserRepository.java
│   │   │   │   └── 📁 service/      # Business logic
│   │   │   │       ├── 📄 TaskService.java
│   │   │   │       ├── 📄 UserService.java
│   │   │   │       └── 📄 DiscordNotificationService.java
│   │   │   └── 📁 resources/
│   │   │       ├── 📄 application.properties  # Configuration
│   │   │       └── 📁 static/      # Static assets (if any)
│   │   └── 📁 test/                # Unit & integration tests
│   └── 📄 mvnw                     # Maven wrapper
│
├── 📁 frontend/                   # React + Vite Frontend
│   ├── 📄 package.json            # Node dependencies
│   ├── 📄 vite.config.js          # Vite configuration
│   ├── 📄 tailwind.config.js      # Tailwind CSS config
│   ├── 📄 postcss.config.js       # PostCSS config
│   ├── 📄 index.html              # Entry HTML
│   ├── 📁 public/                 # Static assets
│   └── 📁 src/
│       ├── 📄 main.jsx            # React entry point
│       ├── 📄 App.jsx             # Main component
│       ├── 📄 index.css           # Global styles
│       ├── 📁 components/         # Reusable UI components
│       │   ├── 📄 TaskCard.jsx
│       │   ├── 📄 KanbanColumn.jsx
│       │   └── 📄 TaskForm.jsx
│       ├── 📁 services/           # API service layer
│       │   └── 📄 api.js
│       └── 📁 assets/             # Images, icons, etc.
│
├── 📁 discord-bot/               # Python Discord Bot
│   ├── 📄 bot.py                 # Main bot logic
│   ├── 📄 requirements.txt       # Python dependencies
│   ├── 📄 .env.example           # Environment template
│   ├── 📄 .gitignore             # Exclude sensitive files
│   └── 📁 venv/                  # Virtual environment (NOT committed)
│
├── 📄 .gitignore                 # Root gitignore
├── 📄 README.md                  # This file
└── 📄 LICENSE                    # MIT License


Acknowledgments
🦎 MongoDB Atlas - For providing a free, cloud-hosted database with SSL support
💬 Discord Developer Platform - For the robust bot API and OAuth2 integration
🧠 Google Gemini - For powerful, accessible AI capabilities via the Generative Language API
🌱 Spring Boot - For simplifying Java backend development with convention over configuration
⚛️ React & Vite - For enabling fast, modern frontend development with hot module replacement
🎨 Tailwind CSS - For utility-first styling that accelerates UI development
🐍 discord.py - For the excellent Python wrapper around the Discord API
🤝 Open Source Community - For the countless libraries, tutorials, and inspiration
