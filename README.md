# The Unwritten Parable

A Stanley Parable-inspired narrator mod for Minecraft 1.20.1 (Forge).

## Overview

The Unwritten Parable brings the iconic Stanley Parable narration experience to Minecraft. The narrator comments on your every move, breaking the fourth wall and questioning your choices.

## Features

- **6 Unique Narrator Personalities** - Each with distinct commentary styles
- **Multiplayer Support** - Herobrine, Notch, and Chuck Norris narrators on LAN/servers
- **Player Action Narration** - Comments on mining, crafting, sleeping, and more
- **Health & Hunger States** - Narrator reacts to your condition
- **Command Reactions** - Narrator comments when you use commands
- **Halloween Event** - Special Herobrine takeover in October
- **Boss Bar Timer** - Wither-style timer that drains over 3 minutes
- **Client-Side Only** - Works on any server without installation

## Narrator Personalities

| Name | Style |
|------|-------|
| Stanley (The Narrator) | Meta commentary, fourth-wall breaking |
| Nigel (The Guide) | Overly enthusiastic, desperate for approval |
| Malcolm (The Critic) | Sarcastic, bored, unimpressed |
| Edgar (The Void) | Dark, ominous, existential |
| Chester (The Optimist) | Relentlessly positive |
| Reginald (The Ego) | Takes credit for everything |

## Multiplayer Narrators

| Name | Description |
|------|-------------|
| Notch | Observes silently |
| Herobrine | Uses enchanting text, cuts off |
| Chuck Norris | Easter egg (never loses!) |

## Commands

- `/narrator info` - Show current narrator settings
- `/narrator test <1-6>` - Test narrator message
- `/narrator broadcast <1-6>` - Test message (bypasses cooldown)
- `/narrator testnarrator <name> <1-6>` - Test specific narrator
- `/narrator fight <narrator1> <narrator2>` - Start narrator fight
- `/narrator on/off/toggle` - Enable/disable narrator
- `/narrator rules` - Show game rules
- `/narrator help` - Show help

## Installation

1. Install Forge 1.20.1
2. Download the mod jar
3. Place in your `mods` folder
4. Launch Minecraft

## Configuration

Edit `.minecraft/config/theunwrittenparable-common.toml`:
- `cooldownSeconds` - Time between messages (default: 180)
- `enableCombatSilence` - Silence during combat
- `enableDeathCommentary` - Comment on deaths
- `enableEssentialIntegration` - Essential mod support

## Language Support

The mod uses Minecraft's translation system. To add a language:
1. Create a new file in `assets/theunwrittenparable/lang/` (e.g., `ja_jp.json`)
2. Copy the structure from `en_us.json`
3. Translate the messages

## Compatibility

- **Minecraft:** 1.20.1
- **Forge:** 47+
- **Client-side only** - Works on any server
- **Essential mod** - Optional integration

## Links

- [GitHub](https://github.com/DarkPureAcid/TheUnwrittenParable)
- [Issue Tracker](https://github.com/DarkPureAcid/TheUnwrittenParable/issues)

## License

MIT License - See [LICENSE.txt](LICENSE.txt)

## Credits

- Inspired by [The Stanley Parable](https://www.stanleyparable.com/)
- Created by DarkPureAcid