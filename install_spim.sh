#!/usr/bin/env bash
sudo apt-get install -y subversion bison g++ flex
svn checkout svn://svn.code.sf.net/p/spimsimulator/code/ spimsimulator
cd spimsimulator/spim
make
sudo make install