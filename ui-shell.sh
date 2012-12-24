#!/bin/bash

java -cp bin:libs/je-5.0.58.jar:libs/commons-cli-1.2.jar -Xmx50M -Xms50M net.freehal.ui.shell.Shell "$@"
