Introduction
============
This document is aimed at displaying the testing procedures carried out for the Simulizer software, created by team A4 for the team project at the University of Birmingham.

Test plan
---------
The plan for the testing of this software is to split it up into different sections such that we can fully and comprehensively test our system with a high level of acceptance at the end of the project. We aim to have independent testing performed by asking some of our prospective users (students of the Computer Systems & Architecture module and the module lecturer) to use our system. As a team, we will try to perform our own testing, but each of us will try not to test a part of the system that we have written to reduce bias. The area of tests carried out will be as follows.

### Unit testing
The unit testing will be carried out as we write the software, with many JUnit tests written for each component of the software (in isolation). The unit tests will focus on the back end implementation, which mainly comprises the simulation and the assembler.

### Integration testing
The integration tests will concern us integrating the different components of our software together, for example, combining the assembler and simulation together and checking that they, together, can be used to correctly execute an assembly language program.

### Comparison testing
*Comparing our software's execution with that of existing software*

Due to the fact that our aim in this project is to make a better, more usable alternative to existing software, we feel it is also a sensible idea to concern various outputs from our software with that of the existing software (Spim simulator in this case). This will tell us whether we have at least reached the same level of quality/accuracy as the current software and, hopefully, tell us where we have made an improvement of some sort.

### Functional testing
To ensure that our system has the functionality that we intended to implement, we will go through each of the functional requirements defined in the software requirements specification and ensure that the system has met the requirement and that the feature is appropriate for our intended audience.

### End-to-end testing
We intend for this to be a usable system that could be used as a teaching/learning resource, therefore we feel it is important to perform end-to-end-tests, which will test the program flow of the system from starting to closing the application. To do this, we will devise various user scenarios/use cases, which will consist of a number of steps that a potential user would perform, and we will test whether the system operates as expected.

### User testing/evaluation
The final area of our testing is user testing/evaluation. We are very fortunate to have a well defined set of users for this project and hence we intend to utilise this by getting opinions on our software and feedback from both a set of students taking the Computer Systems &amp; Architecture module, as well as the module lecturer, Ian Batten. Due to the agile nature of this project, we will able to do this continuously throughout the project, to give the best possible end product to our users.
