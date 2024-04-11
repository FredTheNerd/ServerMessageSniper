Made a mod to implement both autogg for advancements and something similar but broader than sk1er's popup events. <br>
Unfortunately haven't implemented data driven rules, but they're based on predicate functions (in PacketReaderHelper.java) if you'd like to try and recompile with custom rules. <br>
### Current Rules
+ Ignore tell command suggestions
+ Insert "gg" into advancement messages
+ Automatically run click events in advancement messages that do NOT contain own name
