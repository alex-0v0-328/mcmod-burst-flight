# Burst Flight 爆发飞行 模组

简体中文 | [繁體中文](README.zh-TW.md) | [English](README.en.md)

双击疾跑键，像创造模式一样起飞，速度是原版创造飞行的 3 倍（倍数可调）。再双击一次，或者落地，飞行就结束。

## 玩法

- 双击疾跑键（默认左 Ctrl）起飞；站在地上时会先跳起来。
- 飞行中和创造模式一样：WASD 移动，空格上升，潜行下降，按住疾跑再快一倍。
- 飞行时视野会变宽，增强速度感；可以关掉或调整幅度。
- 再双击疾跑键，或者落地，飞行结束。生存模式会从结束的位置落下，照常受摔落伤害。
- 旁观模式、骑乘和睡觉时不能使用。
- 服务端和客户端都要安装。

## 配置

在游戏里：模组列表 → Burst Flight → 配置（界面只有英文）。

服务端配置在每个世界的 `serverconfig/burst_flight-server.toml`；整合包可以把默认值放在 `defaultconfigs/`。

| 配置项       | 默认值 | 说明                                                          |
|--------------|--------|---------------------------------------------------------------|
| `multiplier` | `3.0`  | 飞行速度，原版创造飞行的几倍，范围 1–8                        |
| `everyone`   | `true` | 是否所有玩家都能用；设为 `false` 时只有其他模组允许的玩家能用 |

客户端配置在 `config/burst_flight-client.toml`，每个玩家自己设置：

| 配置项        | 默认值 | 说明                                                                                                                        |
|---------------|--------|-----------------------------------------------------------------------------------------------------------------------------|
| `fovEffect`   | `true` | 飞行时是否把视野变宽                                                                                                        |
| `fovIncrease` | `15`   | 视野变宽多少，单位是百分比，范围 0–50；原版的“视场角效果”滑块会按比例缩小它，原版另外限制疾跑、飞行和这里加起来最多变宽 50% |

## 在你的模组里使用

通过 [JitPack](https://jitpack.io) 引入，版本填 GitHub 上的发布标签（例如 `v1.0.0`）或某次提交的哈希：

```groovy
repositories {
    maven {
        url = 'https://jitpack.io'
        content { includeGroup 'com.github.alex-0v0-328' }
    }
}

dependencies {
    implementation 'com.github.alex-0v0-328:mcmod-burst-flight:<版本>'
}
```

在 `neoforge.mods.toml` 里声明依赖：

```toml
[[dependencies.your_mod_id]]
    modId="burst_flight"
    type="required"
    versionRange="[1.0.0,)"
    ordering="NONE"
    side="BOTH"
```

API 在 `net.alex.burstflight.api.BurstFlightApi`，只在服务端调用，参数是 `ServerPlayer`：

| 方法                          | 作用                                                   |
|-------------------------------|--------------------------------------------------------|
| `allow(player)`               | 允许这名玩家使用，倍数跟随服务端配置（默认 3）         |
| `allow(player, multiplier)`   | 允许这名玩家使用，并指定倍数（限制在 1–8）             |
| `deny(player)`                | 禁止这名玩家使用；正在飞行会立刻结束                   |
| `reset(player)`               | 撤销上面的决定，交还给服务端配置                       |
| `getMultiplier(player)`       | 当前生效的倍数，0 表示不能使用                         |
| `isBursting(player)`          | 是否正在爆发飞行                                       |

决定随玩家存档保存，死亡后仍然有效，所以调用一次就够了；再次调用会覆盖。正在飞行时改倍数，一个游戏刻内就生效。

```java
BurstFlightApi.allow(serverPlayer, 5.0);
```

## 依赖

- Minecraft 1.21.1
- NeoForge 21.1.252 或更新

## 许可

[MIT](LICENSE.txt)
