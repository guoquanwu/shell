#!/bin/bash

if [[ $# -eq 3 ]]; then
  echo 'please type 2 file : sort.sh file1 file2'
  exit 1
fi

file1=$1
file2=$2

sort ${file1} > .file1
sort ${file2} > .file2

comm .file1 .file2
#comm -1 -2 .file1 .file2
