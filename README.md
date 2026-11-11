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
3. Optionally suppress the bottle (`drop_ominous_bottle`)

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

| Key                    | Values                | Default     | Meaning                                                                 |
|------------------------|-----------------------|-------------|-------------------------------------------------------------------------|
| `enabled`              | bool                  | `true`      | Master switch                                                           |
| `bad_omen_probability` | `0.0`-`1.0`           | `1.0`       | Chance to convert a bottle drop into Bad Omen                           |
| `drop_ominous_bottle`  | `otherwise`, `always` | `otherwise` | `otherwise` = no bottle when effect applied; `always` = effect + bottle |
| `levels`               | `stack`, `drop`       | `stack`     | `stack` = +1 per kill (max V); `drop` = same level as the bottle        |
| `duration_ticks`       | int                   | `120000`    | Effect duration (vanilla bottle = 100 minutes)                          |

If the probability roll **fails**, vanilla continues unchanged (bottle drops, no effect from this mod).

## Levels
- **`stack`**: pre-1.21 style: each successful kill increases Bad Omen by 1, up to level `V`.
- **`drop`**: uses the amplifier vanilla rolled on the bottle (`I` to `V`).

## License
Apache-2.0
