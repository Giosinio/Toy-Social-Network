package repository.file;

import model.Entity;
import repository.memory.InMemoryRepository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.List;

/**
 * Abstract class that implements Repository interface
 * @param <ID> - type E must have an attribute of type ID
 * @param <E> - type of entities saved in repository
 */

public abstract class AbstractFileRepository<ID, E extends Entity<ID>> extends InMemoryRepository<ID, E> {
    private final String filename;

    public AbstractFileRepository(String filename){
        this.filename = filename;
        loadData();
    }

    /**
     * private method that loads the entities from file
     */
    private void loadData(){
        try(BufferedReader bufferedReader = new BufferedReader(new FileReader(filename))){
            String line;
            while ((line = bufferedReader.readLine()) != null){
                List<String> attributes = Arrays.asList(line.split(";"));
                E element = extractEntity(attributes);
                super.save(element);
            }
        }
        catch (FileNotFoundException e){
            System.out.println("Your file wasn't found!");
        }
        catch (IOException e){
            System.out.println("Reading error!\n");
        }
    }

    /**
     * private method that saves all the entities into file
     */
    private void writeData(){
        try(BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(filename))){
            for (E entity : entities.values()){
                bufferedWriter.write(createEntityAsString(entity));
                bufferedWriter.newLine();
            }
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    /**
     * private method that saves a given entity into file
     * @param entity - Entity
     */
    private void appendData(E entity){
        Path path = Paths.get(filename);
        try(BufferedWriter bufferedWriter = Files.newBufferedWriter(path, StandardOpenOption.APPEND)){
            bufferedWriter.write(createEntityAsString(entity));
            bufferedWriter.newLine();
        }
        catch(IOException exception){
            System.out.println("Reading error!\n");
        }
    }


    @Override
    public E save(E entity){
        E returnValue = super.save(entity);
        if(returnValue == null)
            appendData(entity);
        return returnValue;
    }

    @Override
    public E remove(ID id){
        E returnEntity = super.remove(id);
        if(returnEntity != null)
            writeData();
        return returnEntity;
    }

    /**
     * abstract method that creates a new entity from a list of String
     * @param attributes - List
     * @return - new Entity
     */
    public abstract E extractEntity(List<String> attributes);

    /**
     * method that creates a String representation of the given entity(of type E)
     * @param entity - type E
     * @return - String
     */
    protected abstract String createEntityAsString(E entity);
}
