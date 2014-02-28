mvn clean package
cp target/mips.war ~/dev/apache-tomcat-7.0.50/webapps/
cd ~/dev/apache-tomcat-7.0.50/webapps
rm -vrf mips
cd ~/dev/apache-tomcat-7.0.50/bin
./startup.sh &
cd ~/dev/source/dlsu/mips
