gnome-terminal --maximize \
--tab -e "bash -c 'cd config/; mvn clean spring-boot:run; exec bash'"
sleep 10
gnome-terminal --maximize \
--tab -e "bash -c 'cd gateway/; mvn clean spring-boot:run; exec bash'" \
--tab -e "bash -c 'cd oauth2/; mvn clean spring-boot:run; exec bash'" \
--tab -e "bash -c 'cd discovery/; mvn clean spring-boot:run; exec bash'" \
--tab -e "bash -c 'cd welcome/; mvn clean spring-boot:run; exec bash'" \
--tab -e "bash -c 'cd amusement-park-ui/; mvn clean spring-boot:run; exec bash'" \
--tab -e "bash -c 'cd zoo-ui/; mvn clean spring-boot:run; exec bash'" \
--tab -e "bash -c 'cd amusement-park/; mvn clean spring-boot:run; exec bash'" \
--tab -e "bash -c 'cd zoo/; mvn clean spring-boot:run; exec bash'"  \
--tab -e "bash -c 'cd visitor/; mvn clean spring-boot:run; exec bash'" \
--tab -e "bash -c 'cd visitor/; mvn clean spring-boot:run -Drun.jvmArguments='-Dserver.port=33333'; exec bash'" \
--tab -e "bash -c 'docker run -d -p 1521:1521 benike/database-oracle-xe-11g; exec bash'"
