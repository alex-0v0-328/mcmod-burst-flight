# Burst Flight 爆發飛行 模組

[简体中文](README.md) | 繁體中文 | [English](README.en.md)

連按兩下衝刺鍵，像創造模式一樣起飛，速度是原版創造飛行的 3 倍（倍數可調）。再連按兩下，或者落地，飛行就結束。

本文譯自[簡體中文版](README.md)；各版本不一致時，以簡體中文版為準。

## 玩法

- 連按兩下衝刺鍵（預設左 Ctrl）起飛；站在地上時會先跳起來。
- 飛行中和創造模式一樣：WASD 移動，空白鍵上升，潛行下降，按住衝刺再快一倍。
- 飛行時視野會變寬，增強速度感；可以關掉或調整幅度。
- 再連按兩下衝刺鍵，或者落地，飛行結束。生存模式會從結束的位置落下，照常受到摔落傷害。
- 旁觀模式、騎乘和睡覺時不能使用。
- 伺服器端和用戶端都要安裝。

## 設定

在遊戲裡：模組列表 → Burst Flight → 設定（介面只有英文）。

伺服器設定在每個世界的 `serverconfig/burst_flight-server.toml`；模組包可以把預設值放在 `defaultconfigs/`。

| 設定項       | 預設值 | 說明                                                              |
|--------------|--------|-------------------------------------------------------------------|
| `multiplier` | `3.0`  | 飛行速度，原版創造飛行的幾倍，範圍 1–8                            |
| `everyone`   | `true` | 是否所有玩家都能使用；設為 `false` 時只有其他模組允許的玩家能使用 |

用戶端設定在 `config/burst_flight-client.toml`，每位玩家自己設定：

| 設定項        | 預設值 | 說明                                                                                                                        |
|---------------|--------|-----------------------------------------------------------------------------------------------------------------------------|
| `fovEffect`   | `true` | 飛行時是否把視野變寬                                                                                                        |
| `fovIncrease` | `15`   | 視野變寬多少，單位是百分比，範圍 0–50；原版的「視野效果」滑桿會按比例縮小它，原版另外限制衝刺、飛行和這裡加起來最多變寬 50% |

## 在你的模組裡使用

透過 [JitPack](https://jitpack.io) 引入，版本填 GitHub 上的發佈標籤（例如 `v1.0.0`）或某次提交的雜湊值：

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

在 `neoforge.mods.toml` 裡宣告相依性：

```toml
[[dependencies.your_mod_id]]
    modId="burst_flight"
    type="required"
    versionRange="[1.0.0,)"
    ordering="NONE"
    side="BOTH"
```

API 在 `net.alex.burstflight.api.BFApi`，只在伺服器端呼叫，參數是 `ServerPlayer`：

| 方法                        | 作用                                                   |
|-----------------------------|--------------------------------------------------------|
| `allow(player)`             | 允許這名玩家使用，倍數跟隨伺服器設定（預設 3）         |
| `allow(player, multiplier)` | 允許這名玩家使用，並指定倍數（限制在 1–8）             |
| `deny(player)`              | 禁止這名玩家使用；正在飛行會立刻結束                   |
| `reset(player)`             | 撤銷上面的決定，交還給伺服器設定                       |
| `getMultiplier(player)`     | 目前生效的倍數，0 表示不能使用                         |
| `isBursting(player)`        | 是否正在爆發飛行                                       |

決定隨玩家存檔保存，死亡後仍然有效，所以呼叫一次就夠了；再次呼叫會覆蓋。正在飛行時改倍數，一個遊戲刻內就生效。

```java
BFApi.allow(serverPlayer, 5.0);
```

## 相依性

- Minecraft 1.21.1
- NeoForge 21.1.252 或更新

## 授權

[MIT](LICENSE.txt)
