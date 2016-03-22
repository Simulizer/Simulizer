# Teamwork #
<!-- Get some references -->

We implemented a number of strategies and tools in order to help us work effectively as a team. Even when we all met for the first time, we were discussing how we would communicate, e.g. through email, social media, etc. Since we had similar goals for achieving the best piece of software we possibly could, it meant our working styles were very compatible. Everyone regularly checked to see if there were new messages or new tasks assigned to them, which allowed for open discussions and swift development. Furthermore, our dedication to the project meant that we were eager to implement the system and resolve any issues.

Shortly after meeting each other, we decided on a weekly meeting time that suited everyone. Every week since the start of the project, we had a team meeting lasting an hour where we discussed our plans for the future of the project, any issues to be resolved etc. One of the first things we decided in these meetings was to follow an agile development methodology. This meant that in future meetings we planned the tasks for the next sprint, and we could review any outstanding tasks/issues from the last sprint.

To achieve our full potential, we wanted to play to the strengths of our team members, and conversely avoid assigning tasks to members who would not be comfortable with that task. For example, Charlie (?) had never used JavaFX before, so it was clear that assigning him heavy visualisation tasks using JavaFX would not be the best strategy. Similarly, Kelsey (?) does not do the Computer Systems & Architecture module, meaning that he would be more suited to the visualisation side and more general design tasks rather than the low-level of a CPU.

In order to keep track of the tasks for each sprint, and any issues in the project, we used a tool called [Taiga](https://tree.taiga.io), which is specifically suited to agile projects. Taiga allowed us to create a backlog of tasks to be completed and then assign them to certain team members and place them in sprints. Taiga helped us to stay organised as we could always see which tasks needed to be completed, which issues had not been resolved, and generally helped us keep track of our progress.

We realised immediately that we would need some form of instant messaging for our communication outside meetings, so we wanted to find something that would be well suited to teamwork activities. We decided on [Slack](https://slack.com), which has many features particularly useful for software development. By using Slack, different team members could communicate in different channels about different issues. We were also able to integrate Taiga and our GitHub repository with Taiga. This notified us when a commit was made and when a task was changed on Taiga. It was easy to communicate with everyone on Slack, both because the software was useful, but also because everyone would keep Slack open at home, and messages wouldn't go unread or unanswered.

<!-- This renders fine in if converting to HTML
<img src="segments/slack.png" alt="slack" style="width:40%">
-->

![Slack](segments/slack.png){width=80%}

Occasionally we would use Skype if we needed to talk about something in more depth that we didn't get enough time to discuss during our meetings.

Early in the project, we invested some time into investigating tools that would improve our productivity and efficiency in the future. For example, we looked into using build automation tools like [Maven](https://maven.apache.org). We believed this would be important because we would be working with various dependencies, IDEs, and operating systems. Once we decided to use [Gradle](http://gradle.org), we configured it to work with our various development environments, and it allowed each member to specify required dependencies in a single file, which would be available to everyone through GitHub, and then everyone was synced with the same setup. Investing some time into this research has caused future development to run very smoothly and has allowed us to avoid potential problems with dependencies and configurations.

As a more general plan project management tool, we used [Wrike](https://www.wrike.com) as an idealistic model of how our project would develop. By being able to view the entire span of the project in a single place, it meant we could more selectively choose tasks for our sprints to ensure that we would meet these goals. For example, for the prototype presentation, we knew we had to focus more on visualisation so that we would have something to show for our work, even though a lot had been done in the backend.

![Wrike](segments/wrike.png){width=80%}

Our approach of democratically making decisions was consciously chosen shortly after our team's formation. We agreed that if there were any minor disputes about a feature of the system then the team member 'in charge' of that feature would have a larger say in the final decision.

Our democratic decision-making allowed us to avoid conflicts within the team. For example, if team members were passionate about an idea but with a differing opinion, each member was able to present their point of view, and then we could decide on a plan of action as a whole. As mentioned, our team is very dedicated to the project, and these discussions helped to manifest even better ideas and designs. Moreover, each discussion was settled before we moved on; there wasn't an instance where a team member decided to do it 'their way' without prior agreement, which meant everyone was aware of project decisions.

Our meetings were always very lively with lots of ideas and discussions, which meant we never came away from a meeting wondering what we were going to do for our sprint. We made sure to assign tasks to each member before the meeting was over to ensure that everyone was able to contribute in order to achieve our team's full potential.

A feature of our teamwork and communication which has been integral to our development is our branch model using git. We would reserve our master branch for stable builds, and if we needed to work on a bug, feature, or component of the system, a separate branch would be created. This allowed us to work independently on our own branches without ever interfering with anyone else's code. We were still able to work on each other's branches if we wanted to collaborate on a piece of functionality, but the ability to evolve, or accidentally corrupt, the system without interrupting anyone else's progress has been crucial to our success. We then regularly transfer our changes over the svn repository to allow the module coordinators and our tutor to view our code.

We chose git as our version control system for various reasons. All of us were very familiar with git, meaning that we saved time by not relearning everything in svn. In addition, our branching model was achieved quite painlessly by using git. Merges were much easier than they would have been in svn, which allowed us to save valuable time during development. A small amount of research into the comparison of svn and git also helped us make this decision.

Throughout the development of our system we needed a repository for files such as design documents, presentation slides, images, etc. We decided to use a shared Google Drive folder for this rather than our GitHub repository because we wanted to upload binary files and visual-based documents, which otherwise would have been more awkward to view and modify.

By following a collaborative and efficient workflow throughout the project, we were able to stay motivated and focused in order to accomplish our goals.
