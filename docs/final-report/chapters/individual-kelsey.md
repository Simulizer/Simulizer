##Kelsey McKenna##
<!--
Individual summary of the key contributions
and reflection (role in the team,
personal experience with team project)

Early:
- Suggesting Slack
- Setting up GitHub repository, google drive, etc.
- Contacting module tutors to confirm that use of external libraries okay
- Contributing to specification (functional requirements)
- Researched Maven, Gradle, and ANTLR; wrote tutorials for these
- CircleCI
- Sequence diagrams, UML, Use cases

Middle:
- Prototype syntax and error highlighting for code editor
- List visualisation, tower of hanoi
- General debugging, e.g. making sure tooltips show on the on the correct screen; code editor;
- Pipeline view
- Splash screen
- Resizing high level visualisations
- Memory view
- Diligent with bug reporting and fixing
- Script to push to svn
- Visualisations

End:
- Creating tests (functional + end-to-end)
- Markdown $\to$ LaTeX framework for final report
- Daily plan for last two weeks
- Converting the high-level visualisations to canvas
- Teamwork section of final report
- Writing parts of the presentation
- Writing about my contribution in the user guide
-->
My main role in our project has been to develop the high-level and low-level visualisations, which complement the key visualisation -- the CPU. However, over the course of the project I have also been in charge of the code editor (and its syntax and error highlighting), researching and reporting on key aspects of our software configuration, e.g. Gradle, UI aspects, setting up a framework for developing the final report, among other things.

Since I am the only member of the team not studying the Computer Systems &amp; Architecture module, it made sense for me to focus on parts of the design and implementation that were not heavily based around the low-level architecture, so I have been responsible for more of the visualisation side.

Before starting the project, I was slightly worried about not having the same level of knowledge in CPU architecture as my team members, who all study the module. I even considered trying to catch up on the module content, but after we started assigning tasks to each other, I soon realised that not having detailed knowledge of CPUs was not going to affect the amount I would contribute.

<!--Right after our team was announced in the lecture, I suggested to everyone that we could use Slack as our communication channel, as I had done some research on it previously. Slack has turned out to be our main communication channel, which I integrated with my GitHub account so that we would receive notifications about pushes.-->

Before we started programming, I was in charge of researching build automation tools, e.g. Maven and Gradle, and deciding which one(s) we should use. After doing some research I decided on Gradle, as it was more appropriate for our purposes, and there was a lot of helpful documentation and various plug-ins. As well as this, we needed a parser to work with our custom assembly language, which includes annotations. I looked into the parsing tool [ANTLR](http://www.antlr.org) and wrote a prototype of the language we would be using (a reduced version of MIPS assembly).

In the design phase, I created various sequence diagrams to help us get a better understanding of how we would operate the system in the high-level, and I wrote a number of use cases to help us extract our requirements. UML class diagrams, and use cases for general usage of the system.

To develop appropriate high-level visualisations, e.g. tower of hanoi, lists etc., I needed to create a number of prototypes in order to test the capabilities of certain features in JavaFX. Getting a prototype for animations took some time, and I made sure to design the high-level visualisations so that they can easily be adapted and extended, and so that they provided a general interface that was easy to connect with the other parts of the system, especially the annotations. Similarly for low-level visualisations, e.g. the pipeline view, I went through a number of iterations of prototypes in order to achieve the final result. Again, the design helped to separate the model and view meaning that once I had completed the visualisation side, it would be very easy to link with the model in the back end.

Throughout the project I took charge of a number of tasks which I believed improved the efficiency of our group. For example, I wrote a script to transfer all our code from git to the svn repository. I also had the idea of setting up a framework where we could write our final report in markdown format, which is very simple, and then automatically convert to $\LaTeX$. For the last 10 days of the project I created a daily plan, with each day detailing exactly what we should have finished by the end of that day. I felt this was important to help us continue at a good pace, and to make sure that we were on track to finish everything on time. I was also diligent in fixing and reporting bugs, which meant that we could keep track of the stability of the program.

Overall, I feel I have learned a lot about working in a team, about tools and strategies for developing software projects, and I have improved my software engineering abilities. I look forward to working on future projects where I can continue to develop these skills.

<!--Later on in the project, when we had a number of components working together, we found that the animations were quite sluggish. I wanted to make sure the animations were as smooth as possible, so this meant converting my code to use the `Canvas` component of the JavaFX API.-->

<!--Throughout the project I have contributed to the documentation and organised the structure of these documents.-->



<!-- -------->
