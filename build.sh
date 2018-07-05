#!/bin/bash
START_TIME="$(date +%Y%m%d-%H%M%S)"

TAG=$1

# build TaskManager.war
# cd /devcloud/plugins/CodeDEX/code/che/TaskManager-Build-enhanced
COMMIT="$(git rev-parse --short HEAD)"

mvn -DskipTests=true -Dmaven.test.skip  clean install
ls -l target/*.war

# build docker image
# BUILD_SERVER="centos@100.100.153.189" # cm01
PRIVATE_DOCKER_REGISTRY_TEST=100.101.153.246/classroom_task

if awk 'BEGIN {now = strftime("%k%M")+0; exit(now < 300 || now > 500)}'; then
        #IMG_TAG=$(date +%Y%m%d-%H%M%S)
    IMG_TAG=$(date +%Y%m%d-%H0000)
else
        IMG_TAG=$(date +%Y%m%d)
fi
[[ -z $TAG ]] || IMG_TAG=$TAG
IMG_NAME=develop/taskmanager:$IMG_TAG
# DEST=/tmp/$(date +%s%N | sha1sum | head -c7).war

# scp target/*.war $BUILD_SERVER:$DEST
# cat <<EOF | ssh $BUILD_SERVER bash -s
# set -ex

## clean old docker image in host
# OLD_IMAGES="$(docker images | grep icp_cloudide/ck-e | tail -n+5 | awk '{print $3}' | xargs)"
# { [[ -z "$OLD_IMAGES" ]] || docker rmi $OLD_IMAGES; } || true

cp target/*.war target/TaskManager.war

## prepare workdir
# WS=$(mktemp -d)
# cd $WS
cd target
FILE_NAME=TaskManager.war
TAR_FILE_NAME=TaskManager.tar
# mv $DEST $FILE_NAME
FOLDER_NAME=${FILE_NAME%.*}
unzip $FILE_NAME -d $FOLDER_NAME

sudo chown -R root:root $FOLDER_NAME
sudo tar -cf $TAR_FILE_NAME $FOLDER_NAME
sudo chown -R root:root $TAR_FILE_NAME


## build docker image
cat <<EOF_WRAPPER > wrapper.sh
#!/bin/sh
set -ex
# extract war files
cd /usr/local/tomcat/webapps
rm -rf ROOT/ docs/ examples/ host-manager/ manager/ || true
if test -f *.war; then
  ls *.war | sed 's~\.war$~~' | xargs -I{} unzip -o {}.war -d {}
  rm *.war
fi
# overwrite config files
# ! test -d /etc/CloudIDE/config/WM/WEB-INF || \cp -r /etc/CloudIDE/config/WM/WEB-INF/*  WorkspaceManager/WEB-INF/
# ! test -f /etc/CloudIDE/config/WM/hosts || cat /etc/CloudIDE/config/WM/hosts >> /etc/hosts
# ! test -d /etc/CloudIDE/config/WM/Devcloud || \cp -r /etc/CloudIDE/config/WM/Devcloud/*  WorkspaceManager/WEB-INF/classes/
# ! test -d /etc/CloudIDE/config/WM/WM || \cp -r /etc/CloudIDE/config/WM/WM/*  WorkspaceManager/WEB-INF/classes/properties/
#! test -f /etc/CloudIDE/config/WM/log4j.properties || \cp /etc/CloudIDE/config/WM/log4j.properties  SessionManager/WEB-INF/classes/
cat /etc/hosts


# execute original command
cd /usr/local/tomcat/
perl -p -i -e s/CloudIDEManager/TaskManager/g /usr/local/tomcat/conf/server.xml
if test -z "\$@"; then
  exec bin/catalina.sh run
else
  \$@
fi
tail -f /devcloud/log/tomcat/catalina.out
EOF_WRAPPER
chmod +x wrapper.sh

echo "FROM 100.101.153.246/cloudide/tomcat:steadyroot
LABEL START_TIME=\"$START_TIME\" COMMIT=\"$COMMIT\"
ENV TZ Asia/Shanghai
ADD $TAR_FILE_NAME /usr/local/tomcat/webapps/
ADD wrapper.sh /
ENTRYPOINT /wrapper.sh
" > Dockerfile
docker build -t $IMG_NAME .


## push docker image to private registry
#docker tag $IMG_NAME $PRIVATE_DOCKER_REGISTRY_DEV/$IMG_NAME
#docker push $PRIVATE_DOCKER_REGISTRY_DEV/$IMG_NAME
docker tag $IMG_NAME $PRIVATE_DOCKER_REGISTRY_TEST/$IMG_NAME
docker login -u yinpeng -p Need2change 100.101.153.246
docker push $PRIVATE_DOCKER_REGISTRY_TEST/$IMG_NAME


## clean workdir
# sudo rm -rf $WS
#EOF


## result
END_TIME="$(date +%Y%m%d-%H%M%S)"
printf "%s %s %s %s %s\n" "$START_TIME" "$END_TIME" "PRIVATE_DOCKER_REGISTRY_TEST" $PRIVATE_DOCKER_REGISTRY_TEST/$IMG_NAME $COMMIT | tee result.log
