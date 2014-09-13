# DOCKER-VERSION 1.2.0

FROM    centos:centos6
maintainer Abdullahi Egal &lt;abdu.egal@gmail.com&gt;

run cd ~

# Install prerequisites
run apt-get update
run apt-get install -y software-properties-common

# Install java8
run add-apt-repository -y ppa:webupd8team/java
run apt-get update
run echo oracle-java8-installer shared/accepted-oracle-license-v1-1 select true | sudo /usr/bin/debconf-set-selections
run apt-get install -y oracle-java8-installer

# Install tools
run apt-get install -y git maven

# Clone project
run git clone https://github.com/abduegal/Microservice-Admin-Dashboard.git
run git checkout docker

# Build project
run cd Microservice-Admin-Dashboard/dashboard &amp;&amp; mvn package
run cd ~
run cd Microservice-Admin-Dashboard/example &amp;&amp; mvn package

# Run both projects
run cd ~/Microservice-Admin-Dashboard
run nohup java -jar dashboard/target/dashboard-0.8.jar server dashboard/config.yml

run cd ~/Microservice-Admin-Dashboard
run nohup java -jar example/target/example-1.0-SNAPSHOT.jar server example/config.yml

# Expose the http port
expose 8080
