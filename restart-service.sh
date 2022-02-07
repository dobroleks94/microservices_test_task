#!/bin/bash

while getopts n: flag
do
  case "$flag" in
    n) servicename=${OPTARG} ;;
    *)
      echo "Please, specify service name with flag -n"
      exit 1 ;;
  esac
done

docker-compose stop "$servicename" && docker system prune && docker-compose build "$servicename" && docker-compose up -d "$servicename"