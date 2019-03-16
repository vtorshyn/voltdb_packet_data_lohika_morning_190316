jarname=collector-sp.jar
voltdbroot=/home/vt/ports/voltdb/voltdb/voltdb

all: clean
	mkdir -p obj && javac -classpath "./:$(voltdbroot)/*" -d ./obj  src/main/java/com/vtorshyn/voltdb/collector/*.java
	jar  cvf $(jarname) -C obj .

clean:
	rm -rf ./obj