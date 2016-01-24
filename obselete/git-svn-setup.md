Git SVN
=======

Setup
-----
```
sudo apt-get update
sudo apt-get install git-svn # Installation
mkdir A4 && cd A4            # Make the git directory
git svn init "https://codex.cs.bham.ac.uk/svn/team-project/A4/" # Setup git-svn folder
git svn fetch -r1            # Fetch the latest revision
git svn rebase               # Update working directory
```

Now add the GitHub remote: `git remote add origin git@github.com:ToastNumber/A4.git` or `git remote add origin https://github.com/ToastNumber/A4.git`

Usage
-----
Work with Git as normal and only push to the svn server when you need to and once all `git commit`s are done.

- `git svn rebase` updates the git repository from the svn repository.
- `git svn dcommit` pushes all git commits to the svn repository.
