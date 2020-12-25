#!/bin/bash
javac -d ./bin ./src/mua/*.java
java -cp bin/ mua.Main < in > out