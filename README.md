# The Unwritten Parable

A Stanley Parable-inspired narrator mod for Minecraft 1.20.1 (Forge).

## Features

- **Dynamic Narrator** - Comments on your actions in real-time
- **6 Narrator Personalities** - Stanley, Helpful, Sarcastic, Dark, Optimistic, Narcissistic
- **Per-World Settings** - Each world has its own narrator personality
- **Combat Detection** - Narrator stays silent during fights
- **Death Commentary** - Narrator mocks you when you die
- **Multiplayer Support** - Herobrine/Notch narrator on servers
- **Configurable Cooldown** - Default 3 minutes between messages

## Commands

- `/narrator select <1-6>` - Choose narrator personality
- `/narrator info` - Show current narrator settings
- `/narrator test <1-5>` - Test a narrator message type
- `/narrator help` - Show command help

## Message Types

1. Block breaking
2. Biome changes
3. Death
4. Combat
5. Idle

## Configuration

Edit `theunwrittenparable-common.toml` in your config folder:
- `enableNarrator` - Enable/disable narrator
- `cooldownSeconds` - Time between messages (default: 180)
- `enableCombatSilence` - Silence during combat
- `enableDeathCommentary` - Comment on deaths

## Building

1. Download Forge MDK for 1.20.1
2. Copy gradle files to project directory
3. Run `gradlew build`
4. Find jar in `build/libs/`

## Planned Features

- Essential mod integration for friend-based narration
- More narrator personalities
- Custom message packs
- Narrator voice packs