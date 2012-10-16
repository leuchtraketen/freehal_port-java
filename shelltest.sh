#!/bin/bash

cd bin
java -cp .:../libs/je-5.0.58.jar net.freehal.ui.shell.ShellTest "$1"
