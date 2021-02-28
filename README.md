# moloch
Forge 1.16 Moloch Mod

I (@ramou) have removed the admin interface and now you can interact with your moloch block via commandline only. All times are in miliseconds from the epoch, so use `1614449380570lL` notation for starts and ends. `interval` is small enough that you can just put an unmarked number, but `variance` should use the `L` notation as the desired standard deviation will be the square of the variance... why don't I just ask for stdev? Maybe I should, someone post an issue (I spend too much time with stochastic anlysis).

## Moloch
Each moloch block represents an aspect of the angry all-consuming Moloch! Moloch progresses through sequences of desires, which when met result in rewards for the affected folks, and when not met, result in punishment. As such, Moloch can be treated as a sequence of progressions that start and end.

In each progression, there is a list of desired items with a desired amount associated with each. During a progression, players may move items into the single slot container of the moloch block, where, if they are desired items, they will be gradually consumed. If all desired items in a progression are consumed before it ends, moloch will bestow the specified rewards. However, if the end of the progression is reached without all desires being met, punishments will be meted out instead!

 * You can inspect your moloch with `/data get block <targetPos>`
Keep in mind that since this is the vanilla data command, `<targetPos>` can either be x, y, z coodinates or any of the usual target selectors. Just make sure you've got a moloch block to edit before you start messing around or it will be very boring for you.

 * You can update the name of your moloch with `/data modify block <targetPos> molochName set value "<newName>"`

### Progressions
 * Building a full progression is a bit long for one command, so you may want to break it up and start with a blank progression: 
 `/data modify block <targetPos> progressions prepend value {id: <id>, start: <start>, end: <end>, active: 0b}`
 * You can prepend multiple progressions and check all of them:
 `/data get block <targetPos> progressions` or each of them by index:
 `/data get block <targetPos> progressions[<i>]` where `<i>` is the 0-based index of the progression you want to inspect:
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
 The `<id>` should be a unique long, but for now it isn't checked. Future versions will use it as a shorthand for editing things. `<start>` and `<end>` are in miliseconds from the epoch (you can look some place like https://currentmillis.com/ to do quick conversion), with the progression not becoming active until `<start>` (hopefully that's working) and Moloch not activating punishments until `<end>` passes with unfulfilled desires. If omitted, `<start>` will default to the current time. If `<end>` is omitted, it will default to a week after start. Note that we start `<active>` as false because we do not want this progression to get seen or used by players until it is ready, and accordingly if omitted `<active>` will default to `0b` (`false`). 
 
For now, the `desires`, `rewards` and `punishments` act as placeholder, defaulting to empty lists when omitted. We'll look at filling these in a little further below.

 * You can remove progressions by index as well:
 `/data remove block <targetPos> progressions[<i>]`
 * Lastly, you can update specific fields of a progression:
 `/data modify block <targetPos> progressions[<i>].active set value 1b` (which sets that progression as active... but don't do that till you've set up the desires and rewards/punishments first!)

### Desires
Desires are the way the things moloch wants are shown.
 * To add desires to the new progression, you can do something lie:
`/data modify block <targetPos> progressions[<i>].desires prepend value {id: 1, item: "<item>", amountTotal: 4, amountRemaining: 2}`
 * You can prepend multiple desires and check all of them:
 `/data get block <targetPos> progressions[<i>].desires` or each of them by index:
 `/data get block <targetPos> progressions[<i>].desires[<j>]` where `<j>` is the 0-based index of the desire you want to inspect in progression with 0-based index `<i>`:
```json
{
  id: 1L, 
  item: "minecraft:apple", 
  amountTotal: 4, 
  amountRemaining: 2
}
```
As with progressions, the `<id>` should be unique to the desires in this moloch (and progression), but it is not currently checked. The `<item>` should be the id of the item, e.g. `minecraft:apple`, and thus it can support items from modded Minecraft. An invalid bedrock item with a message `err` in red will be shown in the container interface if the item cannot be found (or if you foolishly say that Moloch desires bedrock); note that if this is not corrected, that progression will invariably lead to punishment as mortals may not give Moloch bedrock... 

When active as the current progression, the first three desired items will be visible, along with the `amountRemaining` for each of them. `amountTotal` must be a positive integer and will default to `1` if omitted. `amountRemaining` must similarly be a positive integer, and will default to whatever `amountTotal` is if omitted.

 * You can remove desires withing a progression by index as well:
 `/data remove block <targetPos> progressions[<i>].desires[<j>]`
 * Lastly, you can update specific fields of a desire within a given progression:
 `/data modify block <targetPos> progressions[<i>]..desires[<j>].item set value "minecraft:cake"` (Who doesn't like cake?!?! You'll note that your can ask for a ton of cake, as Moloch is not bound by mortal restrictions on stacks of cake)

### Rewards/Punishments
Rewards and Punishments use <actions>. When all desires are met for a progression, all rewards become active, running a number of times based

