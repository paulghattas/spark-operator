FROM fabric8/java-centos-openjdk8-jdk:1.4.0
ADD target/oshinko-operator-*.jar /oshinko-operator.jar
CMD ["/usr/bin/java", "-jar", "/oshinko-operator.jar"]