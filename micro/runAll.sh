gnome-terminal --maximize \
--tab -e "bash -c 'java -jar config/target/*.jar; exec bash'"
sleep 10
gnome-terminal --maximize \
--tab -e "bash -c 'java -jar gateway/target/*.jar; exec bash'" \
--tab -e "bash -c 'java -jar oauth2/target/*.jar; exec bash'" \
--tab -e "bash -c 'java -jar discovery/target/*.jar; exec bash'" \
--tab -e "bash -c 'java -jar amusement-park/target/*.jar; exec bash'" \
--tab -e "bash -c 'java -jar zoo/target/*.jar; exec bash'"  \
--tab -e "bash -c 'java -jar visitor/target/*.jar; exec bash'" \
--tab -e "bash -c 'java -Dserver.port=33333 -jar visitor/target/*.jar; exec bash'" \
--tab -e "bash -c 'docker stack deploy -c docker-compose.yml services; exec bash'"
