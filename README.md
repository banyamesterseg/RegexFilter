### A minimal, yet full-blown, chat filter solution based on Regular Expressions
This plugin is a remake and extension of [FloydATC's RegexFilter](https://bukkit.org/threads/4961/)
This plugin was originally developed by [HazeDev](https://hazedev.me) of [EQUARE Studios](https://www.spigotmc.org/threads/505189/) according to my specifications

Notice: this plugin does no filtering ON ITS OWN; you'll have to set up the patterns yourself

## Features
  - **Replace** text in chat for **moderation** or for **fun**
  - **Deny** sending messages matching filter
  - **Warn** players about what they just said
  - **Notify** staff (or anyone, really, it's permission-based)
  - **Execute** commands regarding players (eg. autokick)
  - **Exempt** staff from filters (again, permissions)
  - **PCRE**-compatible engine ([java.util.regex](https://docs.oracle.com/javase/8/docs/api/index.html?java/util/regex/package-summary.html))
  - **YAML** config

## Commands
  - /regexfilter reload: reloads the config, requires the relevant permission (or OP)

## Permissions
  - regexfilter.exempt:    don't apply any filters whatsoever - NB this currently blocks side effects also
  - regexfilter.exempt.<group>:  don't apply specific filters - you can specify permission leaf node names in the config
  - regexfilter.notify.<group>:   alert on specific matches   - you can specify permission leaf node names in the config
  - regexfilter.reload

## Configuration
see included `config.yml` for details and examples

## Upcoming features
  - JSON support
  - More commands, in-game modification of filters
  - Automatic reload of config on save
  - Multiple outcomes
  - Test harness
  - Migration to Gradle
