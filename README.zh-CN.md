# PatPat NH

[English](README.md)

这是 [PatPat](https://github.com/LopyMine/PatPat) 模组在 Minecraft 1.7.10 / Forge 环境下的移植版本。

目标：

- 玩家潜行 + 主手空手 + 右键活体实体后触发拍头动画
- 仅客户端安装也可使用（无需服务端）
- 服务端安装时，将拍头动作同步给附近玩家
- 资源包可覆盖动画纹理、配置和音效，并尽量兼容上游资源包格式

## 构建

Windows：

```bat
gradlew.bat build
gradlew.bat runClient
gradlew.bat runServer
```

## 资源包说明（1.7.10 移植）

### 必要目录结构

资源包文件应放在：

```text
assets/patpat/textures/
	your_animation.json 或 .json5
	your_texture.png
```

### 实体 ID 兼容

现代资源包常见写法：

- `minecraft:Zombie`
- `minecraft:EnderDragon`

而 1.7.10 常见实体名是驼峰形式（例如 `EnderDragon`）。
本移植会对实体 ID 做归一化匹配，使现代 snake_case 写法也能匹配到 1.7.10 的实体名。

### 末影龙示例

要匹配末影龙，可在 `entities` 中使用以下任一写法：

- `minecraft:ender_dragon`
- `ender_dragon`
- `EnderDragon`

### 移植建议

1. 纹理路径保持为 `patpat:textures/<file>.png`。
2. 动画字段尽量与上游一致（`duration`、`frame`、`sound`、`entities`）。
3. 资源包里优先使用现代命名空间 ID，本移植已处理与旧实体名的映射。
4. 若动画仍未生效，先确认目标实体已被写入 `entities`（例如只配置了 `minecraft:zombie` 就不会作用于末影龙）。

## 参考

- 上游仓库：https://github.com/LopyMine/PatPat
- 上游 Wiki：https://github.com/LopyMine/PatPat/wiki/
