# Getting Started

So you want to learn how to create your own Spaces? Well then this is the guide for you!

## Creating a Space

To begin, make sure you are in the Lobby. If not you can go there using `/lobby` (or `/spawn`).
Once you are in the Lobby, look for this item in your hotbar and right click it.

![image](https://i.imgur.com/l2p82cp.png)

You will be greeted with a menu showing you a list of all your current Spaces, and in the bottom right this item to create a new space

![image](https://i.imgur.com/WCjbQHT.png)

> **Note:** Not seeing a Create Space item?
> This means you can not create a new Space. Common reasons are:
> - You already own the maximum amount of Spaces allowed by the server.
> - The server does not have any Spaces available for anyone.
> - Space Creation could be disabled.
>
> If this is the case, will have to re-use an existing space, look for another server, or host your own.

## Joining a Space

Once you have a Space, click it in the My Spaces menu and you will be sent to that space and put into "play" mode. You won't be able to do a lot, because your Space does not come with any logic. To fix that, type `/code` (or `/dev`). To return to play mode later, you can use `/play`.

## Placing your first Node

Make sure you are in "code" mode, and then press swap hands while looking at the wall. You will see a menu similar to this, the Category Menu

![image](https://i.imgur.com/wiJJUt0.png)

In the menu, right click `Events` and then `On Player Join`.

![image](https://i.imgur.com/6kdZxAy.png)

This is a "Node", every node is a building block that usually does a single thing. By combining nodes we can create custom logic.
In this case, we have an `On Player Join` node, which has 2 outputs (right side) and no inputs (left side, nothing).
The `Signal` output will be used for specifying what to do when a player joins your space, and the `Player` output will be the player that joined.

You can try going into `/play` now, but nothing will happen.
Why? For two reasons:
1. After any change to your logic, you need to `/reload` for the changes to take effect.
2. More importantly, we did not specify anything to do once the player joins.

## Another Node
If you didn't skip to here, but followed the step above, you already have an `On Player Join` node.
For this example, we will send the player a message when they join. To do so, begin by swapping hands again (while looking at an empty point on the wall).
The Category Menu will appear again. This time you will want to select `Actions` and then `Send Message`

![image](https://i.imgur.com/s42eeEN.png)

This node looks a bit different, try to guess what the 3 inputs (signal, player, message) and 1 output (next) do.
Here is a summary:
The `Signal` input wants, well a signal as an input (when to send the message)
The `Player` input is who to send the message to
The `Message` input is the message we want to send.
And finally, the `Next` output is for what to do after the message was sent.

Under the assumption, that you did not place the new node next to the old one, like this you can move it by right clicking its background to start dragging, and another right click to stop.

![image](https://i.imgur.com/XSqMBbF.png)

## Specifying an Input Value
If you now put your crosshair over the `Message` input of the `Send Message` Node and type in chat, as if you where talking, you see the node change.

![image](https://i.imgur.com/uloJN0P.png)

This will be the message that is sent to the player. If you want to remove it, you can left click the text.
If you left click a part of the node, you will remove it completely and have to re-create it.
You can also type in chat while a value is already set to update it.

## Making it work
If you tried to `/reload` and then `/play`, you will have noticed nothing happened. This is for one main reason: The inputs and outputs aren't connected.
That means that the `On Player Join` node did notice you join, but as the signal output is not connected to anything, it does not know what to do.
To fix this, right click the `Signal` in the `On Player Join`. It will start creating a wire

![image](https://i.imgur.com/hYM4OBN.png)

While creating the wire, you can left click to abort, or right click while looking at an input with the same type to connect them. In this case, the `Signal` on the `Send Message`.
Once you have that, do the same for the player out- and input and you will get this.

![image](https://i.imgur.com/FW0r4El.png)

Now you can `/reload` and `/play` and you will be greeted with your message.

## A second message
This is rather simple, create another send message node, and move it to the right. Set a message again, and connect them as shown:

![image](https://i.imgur.com/qbmYPsQ.png)

For the player wire, you can press swap hands while looking at the old one, to create a wire splitting of it, referencing the same player. In this case its not too useful, but generally that can be done to avoid having to re-create a long wire.
To test its the same as always: `/reload` then `/play`.

## Have Fun!
There obviously is more you can do but thats beyond the scope of this. So go ahead, explore the current nodes, and see what you can make.