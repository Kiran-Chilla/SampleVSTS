Double click Server.jar once and Client.jar 4 times to run the game as i submitted executable jars.

Clients wont run without starting Server first.


Compiling programs:

compiling java files through cmd:
set java path and then
javac *.java

compiling java files in jar through cmd:
javac -cp "path to jarServer.jar" *.java
javac -cp "path to jar\Client.jar" *.java

Executing programs:

I submitted runnable jar files. 
Double click Server.jar. Server starts running on port.
Double click Client.jar. It will open window with GUI for user and it should be pretty clear from there.
Open 4 clients i.e. double click Client.jar 4 times to continue the play.

(or)

open 5-cmd's:
go to file path and then
type
java Server
in one cmd and
java Client
in other four cmd's

(or)
open 5-cmd's:  
go to file path of jar and then
type
java -jar Server.jar
in one cmd and
java -jar Client.jar
in other 4 cmd's

Double clicking Server.jar once and Client.jar 4 times is easier option.
