export CLASSPATH=/hextrato/kral/bin/lib
export KRAL_HOME=/hextrato/kral/bin
java -cp $KRAL_HOME/kral-1.13.jar:$CLASSPATH/*:. com.hextrato.kral.console.KConsole "$@"

