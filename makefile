VERSION ?= 1.1
ARGS ?=

TARGET=target/traffic-$(VERSION)-SNAPSHOT.jar
WEB_APP='src.main.python.web:app'

.PHONY: all clean

all: compile package run

compile:
	@mvn compile

package:
	@mvn package

run:
	java -jar $(TARGET) $(ARGS)

clean:
	rm -rf target