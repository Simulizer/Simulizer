# High-level Visualisations #
The high-level visualisations in Simulizer allow you to view the output of your algorithms in a more visual way than tediously scanning through the contents of registers, variables, etc. There are several high-level visualisations available in Simulizer, including a list visualisation and Tower of Hanoi. There are features for the list visualisation that allow you to swap and highlight elements, and place markers above them. This could be useful for algorithms such as binary search, where you may want to highlight the current element being inspected, and place `L` and `R` markers over the left and right end points of the current section of the list.

As well as making your programs easier to view and debug, the high-level visualisations also provide a satisfying result when an algorithm is implemented correctly, which will hopefully make writing assembly more fun!

## Tower of Hanoi ##
![Towers of Hanoi](segments/high-level.png)


[Tower of Hanoi](https://en.wikipedia.org/wiki/Tower_of_Hanoi) is a simple game where the player's goal is to move all $n$ discs from the initial peg to another peg by moving one disc at a time without ever placing a larger disc on top of a smaller disc.

To use the Tower of Hanoi visualisation, add the comment
```
# @{ var h = vis.load('tower-of-hanoi') }@
```
to the start of your program. To set the initial number of discs, add `#@{h.setNumDisks(4)}@`, where 4 can be replaced with any positive integer you wish. To show the visualiser window, call `#@{vis.show()}@`. Finally, to indicate that you want to move a disc from peg $i$ to peg $j$, write `#@{ h.move(i,j)}@`.

## List Visualisation ##
![List Visualisation](segments/list.png)


To use the list visualisation, add the comment
```
# @{ var l = vis.load('list') }@
```
to the start of your program. To set the list, write
```
# @{ l.setList(simulation.readUnsignedWordsFromMem(start, end)) }@
```
To show the visualiser, write `#@{vis.show()}@`. To swap elements with indices $i$ and $j$, write `#@{l.swap(i,j)}@`. To emphasise the element as position $i$ write `#@{l.emph(i)}@`. To add a marker over the element at position $i$ write
```
# @{l.setMarker(i,"<label-text>")}@
```
