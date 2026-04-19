# Advancement Disable

A simple mod which can remove all advancements in a namespace.

## Usage

The mod removes all advancements in the namespaces configured in the config.

```toml
disabledMods = [
    "minecraft", # remove all minecraft advancements
    "mine.*", # remove advancements from all mods starting with mine
    ".*" # remove all advancements
]
```

## Dependencies

Bundles [night-config](https://github.com/TheElectronWill/night-config)
