@echo off
set command=mvn -DskipTests clean compile package
%command%
