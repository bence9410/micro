gnome-terminal --maximize \
--tab -e "bash -c 'cd gateway/; mvn clean spring-boot:run; exec bash'" \
--tab -e "bash -c 'cd oauth2/; mvn clean spring-boot:run; exec bash'" \
--tab -e "bash -c 'cd welcome/; mvn clean spring-boot:run; exec bash'" \
--tab -e "bash -c 'cd amusement-park-ui/; mvn clean spring-boot:run; exec bash'" \
--tab -e "bash -c 'cd zoo-ui/; mvn clean spring-boot:run; exec bash'" \
--tab -e "bash -c 'cd amusement-park-micro/; mvn clean spring-boot:run; exec bash'" \
--tab -e "bash -c 'cd zoo/; mvn clean spring-boot:run; exec bash'"
