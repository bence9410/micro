gnome-terminal --maximize \
--tab -e "bash -c 'cd gateway/; mvn clean package dockerfile:build; exec bash'" \
--tab -e "bash -c 'cd oauth2/; mvn clean package dockerfile:build; exec bash'" \
--tab -e "bash -c 'cd welcome/; mvn clean package dockerfile:build; exec bash'" \
--tab -e "bash -c 'cd amusement-park-ui/; mvn clean package dockerfile:build; exec bash'" \
--tab -e "bash -c 'cd zoo-ui/; mvn clean package dockerfile:build; exec bash'" \
--tab -e "bash -c 'cd amusement-park/; mvn clean package dockerfile:build; exec bash'" \
--tab -e "bash -c 'cd zoo/; mvn clean package dockerfile:build; exec bash'" \
--tab -e "bash -c 'cd database/; docker build -t benike/database-oracle-xe-11g .; exec bash'"
