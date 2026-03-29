import discord
from discord.ext import commands
import aiohttp
import os
import json
import re  
import google.generativeai as genai
from dotenv import load_dotenv
from datetime import datetime

# Load environment variables
load_dotenv()

# Configure AI (ignore deprecation warning for now)
genai.configure(api_key=os.getenv('GEMINI_API_KEY'))
model = genai.GenerativeModel('gemini-1.5-flash')

# Discord Bot Setup
intents = discord.Intents.default()
intents.message_content = True
intents.members = True

bot = commands.Bot(command_prefix='!', intents=intents, help_command=None)  # Disable default help
BACKEND_URL = os.getenv('BACKEND_URL', 'http://localhost:8080')

# Task status mapping
STATUS_MAP = {
    'backlog': 'Backlog',
    'todo': 'Todo', 
    'in progress': 'In Progress',
    'done': 'Done'
}

async def create_task_in_backend(title, description, status='Todo', priority='Medium', 
                                  difficulty='Medium', assignee_name=None):
    """Create a task in the Java backend"""
    try:
        async with aiohttp.ClientSession() as session:
            task_data = {
                "title": title,
                "description": description,
                "status": status,
                "priority": priority,
                "difficulty": difficulty,
                "assigneeName": assignee_name or "Unassigned",
                "createdBy": "discord-bot"
            }
            
            async with session.post(f'{BACKEND_URL}/api/tasks', json=task_data) as response:
                if response.status == 200:
                    return await response.json()
                else:
                    return None
    except Exception as e:
        print(f"Error creating task: {e}")
        return None

async def get_all_tasks():
    """Get all tasks from backend"""
    try:
        async with aiohttp.ClientSession() as session:
            async with session.get(f'{BACKEND_URL}/api/tasks') as response:
                if response.status == 200:
                    return await response.json()
                return []
    except Exception as e:
        print(f"Error fetching tasks: {e}")
        return []
def analyze_task_intent(message_content):
    """Use AI to analyze message and extract task details - with better extraction"""
    try:
        prompt = f"""
You are a task extraction assistant. Analyze this Discord message and extract ALL task details.

Message: "{message_content}"

Return ONLY valid JSON in this exact format:
{{
    "is_task_request": true,
    "title": "short task title (under 10 words)",
    "description": "detailed description of what needs to be done",
    "status": "Todo",
    "priority": "High or Medium or Low",
    "difficulty": "Hard or Medium or Easy",
    "assignee": "person's name or null"
}}

Extraction Rules:
1. **Priority**: Look for keywords like "high priority", "urgent", "🔥" → High; "low priority", "minor" → Low; else Medium
2. **Difficulty**: Look for "hard", "complex", "💪" → Hard; "easy", "simple", "quick" → Easy; else Medium
3. **Assignee**: Extract name after "for", "assign to", "assigned to", or @mentions
4. **Description**: Extract detailed description (usually after dashes or priority mentions)
5. **Title**: Keep it short and clear (first meaningful phrase)

Examples:
- "Build login page - High priority, Hard difficulty, assign to Sarah - Implement JWT auth"
  → title: "Build login page", priority: "High", difficulty: "Hard", assignee: "Sarah", description: "Implement JWT auth"

- "Fix CSS bugs - Low priority for John - Quick fix needed"
  → title: "Fix CSS bugs", priority: "Low", difficulty: "Easy", assignee: "John", description: "Quick fix needed"

- "Design landing page - Medium difficulty - Create mockups and prototypes"
  → title: "Design landing page", priority: "Medium", difficulty: "Medium", assignee: null, description: "Create mockups and prototypes"
"""
        
        response = model.generate_content(prompt)
        text = response.text.strip()
        
        # Extract JSON from response (more robust parsing)
        import re
        json_match = re.search(r'\{[\s\S]*\}', text)
        if json_match:
            text = json_match.group(0)
        
        result = json.loads(text)
        
        # Validate and normalize
        if not isinstance(result.get('is_task_request'), bool):
            result['is_task_request'] = True
        
        # Ensure priority is valid
        if result.get('priority') not in ['High', 'Medium', 'Low']:
            result['priority'] = 'Medium'
        
        # Ensure difficulty is valid
        if result.get('difficulty') not in ['Hard', 'Medium', 'Easy']:
            result['difficulty'] = 'Medium'
        
        # Ensure status is valid
        if result.get('status') not in ['Backlog', 'Todo', 'In Progress', 'Done']:
            result['status'] = 'Todo'
        
        return result
        
    except Exception as e:
        print(f"AI analysis error: {e}")
        # FALLBACK: Pattern-based parsing
        return fallback_parse_task(message_content)

