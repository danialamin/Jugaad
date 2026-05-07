package database;

import state.GameState;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class XMLPersistenceHandler implements PersistenceHandler {

    private static final String FILE_NAME = "savegame.xml";

    @Override
    public boolean save(GameState state) {
        try (XMLEncoder encoder = new XMLEncoder(new BufferedOutputStream(new FileOutputStream(FILE_NAME)))) {
            encoder.writeObject(state);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public GameState load(int saveId) {
        try (XMLDecoder decoder = new XMLDecoder(new BufferedInputStream(new FileInputStream(FILE_NAME)))) {
            return (GameState) decoder.readObject();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
