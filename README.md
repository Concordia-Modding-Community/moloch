# moloch
Forge 1.16 Moloch Mod

All times are in miliseconds from the epoch, so use `1614449380570lL` notation for starts and ends. `interval` is small enough that you can just put an unmarked number, but `variance` should use the `L` notation as the desired standard deviation will be the square of the variance... why don't I just ask for stdev? Maybe I should, someone post an issue (I spend too much time with stochastic anlysis).

## Moloch
Each moloch block represents an aspect of the angry all-consuming Moloch! Moloch progresses through sequences of desires, which when met result in rewards for the affected folks, and when not met, result in punishment. As such, Moloch can be treated as a sequence of progressions that start and end.

In each progression, there is a list of desired items with a desired amount associated with each. During a progression, players may move items into the single slot container of the moloch block, where, if they are desired items, they will be gradually consumed. If all desired items in a progression are consumed before it ends, moloch will bestow the specified rewards. However, if the end of the progression is reached without all desires being met, punishments will be meted out instead!

 * You can inspect your moloch with `/data get block <targetPos>`
Keep in mind that since this is the vanilla data command, `<targetPos>` can either be x, y, z coodinates or any of the usual target selectors. Just make sure you've got a moloch block to edit before you start messing around or it will be very boring for you.

```
{
	molochName: "Moloch", 
	subjects: [], 
	progressions: [], 
	x: 29, y: 64, z: -12, 
	Items: [], 
	id: "moloch:moloch"
}

```

The basic structure has the `molochName` (defaults to "Moloch") that users will see when they interact with the moloch block. `subjects` lets moloch know which players it should pay speciial attention to. `progressions` are the meat of the moloch mod and are explained in detail below. Since the moloch block is a container, it contains a list of `Items`.

 * You can update the name of your moloch with `/data modify block <targetPos> molochName set value "<newName>"`
 * You can append subjects with `/data modify block <targetPos> subjects append value <NAME|UUID>` where you can user either a username or a uuid. If recognized, you'll see that UUIDs are used to populate the NBT data, and the `<NAME>` is just allowed as a convenient shorthand. It populates out of UsernameCache, so if users aren't there, Moloch will not deign for them to be subject to their whims. You can remove subjects by `<index>` `/data remove block <targetPos> subjects[<index>]`
 * You can change the contents of the moloch block's inventory slot with `/data modify block <targetPos> Items append value {id: "<item>", Slot: 0, Count: <amount>}`. You can empty the slot using `/data remove block <targetPos> Items[0]`
 where `<item>` is any item id and `<amount>` is a stack size, though all this is subject to the regular container restrictions (it'll ignore trying to append with other slots). 
 

### Progressions
 * Building a full progression is a bit long for one command, so you may want to break it up and start with a blank progression: 
 `/data modify block <targetPos> progressions append value {id: <id>, start: <start>, end: <end>, active: 0b}`
 * You can append multiple progressions and check all of them:
 `/data get block <targetPos> progressions` or each of them by index:
 `/data get block <targetPos> progressions[<i>]` where `<i>` is the 0-based index of the progression you want to inspect:
```
{
  punishments: [], 
  desires: [], 
  start: 1614449380570L, 
  active: 0b, 
  end: 1614535740000L, 
  id: 1L, 
  rewards: []
}
```
 The `<id>` should be a unique long, but for now it isn't checked. Future versions will use it as a shorthand for editing things. `<start>` and `<end>` are in miliseconds from the epoch (you can look some place like https://currentmillis.com/ to do quick conversion), with the progression not becoming active until `<start>` (hopefully that's working) and Moloch not activating punishments until `<end>` passes with unfulfilled desires. If omitted, `<start>` will default to the current time. If `<end>` is omitted, it will default to a week after start. Note that we start `<active>` as false because we do not want this progression to get seen or used by players until it is ready, and accordingly if omitted `<active>` will default to `0b` (`false`). 
 
For now, the `desires`, `rewards` and `punishments` act as placeholder, defaulting to empty lists when omitted. We'll look at filling these in a little further below.

 * You can remove progressions by index as well:
 `/data remove block <targetPos> progressions[<i>]`
 * Lastly, you can update specific fields of a progression:
 `/data modify block <targetPos> progressions[<i>].active set value 1b` (which sets that progression as active... but don't do that till you've set up the desires and rewards/punishments first!)

### Desires
Desires are the way the things moloch wants are shown.
 * To add desires to the new progression, you can do something like:
