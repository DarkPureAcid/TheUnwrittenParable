# The Unwritten Parable - Changelog

## Version 1.0.0 (October 13, 2026)

### Initial Release

**Core Features:**
- Stanley Parable-inspired narrator mod for Minecraft 1.20.1 (Forge)
- Client-side only - works on any server without server installation
- 6 unique narrator personalities with distinct commentary styles

**Narrator Personalities:**
- **Stanley (The Narrator)** - Meta commentary, fourth-wall breaking
- **Nigel (The Guide)** - Overly enthusiastic, desperate for approval
- **Malcolm (The Critic)** - Sarcastic, bored, unimpressed
- **Edgar (The Void)** - Dark, ominous, existential
- **Chester (The Optimist)** - Relentlessly positive
- **Reginald (The Ego)** - Takes credit for everything

**Multiplayer Narrators (LAN/Server):**
- **Notch** - Observes silently
- **Herobrine** - Uses enchanting text (Standard Galactic), text cuts off
- **Chuck Norris** - Easter egg with Minecraft-style Chuck Norris jokes (never loses)

**Player Action Narration:**
- Block breaking (survival and creative modes)
- Biome changes
- Death and combat
- Idle/standing still
- Achievements
- Health states (full, half, 25%)
- Hunger states (full, half, low)
- Sleeping and waking up
- Crafting and smelting
- Mining and trading
- XP level ups
- Opening containers (chests, crafting tables, furnaces)
- Command usage (gamemode, time, weather, tp, etc.)
- Difficulty changes

**Special Features:**
- Boss bar timer (Wither-style, drains over 3 minutes)
- Herobrine name animation (English to enchanting text)
- Herobrine.EXE takeover on LAN
- Narrator fight system (Chuck Norris never loses)
- Halloween event (October 25-31)
- Mod scanner for modpack compatibility

**Commands:**
- `/narrator info` - Show current narrator settings
- `/narrator test <1-6>` - Test narrator message (respects cooldown)
- `/narrator broadcast <1-6>` - Test narrator message (bypasses cooldown)
- `/narrator testnarrator <name> <1-6>` - Test specific narrator
- `/narrator fight <narrator1> <narrator2>` - Start narrator fight
- `/narrator stopfight` - Stop current fight
- `/narrator on/off/toggle` - Enable/disable narrator
- `/narrator rules` - Show game rules
- `/narrator halloween` - Toggle Halloween mode (debug)
- `/narrator help` - Show help

**Configuration:**
- Configurable cooldown (default 3 minutes)
- Combat silence toggle
- Death commentary toggle
- Essential mod integration (optional)
- Per-world narrator personality (random assignment)

**Language Support:**
- Uses Minecraft's translation system
- Player's username appears in messages
- Supports multiple languages (add lang files)

**Technical:**
- Forge 1.20.1
- Client-side only
- No dependencies (Essential mod optional)
- JSON config for data storage
- Game rule system for toggling features

**Known Issues:**
- Command detection via CommandEvent (may not work on all servers)
- Boss bar uses reflection (may break with Minecraft updates)

**Future Plans:**
- TTS integration (TTSReader)
- Player models (first-person only)
- More narrator personalities
- More player action commentary
- Custom message packs