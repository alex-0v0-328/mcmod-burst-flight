# Burst Flight Mod

[简体中文](README.md) | [繁體中文](README.zh-TW.md) | English

Double-tap the sprint key to take off like in creative mode, at 3 times vanilla creative flight speed (the multiple is configurable). Double-tap again, or land, and the flight ends.

This is a translation of [README.md](README.md); where the versions differ, the Simplified Chinese one prevails.

## How to play

- Double-tap the sprint key (left Ctrl by default) to take off; standing on the ground, you jump first.
- In flight it works like creative mode: WASD to move, Space to rise, Sneak to descend, hold Sprint to go twice as fast.
- The view widens in flight for a stronger sense of speed; you can turn it off or change how much.
- Double-tap the sprint key again, or land, and the flight ends. In survival you fall from where it ended and take fall damage as usual.
- Not available in spectator mode, while riding or while sleeping.
- Install it on both the server and the client.

## Configuration

In game: Mods → Burst Flight → Config.

The server settings live in each world's `serverconfig/burst_flight-server.toml`; a modpack can ship its defaults in `defaultconfigs/`.

| Option       | Default | Description                                                                        |
|--------------|---------|------------------------------------------------------------------------------------|
| `multiplier` | `3.0`   | Flying speed as a multiple of vanilla creative flight, from 1 to 8                 |
| `everyone`   | `true`  | Whether every player may use it; with `false`, only players another mod allows can |

The client settings live in `config/burst_flight-client.toml`, one per player:

| Option        | Default | Description                                                                                                                                              |
|---------------|---------|----------------------------------------------------------------------------------------------------------------------------------------------------------|
| `fovEffect`   | `true`  | Whether the view widens in flight                                                                                                                        |
| `fovIncrease` | `15`    | How much wider, in percent, from 0 to 50; the vanilla FOV Effects slider scales it down, and vanilla caps sprint, flight and this together at 50 % wider |

## Using it in your mod

Add it through [JitPack](https://jitpack.io); the version is a release tag on GitHub (such as `v1.0.0`) or a commit hash:

```groovy
repositories {
    maven {
        url = 'https://jitpack.io'
        content { includeGroup 'com.github.alex-0v0-328' }
    }
}

dependencies {
    implementation 'com.github.alex-0v0-328:mcmod-burst-flight:<version>'
}
```

Declare the dependency in `neoforge.mods.toml`:

```toml
[[dependencies.your_mod_id]]
    modId="burst_flight"
    type="required"
    versionRange="[1.0.0,)"
    ordering="NONE"
    side="BOTH"
```

The API is `net.alex.burstflight.api.BFApi`, called on the server only, with a `ServerPlayer`:

| Method                      | Effect                                                                |
|-----------------------------|-----------------------------------------------------------------------|
| `allow(player)`             | Lets the player use it at the server config's multiple (3 by default) |
| `allow(player, multiplier)` | Lets the player use it at the given multiple (clamped to 1–8)         |
| `deny(player)`              | Forbids it for the player; a flight in progress ends at once          |
| `reset(player)`             | Drops the decision above and hands the player back to the config      |
| `getMultiplier(player)`     | The multiple in force; 0 means the player may not use it              |
| `isBursting(player)`        | Whether the player is in a burst flight                               |

A decision is saved with the player and survives death, so one call is enough; a later call replaces it. A new multiple applies to a flight in progress within one tick.

```java
BFApi.allow(serverPlayer, 5.0);
```

## Requirements

- Minecraft 1.21.1
- NeoForge 21.1.252 or newer

## License

[MIT](LICENSE.txt)
