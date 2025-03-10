TARGET=target/traffic-1.0-SNAPSHOT.jar

.PHONY: all clean

all: compile package run

compile:
	@mvn compile

package:
	@mvn package

run:
	@java -jar $(TARGET)

clean:
	rm -rf target