def fallback_parse_task(message_content):
    """
    Enhanced fallback parser - extracts priority, difficulty, assignee, and description
    Examples:
    - "Build login page - High priority, Hard difficulty, assign to Sarah - Implement JWT auth"
    - "Fix CSS bugs - Low priority for John - Quick fix needed"
    - "Design landing page - Medium difficulty - Create mockups and prototypes"
    """
    import re
    
    message_lower = message_content.lower()
    
    # Check if it looks like a task request
    task_keywords = ['task', 'create', 'build', 'fix', 'implement', 'add', 'update', 'work on', 'design']
    is_task = any(keyword in message_lower for keyword in task_keywords)
    
    if not is_task:
        return {"is_task_request": False, "title": None, "description": None}
    
    # Extract priority
    priority = "Medium"  # Default
    if any(word in message_lower for word in ['high priority', 'urgent', 'critical', 'important', '🔥']):
        priority = "High"
    elif any(word in message_lower for word in ['low priority', 'minor', 'nice to have', 'when possible']):
        priority = "Low"
    
    # Extract difficulty
    difficulty = "Medium"  # Default
    if any(word in message_lower for word in ['hard difficulty', 'complex', 'difficult', 'challenging', '💪']):
        difficulty = "Hard"
    elif any(word in message_lower for word in ['easy difficulty', 'simple', 'quick', 'straightforward', '🟢']):
        difficulty = "Easy"
    
    # Extract assignee (name after "for", "assign to", "assigned to")
    assignee = None
    assignee_patterns = [
        r'(?:for|assign(?:ed)?\s+to)\s+([A-Z][a-z]+(?:\s+[A-Z][a-z]+)?)',
        r'@([A-Z][a-z]+)',
    ]
    for pattern in assignee_patterns:
        match = re.search(pattern, message_content)
        if match:
            assignee = match.group(1).strip()
            break
    
    # Extract description (text after the last dash, or after priority/difficulty mentions)
    description = message_content
    # Try to find description after dashes
    if ' - ' in message_content:
        parts = message_content.split(' - ')
        if len(parts) > 1:
            # Last part is usually the description
            description = parts[-1].strip()
            # But if it's just priority/difficulty, use second to last
            if description.lower() in ['high priority', 'low priority', 'medium priority', 
                                       'hard difficulty', 'easy difficulty', 'medium difficulty']:
                if len(parts) > 2:
                    description = parts[-2].strip()
    
    # Extract title (first meaningful phrase, before dashes or priority mentions)
    title = message_content
    # Remove priority/difficulty keywords from title
    title = re.sub(r'\s*[-–—]\s*(?:high|low|medium)\s*(?:priority|difficulty).*$', '', title, flags=re.I)
    # Remove assignee mentions from title
    title = re.sub(r'\s*(?:for|assign(?:ed)?\s+to)\s+[A-Z][a-z\s]+$', '', title, flags=re.I)
    # Clean up and limit length
    title = title.split(' - ')[0].strip() if ' - ' in title else title
    title = title[:80] if len(title) > 80 else title
    
    # If title is empty or same as description, use first few words
    if not title or title == description:
        words = message_content.split()[:5]
        title = ' '.join(words) + "..." if len(words) >= 5 else ' '.join(words)
    
    return {
        "is_task_request": True,
        "title": title if title else "Untitled Task",
        "description": description if description and description != title else message_content,
        "status": "Todo",  # Default status
        "priority": priority,
        "difficulty": difficulty,
        "assignee": assignee
    }

@bot.event
async def on_ready():
    print(f'✅ HackFlow Bot is online!')
    print(f'Bot ID: {bot.user.id}')
    print(f'Connected to {len(bot.guilds)} server(s)')

