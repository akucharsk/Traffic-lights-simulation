VERSION ?= 1.1

TARGET=target/traffic-$(VERSION)-SNAPSHOT.jar
WEB_APP='src.main.python.web:app'

.PHONY: all clean

all: compile package run

web: compile package
	java -jar $(TARGET)

compile:
	@mvn compile

package:
	@mvn package

run:
	java -jar $(TARGET)

clean:
	rm -rf target