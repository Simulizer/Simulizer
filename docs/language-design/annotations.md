Annotation Design
=================

Annotations will be written inside comments. They can have text before and after them, i.e. `# <prefix><annotation><suffix>`.

Annotations will specify actions for the high-level visualisation, i.e. the visualisation of data structures, such as lists, graphs, etc. The verbs describing annotations will include:
- `watch` - For example, if you want to display the value of a register, you would specify the register in the `watch` command.
- `animate` - If you want to specify a particular animation to be shown, e.g. swapping to values in a list, you can use the `animate` command.

Style
-----
The annotations will be similar to the `LaTeX` style. Most commands will have the form
```
@<verb>[<option>*]{<register>|<variable>}
```

The only command that is slightly different is the `declare` command, which uses a C-style declaration between the curly braces: `@declare[type=<type>]{<name> = <value>}`

To cancel a particular command, use `@!` as the prefix, e.g. `@!watch{$v0}`. If you want to cancel the last command, you can use `@stop{}`.

Command Usage
-------------
### `watch`
Usage: `@watch[showlabel=(true|false)]{<register>|<variable>}`
- `<register>` or `<variable>` is the name of the register/variable to watch.
- `showlabel` indicates whether or not the name of the register/variable will be shown. By default `showlabel=true`.

Example: `@watch[showlabel=false]{mygraph}`

### `animatelistswap`
Usage: `@animatelistswap[index1=<int>,index2=<int>]{<variable>}`
- `index1` and `index2` specify the indices of the list that are involved in the swap. If the algorithm is not actually swapping elements these elements, then the animation will still run, but immediately after the animation is finished, the list will be displayed in the correct order.
- `<variable>` is the identifier for the list.

Example: `@animatelistswap[index1=3,index2=4]{mylist}`

### `declare`
Usage: `@declare[type=(list|graph)]{<name> = <value>}`
- `type` specifies the type of the variable as either a list or a graph.
- `<name>` declares the name of the variable.
- `<value>` specifies the value that will be bound to `<name>`. Graphs can be declared using the `\graph{...}` environment, using [Dot](http://www.graphviz.org/) syntax.

Example: `@declare[type=list]{mylist = [1, 2, 3]}`

Example: 
```
@declare[type=graph]{
  mygraph = \graph{
    A -> B;
    A -> C [label = "5"];
    B -> C;
  } 
}
```