@bot.command(name='task')
async def create_task(ctx, *, message: str):
    """Create a task from Discord message - flexible parsing"""
    await ctx.typing()
    
    # Analyze message with AI (with fallback)
    analysis = analyze_task_intent(message)
    
    # If AI/parsing says it's not a task, try to create anyway with defaults
    if not analysis.get('is_task_request', False):
        # Still try to create with the raw message as description
        analysis = {
            "is_task_request": True,
            "title": message[:50] + "..." if len(message) > 50 else message,
            "description": message,
            "status": "Todo",
            "priority": "Medium",
            "difficulty": "Medium",
            "assignee": None
        }
    
    # Create task in backend
    task = await create_task_in_backend(
        title=analysis.get('title', 'Untitled Task'),
        description=analysis.get('description', message),
        status=analysis.get('status', 'Todo'),
        priority=analysis.get('priority', 'Medium'),
        difficulty=analysis.get('difficulty', 'Medium'),
        assignee_name=analysis.get('assignee')
    )
    
    if task:
        embed = discord.Embed(
            title="✅ Task Created!",
            description=f"**{task['title']}**",
            color=discord.Color.green()
        )
        embed.add_field(name="📝 Description", value=task['description'][:100] + "..." if len(task['description']) > 100 else task['description'], inline=False)
        embed.add_field(name="📊 Status", value=task['status'], inline=True)
        embed.add_field(name="🔥 Priority", value=task['priority'], inline=True)
        embed.add_field(name="💪 Difficulty", value=task['difficulty'], inline=True)
        embed.add_field(name="👤 Assignee", value=task.get('assigneeName', 'Unassigned'), inline=True)
        embed.add_field(name="🆔 Task ID", value=task['id'][-8:], inline=True)
        embed.set_footer(text=f"Created by {ctx.author.display_name} • HackFlow Bot")
        embed.timestamp = datetime.utcnow()
        
        await ctx.send(embed=embed)
    else:
        await ctx.send("❌ Failed to create task. Is the backend running on http://localhost:8080?")

@bot.command(name='tasks')
async def list_tasks(ctx):
    """List all tasks"""
    await ctx.typing()
    
    tasks = await get_all_tasks()
    
    if not tasks:
        await ctx.send("📋 No tasks found!")
        return
    
    # Group by status
    status_groups = {'Backlog': [], 'Todo': [], 'In Progress': [], 'Done': []}
    
    for task in tasks:
        status = task.get('status', 'Todo')
        if status in status_groups:
            status_groups[status].append(task)
    
    embed = discord.Embed(
        title="📊 HackFlow Task Board",
        description=f"Total: **{len(tasks)}** tasks",
        color=discord.Color.blue()
    )
    
    for status, status_tasks in status_groups.items():
        if status_tasks:
            task_list = "\n".join([f"• {t['title']}" for t in status_tasks[:3]])
            if len(status_tasks) > 3:
                task_list += f"\n• ...and {len(status_tasks) - 3} more"
            embed.add_field(name=f"{status} ({len(status_tasks)})", value=task_list, inline=False)
    
    embed.set_footer(text="HackFlow Bot • Use !task <description> to create")
    
    await ctx.send(embed=embed)

@bot.command(name='botinfo')  # ← Renamed from 'help' to avoid conflict
async def show_bot_info(ctx):
    """Show bot information and commands"""
    embed = discord.Embed(
        title="🤖 HackFlow Bot Commands",
        description="AI-powered project management for your Discord server!",
        color=discord.Color.purple()
    )
    
    embed.add_field(name="📝 `!task <description>`", value="Create a new task using natural language", inline=False)
    embed.add_field(name="📊 `!tasks`", value="View all tasks grouped by status", inline=False)
    embed.add_field(name="❓ `!botinfo`", value="Show this help message", inline=False)
    
    embed.add_field(name="💡 Examples", value="`!task Create login page for the app`\n`!task Fix database bug - high priority`", inline=False)
    
    embed.set_footer(text="HackFlow Bot v1.0")
    
    await ctx.send(embed=embed)

@bot.event
async def on_message(message):
    if message.author == bot.user:
        return
    await bot.process_commands(message)

# Run the bot
if __name__ == "__main__":
    TOKEN = os.getenv('DISCORD_TOKEN')
    if not TOKEN:
        print("❌ Error: DISCORD_TOKEN not found in .env file!")
        exit(1)
    
    bot.run(TOKEN)