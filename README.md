# Classic Bad Omen
Minecraft mod that restores **instant Bad Omen** when a raid captain would drop an **ominous bottle**.

Server-side / singleplayer. No dependencies. Clients do not need the mod.

## Design
The mod does **not** reimplement captain / raid / distance checks.

It hooks at the moment vanilla is about to **spawn an ominous bottle from a `Raider`**. That path already encodes:
- patrol / outpost captain
- not participating in a nearby raid
- killed in a way that grants the bottle

Then:
1. Roll `bad_omen_probability`
2. On **success**, apply Bad Omen to the beneficiary (killer / pet owner)
3. Apply the configured bottle policy (`drop_ominous_bottle`)

Trial-chamber vault bottles and any non-raider source are untouched.

## Config
File: `config/classic-bad-omen.json` (created on first launch)

```json
{
  "enabled": true,
  "bad_omen_probability": 1.0,
  "drop_ominous_bottle": "otherwise",
  "levels": "stack",
  "duration_ticks": 120000
}
```

| Key                    | Values                         | Default     | Meaning                                                          |
|------------------------|--------------------------------|-------------|------------------------------------------------------------------|
| `enabled`              | bool                           | `true`      | Master switch                                                    |
| `bad_omen_probability` | `0.0`-`1.0`                    | `1.0`       | Chance to apply Bad Omen                                         |
| `drop_ominous_bottle`  | `otherwise`, `always`, `never` | `otherwise` | See bottle policy below                                          |
| `levels`               | `stack`, `drop`                | `stack`     | `stack` = +1 per kill (max V); `drop` = same level as the bottle |
| `duration_ticks`       | int                            | `120000`    | Effect duration (vanilla bottle = 100 minutes)                   |

### Bottle policy (`drop_ominous_bottle`)
| Value       | On probability success | On probability fail   |
|-------------|------------------------|-----------------------|
| `otherwise` | Effect, no bottle      | Bottle only (vanilla) |
| `always`    | Effect + bottle        | Bottle only (vanilla) |
| `never`     | Effect, no bottle      | No bottle             |

**Warning for `never`:**
Captains will no longer drop ominous bottles. This removes one of the normal ways to obtain them.  
Ominous bottles are required for Trial Chambers (ominous trials / vaults).
Prefer `otherwise` or `always` if players still need that source, or provide them by other means.

## Levels
- **`stack`**: pre-1.21 style: each successful kill increases Bad Omen by 1, up to level `V`.
- **`drop`**: uses the amplifier vanilla rolled on the bottle (`I` to `V`).

## Test command
Summon a harmless raid captain (hold an ominous banner in main hand first):
```
/summon pillager ~ ~ ~ {PatrolLeader:1b}
/item replace entity @n[type=pillager] armor.head from entity @s weapon.mainhand
```

## License
Apache-2.0
