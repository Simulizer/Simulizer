# End to End #
<!-- Reading resource: http://www.guru99.com/end-to-end-testing.html -->

Our prospective user base is quite large (all students on the Computer Systems &amp; Architecture module), and the ability use the system as a whole and access each feature is critical. Therefore, we decided that end to end tests were necessary for our system.

The main components of the system that will be tested are:

- Editor window
- Program IO window
- CPU visualisation window
- High level visualisation window
- Labels window
- Pipeline window
- Memory window
- Registers window
- General window functionality
- Layouts
- Error dialogs
- Configuration/options

Each of these components will be tested in various ways. Firstly, tests will be carried out to ensure that the component works independently of other components (if appropriate). For example, the various features of the code editor, e.g. syntax highlighting, code folding, scrolling, cutting/pasting, etc. can be tested without any other feature of the system. Next, the interconnectivity of components will be tested. For example, clicking on a label in the `Labels` window should bring the cursor in the editor to the corresponding line.
