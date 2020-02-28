export CLASSPATH=/hextrato/kral/bin/lib
export KRAL_HOME=/hextrato/kral/bin
#java -jar $KRAL_HOME/bin/kral-1.9.12.jar "$@" 
#java -classpath $KRAL_HOME/bin com.hextrato.kral.console.KConsole
#java -cp /hextrato/kral/bin/*.jar com.hextrato.kral.console.KConsole
#java -cp $KRAL_HOME/kral-1.9.12.jar:$CLASSPATH/*:. com.hextrato.kral.console.KConsole "$@"
java -cp $KRAL_HOME/kral-1.13.jar:$CLASSPATH/*:. com.hextrato.kral.console.KConsole "$@"

