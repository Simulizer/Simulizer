##Matthew Broadway##

I have been very fortunate to have worked in a team of highly skilled programmers who share my ambition and appreciation for crazy ideas which has allowed us to surpass all initial expectations and create a genuinely useful tool.

Straight after being assigned to the team I brainstormed many ideas into a presentation. When presented to the team they unanimously liked my favourite idea (the CPU visualisation) the best. I then got in touch with our lecturer for the Computer Systems and Architecture and arranged a meeting to discuss what things we could do to make the project interesting or useful.

The very first thing I did towards the code was research into Gradle and created a small JavaFX prototype application which used the internal window system that we ended up using for the rest of the project.

Early on in the project I was responsible for  writing the grammar for our language and the assembler which takes the AST and does error checking and validation, bundling the assembly into an easy format for the simulation to consume.

To accompany the assembler I created a very extensive unit test suite for checking the assembler against hand calculated results and also some tests which compares the assembler with the behaviour of SPIM.

Another task that fell on me was to figure out how to encode and decode between binary and Java representations of signed and unsigned integer values. This was very challenging because Java only supports signed integers. I created the `DataConverter` class which provides this functionality.

After that work was done (which was the initial bottleneck before the other members could really get started) I worked on the code editor component. Up until this point we had been using an existing component that we were not happy with, and so I did research into using the web-technology based 'Ace editor'. Initial tests were promising so I spent a week or so creating the syntax highlighting and code folding rules (along with all the other editor features) in javascript. I was the only contributor to this section as the other members were working on the other functionality such as the visualisation components.

Next I worked on the annotation system. I built the framework for extracting the annotations from the source code and binding them to statements (this meant adding to the assembler code) and then moved on to the runtime environment and 'bridges' which would control the Java components from the annotations. This included work on embedding highlighting rules for javascript inside our language. I was the only contributor to the annotation component (except for additions to the bridges when new visualisations were created).

Next I worked on the clock for the simulation. This required a separate thread which would notify the simulation thread at regular intervals and hold back its execution if necessary. This component underwent several revisions and is now reliable and efficient. I was the only contributor to this component.

Finally I worked on the message passing system which up until this point had been rudimentary and did all its processing on the simulation thread. To get around this I prototyped several designs revolving around using a thread pool to distribute the work. The hardest part was the synchronisation of the components with the simulation. I created a mechanism for preventing the simulation from advancing to the next cycle before all waiting messages were processed. This move broke many of the visualisations and other listeners. Michael and I spent a few days hunting down all the problems caused by the move.

