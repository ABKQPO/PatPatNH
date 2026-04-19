# PatPat NH

[中文说明](README.zh-CN.md)

A Minecraft 1.7.10 / Forge port of the [PatPat](https://github.com/LopyMine/PatPat) mod.

Goals:

- Sneak + empty main hand + right-click a living entity to trigger a pat animation
- Works client-only (no server required)
- When installed on the server, syncs pat actions to nearby players
- Resource packs can override animation textures, configs, and sounds — compatible with upstream pack format

## Build

Windows:

```bat
gradlew.bat build
gradlew.bat runClient
gradlew.bat runServer
```

## Resource Pack Notes (1.7.10 Port)

### Required Pack Structure

Pack files must be placed under:

```text
assets/patpat/textures/
	your_animation.json or .json5
	your_texture.png
```

### Entity ID Compatibility

Modern packs often use IDs like:

- `minecraft:Zombie`
- `minecraft:EnderDragon`

Legacy 1.7.10 entity names are typically camel-case (for example `EnderDragon`).
This port normalizes IDs so modern snake_case IDs can still match legacy names.

### Ender Dragon Example

To target Ender Dragon, use one of these in `entities`:

- `minecraft:ender_dragon`
- `ender_dragon`
- `EnderDragon`

### Migration Suggestions

1. Keep texture paths as `patpat:textures/<file>.png`.
2. Keep animation fields compatible with upstream (`duration`, `frame`, `sound`, `entities`).
3. Prefer modern namespaced IDs in packs; this port handles legacy mapping.
4. If an animation still does not apply, verify the target entity is included in `entities` (for example, a pack configured only for `minecraft:zombie` will not affect Ender Dragon).

## References

- Upstream repository: https://github.com/LopyMine/PatPat
- Upstream wiki: https://github.com/LopyMine/PatPat/wiki/
