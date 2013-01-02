#!/bin/bash

ls bin >/dev/null 2>&1 && (
	export CP=libs/runtime/core/commons-cli-1.2.jar:libs/runtime/core/commons-lang3-3.1.jar:libs/runtime/core/je-5.0.58.jar:libs/runtime/shell/jline-2.10.jar:libs/runtime/xmpp/smack.jar:libs/runtime/xmpp/smackx.jar
	java -cp bin:$CP -Xmx50M -Xms50M net.freehal.ui.common.Main "$@"
	exit $?
)

ls bin >/dev/null 2>&1 || (
	ant jars
	java -jar dist/freehal-dist.jar "$@"
	exit $?
)