`/data modify block <targetPos> progressions[<i>].desires append value {id: 1, item: "<item>", amountTotal: 4, amountRemaining: 2}`
 * You can append multiple desires and check all of them:
 `/data get block <targetPos> progressions[<i>].desires` or each of them by index:
 `/data get block <targetPos> progressions[<i>].desires[<j>]` where `<j>` is the 0-based index of the desire you want to inspect in progression with 0-based index `<i>`:
```
{
  id: 1L, 
  item: "minecraft:apple", 
  amountTotal: 4, 
  amountRemaining: 2
}
```
As with progressions, the `<id>` should be unique to the desires in this moloch (and progression), but it is not currently checked. The `<item>` should be the id of the item, e.g. `minecraft:apple`, and thus it can support items from modded Minecraft. An `barrier` will be shown in the container interface if the item cannot be found (or if you foolishly say that Moloch desires a barrier item); note that if this is not corrected, that progression will invariably lead to punishment as mortals may not give Moloch barriers... 

When active as the current progression, the first three desired items will be visible, along with the `amountRemaining` for each of them. `amountTotal` must be a positive integer and will default to `1` if omitted. `amountRemaining` must similarly be a positive integer, and will default to whatever `amountTotal` is if omitted.

 * You can remove desires withing a progression by index as well:
 `/data remove block <targetPos> progressions[<i>].desires[<j>]`
 * Lastly, you can update specific fields of a desire within a given progression:
 `/data modify block <targetPos> progressions[<i>].desires[<j>].item set value "minecraft:cake"` (Who doesn't like cake?!?! You'll note that your can ask for a ton of cake, as Moloch is not bound by mortal restrictions on stacks of cake)

### Rewards/Punishments
Rewards and Punishments use <actions>. When all desires are met for a progression, all rewards become active, running a number of times based

 * To add rewards and punishments to the new progression, you can do something like:
`/data modify block <targetPos> progressions[<i>].rewards append value {type: 0, id: <j>, doInitial: true, doCountTotal: 4, doCountRemaining: 3, active: 0b, command: "<command>"}`
or
`/data modify block <targetPos> progressions[<i>].punishments append value {type: 0, id: <j>, doInitial: true, doCountTotal: 4, doCountRemaining: 3, active: 0b, command: "<command>"}`
with the only difference in the above being whether we append to the `rewards` list or the `punishments` list.

 * You can append multiple rewards/punishments and check all of them:
 `/data get block <targetPos> progressions[<i>].rewards` or each of them by index:
 `/data get block <targetPos> progressions[<i>].rewards[<j>]` where `<j>` is the 0-based index of the rewards you want to inspect in progression with 0-based index `<i>`:
```
{
   id: 1L, 
   type: 0, 
   doInitial: 1b, 
   doCountTotal: 1,
   doCountRemaining: 1, 
   lastRun: 0L, 
   variance: 0L, 
   active: 0b, 
   interval: 0, 
   command: "/say REWARD!"
}
```

As with progressions/desires, the `<id>` should be unique to the rewards/punishments in this moloch (and progression), but it is not currently checked. 

`doInitial` indicates whether this action will run immediately when queued, so, for example, a reward would run immediately after the last desired item were consumed in a progression. This is the default behaviour, and can be omitted if this is what one wants for a reward. There are cases where one does not want an immediate action, and in this case one can use `doInitial: 0b`. `doCountTotal` represents how many times the action will occur and must be a positive integer, which will default to `1` if omitted. `doCountRemaining` must similarly be a positive integer, and will default to whatever `doCountTotal` is if omitted. 

If an action is to occur more than once, `interval` specifies the mean time between occurrences, and defaults to `10000` (10 seconds), where variance represents the statistical variance and so 68% of the time the action will run between the `interval` - the square root of the `variance` and the `interval` + the square root of the variance. If the `interval` were 20000 (20 seconds) and the `variance` were 9000000 (strandard deviation of 3000 (3seconds)) then 68% of the time Moloch would wait between 17 and 23 seconds before running the next action, and 29% of the time Moloch would wait between either 14-17 seconds or 23-26 seconds before running the next action, etc.. If `interval` is set to smaller than 1000, it will be reset to 1000.

The `type` represents the specific nature of the action. Currently, only the command action (`type: 0`) is implemented, and it takes a `command` which is exactly the sort of single command you might find in a command block, like the above `command: "/say REWARD!"` which will cause Moloch to speak (using the `molochName` you set above) the word `REWARD!`. I'm taking requests for additional types of action besides a basic command action, since some things are either tricky or downright impossible to do with that.


