gnome-terminal --maximize \
--tab -e "bash -c 'docker stack deploy -c docker-compose.yml services; exec bash'" \
--tab -e "bash -c 'java -Xmx128M -jar config/target/*.jar; exec bash'";
sleep 40;
gnome-terminal --maximize \
--tab -e "bash -c 'java -Xmx128M -jar discovery/target/*.jar; exec bash'";
sleep 20;
gnome-terminal --maximize \
--tab -e "bash -c 'java -Xmx128M -jar amusement-park/target/*.jar; exec bash'" \
--tab -e "bash -c 'java -Xmx128M -jar zoo/target/*.jar; exec bash'"  \
--tab -e "bash -c 'java -Xmx128M -jar visitor/target/*.jar; exec bash'" \
--tab -e "bash -c 'java -Xmx128M -Dserver.port=33333 -jar visitor/target/*.jar; exec bash'";
sleep 30;
gnome-terminal --maximize \
--tab -e "bash -c 'java -Xmx128M -jar gateway/target/*.jar; exec bash'" \
--tab -e "bash -c 'java -Xmx128M -jar oauth2/target/*.jar; exec bash'";
