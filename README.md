# Tab Modifier

[![Version](https://img.shields.io/badge/version-2.0.1-green.svg)](https://github.com/dtkdtk/TabModifier)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

# ❗ NOTE: The project is no longer maintained. Use [Active fork (v2.0)](https://github.com/dtkdtk/TabModifier)
===================================================

A simple and flexible tab list manager for SpongeForge servers, built on **SpongeAPI 7.4.0** with **LuckPerms** integration and **PlaceholderAPI** support.

---

## Features

- ✅ Full tab list customization (header & footer)
- ✅ Player name formatting with prefix and suffix
- ✅ LuckPerms integration for permission-based formatting
- ✅ PlaceholderAPI support for dynamic placeholders
- ✅ Real-time updates with configurable intervals
- ✅ Per-player refresh delay to combat server lag
- ✅ Multi-line header/footer support
- ✅ Color codes support (`&` formatting)

---

## Requirements

This plugin requires the following dependencies to be installed on your server:

| Dependency         | Version      | Required      |
|--------------------|--------------|---------------|
| **SpongeForge**    | 1.12.2-2838+ | Yes           |
| **LuckPerms**      | 5.4+         | Yes           |
| **PlaceholderAPI** | 4.4+         | No (optional) |

---

## Commands

### Base Command: `/tabmodifier`

| Command                            | Permission                                        | Description                                       |
|------------------------------------|---------------------------------------------------|---------------------------------------------------|
| `/tabmodifier reload`              | `me.nipo.tabmodifier.reload`                      | Reload the plugin configuration                   |
| `/tabmodifier refresh`             | `me.nipo.tabmodifier.refresh`                     | Refresh tab list for all players                  |
| `/tabmodifier setheader <message>` | `me.nipo.tabmodifier.setheader`                   | Set and save the tab header                       |
| `/tabmodifier setfooter <message>` | `me.nipo.tabmodifier.setfooter`                   | Set and save the tab footer                       |
| `/tabmodifier` (no subcommand)     | any subcommand perm or `me.nipo.tabmodifier.help` | Show available commands based on your permissions |

> **Note:** The alias `/tab` has been removed in v2.0 to avoid conflicts with other plugins.

---

##  Configuration

| Option                           | Type              | Default                      | Description                                                                           |
|----------------------------------|-------------------|------------------------------|---------------------------------------------------------------------------------------|
| `HeaderValue`                    | String            | `"ayo&aoo"`                  | Tab header text. Supports `&` color codes and PlaceholderAPI.                         |
| `FooterValue`                    | String            | `"%time%"`                   | Tab footer text. Supports `&` color codes and PlaceholderAPI.                         |
| `showHeader`                     | Boolean           | `true`                       | Whether to display the header.                                                        |
| `showFooter`                     | Boolean           | `true`                       | Whether to display the footer.                                                        |
| `showPrefix`                     | Boolean           | `true`                       | Whether to display the prefix.                                                        |
| `showSuffix`                     | Boolean           | `true`                       | Whether to display the suffix.                                                        |
| `showDisplayName`                | Boolean           | `true`                       | Whether to display the custom display name.                                           |
| `InitialValue > Prefix`          | String            | `"&d[&bMC&d] "`              | Fallback prefix if no user/group prefix is found.                                     |
| `InitialValue > Suffix`          | String            | `" &9[&b%player_health%&9]"` | Fallback suffix if no user/group suffix is found.                                     |
| `RefreshDelay`                   | Integer (ticks)   | `5`                          | Delay before updating a new player's tab list. Helps with lag. Set to `0` to disable. |
| `UpdateInterval > Header&Footer` | Integer (seconds) | `5`                          | How often to update the header and footer.                                            |
| `UpdateInterval > NameList`      | Integer (seconds) | `10`                         | How often to update player names.                                                     |

> **Priority:** User Prefix > Group Prefix > `InitialValue Prefix`  
> **Priority:** User Suffix > Group Suffix > `InitialValue Suffix`

---

## What's New in v2.0

- **Command info**: `/tabmodifier` and `help` subcommand now shows plugin info & available subcommands based on your permissions
- **Removed alias**: The `/tab` alias has been removed to prevent conflicts
- **Updated dependencies**:
  - SpongeAPI: `7.1.0` → `7.4.0`
  - LuckPerms: `4.x` → `5.4`
  - PlaceholderAPI: `4.4`
- **Rewrite README, add MIT License, add `build.gradle`**
- **Re-maintained fork**: It's alive!

---

## FAQ

<details>
<summary><b>How do I set a prefix/suffix?</b></summary>

Use LuckPerms commands:
```sh
lp user <username> meta setprefix <weight> <prefix>
lp group <groupname> meta setprefix <weight> <prefix>
```
</details>

<details>
<summary><b>Does this support PlaceholderAPI?</b></summary>

Yes! You can use PlaceholderAPI placeholders anywhere — in prefixes, suffixes, headers, and footers. Theoretically, all PlaceholderAPI placeholders are supported.
</details>

<details>
<summary><b>Does it support multi-line headers/footers?</b></summary>

Yes! Use `\n` to create new lines.
</details>

<details>
<summary><b>Does it support color codes?</b></summary>

Yes! Use `&` followed by a color code (e.g., `&c` for red, `&l` for bold).
</details>

---

## 📄 License

This project is licensed under the **MIT License**. Original author is Nipo *(NipoCN)*.

# [Issues & Bugs (GitHub)](https://github.com/dtkdtk/TabModifier/issues)

[Source code (GitHub)](https://github.com/dtkdtk/TabModifier)
