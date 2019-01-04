#!/bin/sh

echo "CATALINA_HOME = " $CATALINA_HOME

mkdir -p /run/conf

if [ -s /run/secrets/*.context.xml ];
  then
    # Symlink context.xml.
    mkdir -p $CATALINA_HOME/conf/Catalina/localhost
    ln -sf /run/secrets/*.context.xml $CATALINA_HOME/conf/Catalina/localhost/ROOT.xml
    echo "FOUND context.xml."
  else
    echo "context.xml NOT FOUND, proceeding with default config"
fi

if [ -s /run/secrets/*.spring.properties ];
  then
    # Symlink spring.properties.
    ln -sf /run/secrets/*.spring.properties /run/conf/spring.properties
    echo "FOUND spring.properties."
  else
    echo "spring.properties NOT FOUND, proceeding with default config"
fi

if [ -s /run/secrets/*.setenv.sh ];
  then
    # Symlink setenv.sh.
    ln -sf /run/secrets/*.setenv.sh $CATALINA_HOME/bin/setenv.sh
    echo "FOUND setenv.sh."
  else
    echo "setenv.sh NOT FOUND, proceeding with default config"
fi

if [ -s /run/secrets/*.logging.properties ];
  then
    ln -sf /run/secrets/*.logging.properties /run/conf/logging.properties
    echo "FOUND logging.properties."
  else
    echo "logging.properties NOT FOUND, proceeding with default config"
fi

# Running Catalina
echo "Starting Catalina:"
${SERVER_HOME}/bin/catalina.sh run
