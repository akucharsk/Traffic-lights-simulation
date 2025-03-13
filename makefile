VERSION ?= 1.1

TARGET=target/traffic-$(VERSION)-SNAPSHOT.jar

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