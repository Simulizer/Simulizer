## How to create a new visualisation ##
Simulizer's visualisations can be easily extended because of the underlying design. The existing visualisations, e.g. tower of hanoi, are completely independent of the simulation and any other visualisations. In this section, we will describe how a new visualisation can easily be added to Simulizer.

In the steps below, we will describe how a potential graph visualisation could be added. The following classes should be written by the developer:
- `GraphModel`
- `GraphVisualiser`

### Writing the model ###
The first step in creating our graph visualisation is creating the model. This should be a subclass of `DataStructureModel`. This class operates as the model in a typical MVC design pattern. For a graph, we might want to model the nodes, the edges, the weights etc.

Any methods that need to be called by the annotations should be public.

For example, we could create a simple model with a single method for the annotations:

```java
public void completeGraph(Integer... nodes) {
  // generate the graph
  setChanged();
  notifyObservers();
}
```

which would do the required computations to generate a completed graph whose labels are given by the values of `nodes`, and will also inform the view that a change has taken palce.

In order to access the information in the model, we should have appropriate get methods. In this example, we might want a method like this:

```java
public MyGraph getGraph() {
  synchronized (graph) {
    return this.graph;
  }
}
```

Note the `synchronized` keyword to prevent thread interference and memory consistency errors.

### Writing the visualisation ###
The visualiser class should be a subclass of `DataStructureVisualiser`. In our example, `GraphVisualiser` should have a constructor like this:

```java
public GraphVisualiser(GraphModel model, HighLevelVisualisation vis) {
  super(model, vis);
  ...
}
```

If, for example, you want to visualise the graph using the [`Canvas`](https://docs.oracle.com/javase/8/javafx/api/javafx/scene/canvas/Canvas.html) component from the JavaFX API, then you could add the line

```java
getChildren().add(canvas);
```

to the constructor and then use the canvas in visualisations.

The `DataStructureVisualiser` class handles the animation timer, receiving messages, etc. We need to implement the `processChange` and  `repaint` methods. The `processChange` method should contain any logic, if any, for updating parameters for the view. For example, if animations are required, an animation timeline could be instantiated. You should call `setUpdatePaused(true)` at the end of the `processChange` method, and `setUpdatePaused(false)` should be called after any animations finish. The `repaint` method should repaint/resize the components in the window.

By implementing the `getName` method you can give your visualisation a name, e.g.

```java
@Override
public String getName() {
  return "Graph";
}
```

### Linking it to the system ###
Now we need to link our model and visualiser to the rest of the system so that they can be accessed via annotations. We need to modify the following classes:

1. `ModelType`: we need to add a constant to the enum, e.g.
```java
public enum ModelType {
  HANOI, LIST, FRAME, GRAPH;
}
```
2. `HighLevelVisualisation`: we need to add our `GRAPH` enum as a case in the switch statement in the `addNewVisualisation` method:
```java
switch (model.modelType()) {
  ...
  case GRAPH:
    vis = new GraphVisualiser((GraphModel) model, this);
    break;
  ...
}
```
3. `HLVisualManager`: we need to allow the user to load the visualisation from their annotations using a user-friendly name, e.g. "graph" in the `create` method:
```java
switch (visualiser) {
    ...
    case "graph":
      model = new GraphModel(io);
      break;
}
```

### Usage ###
Now the visualisation can be called via annotations in the user's code! To load the graph visualisation, use

```javascript
# @{ var g = vis.load('graph') }@
```

More details on how to use visualisations can be found in the user guide.
