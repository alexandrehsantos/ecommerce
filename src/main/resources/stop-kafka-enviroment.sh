#!/bin/bash
amarelo="\e[33;1m"
kafka_server_location="/home/ale/software/kafka_2.12-2.3.1/bin/kafka-server-stop.sh"
zookeeper_location="/home/ale/software/kafka_2.12-2.3.1/bin/zookeeper-server-stop.sh"
config_path="/home/ale/software/kafka_2.12-2.3.1/config/"


echo $amarelo "Stopping Kafka server 0\e[m" 
sh "$kafka_server_location" "$config_path"server.properties &
echo $?
sleep 2    
echo $amarelo "Stopping Kafka server 1\e[m" 
sh "$kafka_server_location" "$config_path"server1.properties &
echo $?
sleep 2    
echo $amarelo "Stopping Kafka server 2\e[m" 
sh "$kafka_server_location" "$config_path"server2.properties &
echo $?
sleep 2    
echo $amarelo "Stopping Kafka server 3\e[m" 
sh "$kafka_server_location" "$config_path"server3.properties &
echo $?
sleep 2    
echo $amarelo "Stopping Kafka server 4\e[m" 
sh "$kafka_server_location" "$config_path"server4.properties &
echo $?
sleep 2    
echo $amarelo "Stopping Kafka server 5\e[m" 
sh "$kafka_server_location" "$config_path"server5.properties &
echo $?
sleep 2     
echo $amarelo "Stopping zookeeper\e[m" 
sh "$zookeeper_location" "$config_path"zookeeper.properties & 
echo $?
