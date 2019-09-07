mkdir -p target/CoffeeMaker_Web/WEB-INF/classes/
javac src/main/java/edu/ncsu/csc326/coffeemaker/exceptions/*.java
javac -classpath src/main/java/ src/main/java/edu/ncsu/csc326/coffeemaker/*.java
mkdir -p target/CoffeeMaker_Web/WEB-INF/classes/edu/ncsu/csc326/coffeemaker/exceptions
cp src/main/java/edu/ncsu/csc326/coffeemaker/*.java target/CoffeeMaker_Web/WEB-INF/classes/edu/ncsu/csc326/coffeemaker/
cp src/main/java/edu/ncsu/csc326/coffeemaker/exceptions/*.java target/CoffeeMaker_Web/WEB-INF/classes/edu/ncsu/csc326/coffeemaker/exceptions/
cp -r src/main/webapp/*.jsp target/CoffeeMaker_Web/
cp src/main/webapp/WEB-INF/web.xml target/CoffeeMaker_Web/WEB-INF/
cd target/CoffeeMaker_Web/
jar -cvf ../CoffeeMaker_Web.war *
