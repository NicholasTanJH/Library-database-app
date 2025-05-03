JAVAC=javac
JAVA=java
CP=.:sqlite-jdbc-3.39.3.0.jar
SRC=src
BIN=bin

# Create bin directory if it doesn't exist
$(BIN):
	mkdir -p $(BIN)

# Compile Java files
build: $(BIN)
	$(JAVAC) -cp $(CP) -d $(BIN) $(SRC)/MyApp.java $(SRC)/MyApp.java

# Run the application
run: build
	$(JAVA) -cp $(BIN):$(CP) MyApp

# Clean compiled files
clean:
	rm -rf $(BIN)/*.class