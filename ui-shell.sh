#!/bin/bash

java -cp bin:libs/je-5.0.58.jar -Xmx50M -Xms50M net.freehal.ui.shell.Shell "$1"
