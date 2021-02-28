# moloch
Forge 1.16 Moloch Mod

I (@ramou) have removed the admin interface and now you can interact with your moloch block via commandline only. All times are in miliseconds from the epoch, so use `1614449380570lL` notation for starts and ends. `interval` and `variance` are small enough that you can just put numbers.

## Moloch
Each moloch block represents an aspect of the angry all-consuming Moloch! Moloch progresses through sequences of desires, which when met result in rewards for the affected folks, and when not met, result in punishment. As such, Moloch can be treated as a sequence of progressions that start and end.

In each progression, there is a list of desired items with a desired amount associated with each. During a progression, players may move items into the single slot container of the moloch block, where, if they are desired items, they will be gradually consumed. If all desired items in a progression are consumed before it ends, moloch will bestow the specified rewards. However, if the end of the progression is reached without all desires being met, punishments will be meted out instead!

 * You can inspect your moloch with `/data get block <blockPos>`
 * You can update the name of your moloch with `/data modify block <blockPos> molochName set value "<newName>"`

### Progressions
 * Building a full progression is a bit long for one command, so you may want to break it up and start with a blank progression: 
 `/data modify block <blockPos> progressions prepend value {id: <id>, start: <start>, end: <end>, active: 0b, desires: [], rewards: [], punishments: []}`
 The `<id>` should be a unique long, but for now it isn't checked. Future versions will use it as a shorthand for editing things. `<start>` and `<end>` are in miliseconds from the epoch (you can look some place like https://currentmillis.com/ to do quick conversion), with the progression not becoming active until `<start>` (hopefully that's working) and Moloch not activating punishments until `<end>` passes with unfulfilled desires. Note that we start `active` as false because we do not want this progression to get seen or used by players until it is ready.

 * You can prepend multiple progressions and check all of them:
 `/data get block <blockPos> progressions` or each of them by index:
 `/data get block <blockPos> progressions[<i>]` where `<i>` is the 0-based index of the progression you want to inspect:
```json
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
 * You can remove progressions by index as well:
 `/data remove block <blockPos> progressions[<i>]`
 * Lastly, you can update specific fields of a progression:
 `/data modify block <blockPos> progressions[<i>].active set value 1b` (which sets that progression as active... but don't do that till you've set up the desires and rewards/punishments first!)
 


### Desires
 * To add desires to the new
/data modify block <blockPos> progressions[0].desires prepend value {id: 1, item: "<item>", amountTotal: 4, amountRemaining: 2}
```json
{
  id: 1L, 
  item: "minecraft:apple", 
  amountTotal: 4, 
  amountRemaining: 2
}
```
As with progressions, the `<id>` should be unique to the desires in this moloch (and progression), but it is not currently checked. The `<item>` should be the id of the item, e.g. `minecraft:apple`, and thus it can support items from modded Minecraft. When active as the current progression, the first three desired items will be visible, along with the `amountRemaining`. An invalid item will show as an "Air" block and will be consumed (I assume @alexandre-lavoie had a fix for that in the old version, but I'm just inclined to make it a bedrock block). `amountTotal` must be a positive integer and will default to `1` if omitted. `amountRemaining` must similarly be a positive integer, and will default to whatever `amountTotal` is if omitted.

### Rewards/Punishments
