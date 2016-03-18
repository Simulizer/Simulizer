#Visualisation Design#
$\TODO{write section}$

- Low Level
    - $\TODO{Theo write some bullet points here}$

- High Level
    - Annotation model and visualisation separate

    - Visualisation changes are queued with a copy of the model stored and the change made. This can fall behind simulation because why slow down the clock for the visuals. There are some catch up methods including:

        - Speed up animations (like swapping)

        - Skipping some animations because it is going to fast to tell (future feature)

    - High Level Visualisation window can contain more than one visualisation.

    - The model is always up to date for meaningful error messages.

    - If the High Level Visualisation window is opened midway through a simulation, it can render the visuals for the current execution.
