# wSurvivalGames

### **Introduction**
The purpose of survival games is looting chests and fighting other players to be the last person alive. 
In wSurvivalGames there are several features such as tiered chests, kill effects, and kits.
Most of this is configurable as well.

### **How to set it up**
Setting it up is very easy. Make sure to have a “general spawn point.” 
This is where the player will go after they leave the game or if the game ends. 
You can do that with the [/sg setworldspawn] command.

Creating arenas: This is very simple. All you have to do is execute the command [/sg createarena arenaName]. 
This will create a new world with the appropriate config files. The arena settings include 
lobby countdown, game duration, grace period, minimum players, maximum players, etc. are all configurable.

Setting spawn points for the waiting room lobby and the game is straightforward. 
To create a lobby spawn point, you use the command [/sg setlobby arenaName]. 
To create a game spawn point, you use the command [/sg setspawn arenaName].

You don’t have to worry about special types of chests. 
You can use the regular chests and put them in the arena wherever you’d like. 
The loot appears in the chests only if it hasn’t been opened before, and the arena is currently in a live game.

You can also delete arenas; the command for that is [/sg deletearena arenaName]

### **Kill Effects**
You also have the option to use Kill Effects. A Kill Effect is a potion effect that you get applied after killing a player. A few preset kill effects have been created, such as Strength, Resistance, Speed, Fire Resistance, and Jump Boost. You create new kill effects and modify the names, amplifiers, and durations of the existing ones. 

### **Tiered Chests**
There are two types of chests, a Tier 1 and a Tier 2. A Tier 2 chest has more powerful loot than a Tier 1. You can configure the chances of a Tier 2 chest appearing. All the loot inside the chests is entirely configurable.



### **Kits** 
This plugin also gives you the option to select and create your custom kits! 
Kit items are entirely configurable. You can have up to 27 different kits!

### **Commands**
`/sg createarena <arenaName>` Creates a new arena\
`/sg deletearena <arenaName>` Deletes an existing arena\
`/sg join <arenaName>` Join an arena\
`/sg leave` Leaves an arena\
`/sg spawnnpc` Spawns an NPC you can click to join a game\
`/sg setlobby <arenaName>` Sets the lobby spawn point for an arena\
`/sg setspawn <arenaName>` Sets a game spawn point for an arena\
`/sg setworldspawn` Sets the world spawn, the spawn where players go to after the game ends, or they leave\
`/sg help` Displays the help message\
`/sg admin` Displays the admin message 
 