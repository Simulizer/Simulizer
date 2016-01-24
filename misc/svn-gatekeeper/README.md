# SVN Gatekeeper Experiment
These scripts setup an isolated environment to prototype a script which will
act as a one-way gatekeeper for transferring the git commit history to SVN.

Note: these scripts work only on Linux (I imagine) and all the necessary
dependencies must be installed (like svn, git etc)

# Operation
1. run `setup-experiment` which will create the experiment directory and repositories
    - `git_main` is the main git repository that members of the development team would use
    - `svn_server` is the server for svn. It contains the repository
      information, however it is not accessible through this directory because
      of the design of SVN.
    - `svn_repo` is not involved in the experiment, but can be used to view the
      state of the repository served by `svn_server`
    - `git_gatekeeper` is a repository that is connected to both `git_main` and
      `svn_server`. It does not push anything back to `git_main`.
2. run `run-gatekeeper-first-time` to pull the commits from `git_main` and
   `svn_server`, merge them (forcefully) and push the mutilated history to svn,
   while not pushing back to `git_main`
3. run `make-merge-change` to automatically create a complex commit history in `git_main`
4. run `run-gatekeeper` to push the changes made to `git_main` to `svn_server`.
   Note: this may fail
5. run `clear-experiment` to clear all traces of the experiment. You can now go
   to step 1 to run another experiment.

